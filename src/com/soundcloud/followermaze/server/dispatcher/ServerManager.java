package com.soundcloud.followermaze.server.dispatcher;

import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.endtoend.ClientManager;
import com.soundcloud.followermaze.server.service.EventHandlerService;
import com.soundcloud.followermaze.server.service.UserRegistryService;

/**
 * Class handles the start up and shutdown of EventDispatcher and UserClientDispatcher Servers
 *
 */
public class ServerManager {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( ClientManager.class );

  private Thread eventDispatcherThread = null;

  private Thread userClientDispatcherThread = null;

  private UserClientDispatcher userClientDispatcher = null;

  private EventDispatcher eventDispatcher = null;

  /** Method starts an EventDispatcher and an UserClientDispatcher */
  public void startUpServers( int eventDispatcherPort, int userClientDispatcherPort ) {

    boolean startedEventDispatcher = false;
    final CountDownLatch readySignalEventDispatcher = new CountDownLatch( 1 );
    eventDispatcher = new EventDispatcher( eventDispatcherPort, readySignalEventDispatcher );
    eventDispatcherThread = new Thread( eventDispatcher );
    eventDispatcherThread.start();
    try {
      readySignalEventDispatcher.await();
      startedEventDispatcher = true;
      logger.info( "Event Dispatcher is waiting for incoming connections..." );
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
        logger.info( "User Client Dispatcher is waiting for incoming connections..." );
      } catch ( InterruptedException e ) {
        logger.error( "Error while starting up User Client Dispatcher!", e );
      }
    }
  }

  /**
   * Method stops the servers by shutting down the threads
   */
  public void stopServers() {
    EventHandlerService.INSTANCE.reset();
    UserRegistryService.INSTANCE.reset();
    eventDispatcher.shutdown();
    userClientDispatcher.shutdown();
    eventDispatcherThread.interrupt();
    userClientDispatcherThread.interrupt();
  }

  /**
   * Resets the EventHandlerService and the UserRegistryService to their startup state
   */
  public void resetServers() {
    EventHandlerService.INSTANCE.reset();
    UserRegistryService.INSTANCE.reset();
  }
}
