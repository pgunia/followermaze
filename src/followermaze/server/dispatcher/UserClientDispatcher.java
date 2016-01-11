package followermaze.server.dispatcher;

import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import followermaze.server.config.ConfigService;
import followermaze.server.service.UserRegistryService;

/**
 * 
 * Instance of this class processes messages that are retrieved by clients connected on the user client port (default 9099)
 * 
 */
class UserClientDispatcher extends BaseDispatcher {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( UserClientDispatcher.class );

  /**
   * Constructs a UserClientDispatcher
   * 
   * @param port
   *          Port on which to listen for incoming connections
   * @param readyLatch
   *          CountDownLatch used for synchronization
   */
  public UserClientDispatcher( int port, final CountDownLatch readyLatch ) {
    super( port, readyLatch );
  }

  /**
   * Main processing loop, waits for incoming connections on provided port.
   */
  @Override
  public void run() {

    final int corePoolSize = 100;
    final int keepAliveTime = 1;

    // use ThreadPoolExecutor to enable loaddepending generation and starvation of threads
    final ExecutorService executorService = new ThreadPoolExecutor( corePoolSize, ConfigService.INSTANCE.getMaxThreadsUserClientDispatcher(), keepAliveTime, TimeUnit.MINUTES,
        new LinkedBlockingQueue<Runnable>() );

    try {
      logger.info( "User Client Dispatcher is waiting for incoming connections..." );
      // server is ready for incoming connections
      readyLatch.countDown();
      while ( running ) {
        final SocketChannel clientSocket = serverSocket.accept();
        if ( clientSocket != null ) {
          executorService.submit( new UserClientConnectionHandler( clientSocket ) );
        }
      }
    } catch ( Exception e ) {
      logger.error( "Error during socket communication.", e );
    } finally {
      try {
        cleanUp();
        logger.info( "Closing Server Socket..." );
        serverSocket.close();
        logger.info( "Closing Server Socket...completed" );
        logger.info( "Shutting down executor service..." );
        executorService.shutdownNow();
        logger.info( "Shutting down executor service...completed" );
      } catch ( Exception e ) {
        logger.error( "Error during component shutdown.", e );
      }
    }
  }

  @Override
  void cleanUp() {
    UserRegistryService.INSTANCE.reset();
  }
}
