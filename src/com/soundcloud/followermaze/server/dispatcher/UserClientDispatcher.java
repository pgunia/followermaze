package com.soundcloud.followermaze.server.dispatcher;

import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;
import com.soundcloud.followermaze.server.service.UserRegistryService;

/**
 * 
 * Instance of this class processes messages that are retrieved by clients connected on the user client port (default 9099)
 * 
 */
class UserClientDispatcher extends BaseDispatcher {

  private static final Logger logger = LogManager.getLogger( UserClientDispatcher.class );

  public UserClientDispatcher( int port, final CountDownLatch readyLatch ) {
    super( port, readyLatch );
  }

  @Override
  public void run() {

    final int corePoolSize = 50;
    final int keepAliveTime = 5000;

    // use ThreadPoolExecutor to enable loaddepending generation and starvation of threads
    final ExecutorService executorService = new ThreadPoolExecutor( corePoolSize, ConfigService.INSTANCE.getMaxThreadsUserClientDispatcher(), keepAliveTime, TimeUnit.MILLISECONDS,
        new LinkedBlockingQueue<Runnable>() );

    try {
      logger.info( "UserClientDispatcher is waiting for incoming connections..." );
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
