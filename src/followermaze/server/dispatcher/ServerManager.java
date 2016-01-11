package followermaze.server.dispatcher;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import followermaze.server.service.EventHandlerService;
import followermaze.server.service.UserRegistryService;

/**
 * Class handles the start up and shutdown of EventDispatcher and UserClientDispatcher Servers
 *
 */
public class ServerManager {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( ServerManager.class );

  /** Thread that runs the EventDispatcher */
  private Thread eventDispatcherThread = null;

  /** Thread that runs the UserClientDispatcher */
  private Thread userClientDispatcherThread = null;

  /** Runnable instance of UserClientDispatcher */
  private UserClientDispatcher userClientDispatcher = null;

  /** Runnable instance of EventDispatcher */
  private EventDispatcher eventDispatcher = null;

  /**
   * Method starts an EventDispatcher and an UserClientDispatcher
   * 
   * @param eventDispatcherPort
   *          Port on which the EventDispatcher waits for EventSources to connect
   * @param userClientDispatcherPort
   *          Port on which the UserClientDispatcher waits for clients to connect
   */
  public void startUpServers( int eventDispatcherPort, int userClientDispatcherPort ) {

    boolean startedEventDispatcher = false;
    final CountDownLatch readySignalEventDispatcher = new CountDownLatch( 1 );
    eventDispatcher = new EventDispatcher( eventDispatcherPort, readySignalEventDispatcher );
    eventDispatcherThread = new Thread( eventDispatcher );
    eventDispatcherThread.start();
    try {
      readySignalEventDispatcher.await();
      startedEventDispatcher = true;
    } catch ( InterruptedException e ) {
      logger.error( "Error wail starting up Event Dispatcher!", e );
    }

    if ( startedEventDispatcher ) {
      final CountDownLatch readySignalUserDispatcher = new CountDownLatch( 1 );
      userClientDispatcher = new UserClientDispatcher( userClientDispatcherPort, readySignalUserDispatcher );
      userClientDispatcherThread = new Thread( userClientDispatcher );
      userClientDispatcherThread.start();

      try {
        readySignalUserDispatcher.await();
      } catch ( InterruptedException e ) {
        logger.error( "Error while starting up User Client Dispatcher!", e );
      }
    }
  }

  /**
   * Starts up only the ClientDispatcher
   * 
   * @param userClientDispatcherPort
   *          Port on which the server listens for incoming client connections
   */
  public void startUpUserClientDispatcher( int userClientDispatcherPort ) {

    final CountDownLatch readySignalUserDispatcher = new CountDownLatch( 1 );
    userClientDispatcher = new UserClientDispatcher( userClientDispatcherPort, readySignalUserDispatcher );
    userClientDispatcherThread = new Thread( userClientDispatcher );
    userClientDispatcherThread.start();

    try {
      readySignalUserDispatcher.await();
      logger.info( "User Client Dispatcher is waiting for incoming connections..." );
    } catch ( InterruptedException e ) {
      logger.error( "Error while starting up User Client Dispatcher!", e );
    }

  }

  /**
   * Method stops the servers by shutting down the threads
   */
  public void stopServers() {
    EventHandlerService.INSTANCE.reset();
    UserRegistryService.INSTANCE.reset();

    if ( eventDispatcher != null ) {
      eventDispatcher.shutdown();
    }
    if ( userClientDispatcher != null ) {
      userClientDispatcher.shutdown();
    }
    if ( eventDispatcherThread != null ) {
      eventDispatcherThread.interrupt();
    }
    if ( userClientDispatcherThread != null ) {
      userClientDispatcherThread.interrupt();
    }
  }

  /**
   * Resets the EventHandlerService and the UserRegistryService to their startup state
   */
  public void resetServers() {
    EventHandlerService.INSTANCE.reset();
    UserRegistryService.INSTANCE.reset();
  }
}
