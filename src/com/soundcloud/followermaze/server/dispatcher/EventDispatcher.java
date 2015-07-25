package com.soundcloud.followermaze.server.dispatcher;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.service.EventHandlerService;

/**
 * 
 * Instance of this class waits for incoming connections on the event source port (default 9090). It uses Java NIO configured to be non-blocking. If a client connects on the port, a new
 * EventConnectionHandler is created that gets the socket channel instance created by the socket.accept(). The EventConnectionHandler is passed into an executor service which processes it decoupled
 * from the listening loop.
 */
public class EventDispatcher extends BaseDispatcher {

  private static final Logger logger = LogManager.getLogger( EventDispatcher.class );

  /**
   * @param port
   *          Port in which the socket waits for incoming connections
   */
  EventDispatcher( final int port, final CountDownLatch readyLatch ) {
    super( port, readyLatch );
  }

  /**
   * Method contains the main listening loop for incoming client connections. If a client connection is received, the method creates an instance of EventConnectionHandler and passes it into an
   * executor service for concurrent processing.
   */
  @Override
  public void run() {
    logger.entry();

    // use single thread executor as there is only one client connecting on this port
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    try {
      logger.info( "EventDispatcher is waiting for incoming connections..." );
      // server is ready for incoming connections
      readyLatch.countDown();
      while ( running ) {
        final SocketChannel clientSocket = serverSocket.accept();
        if ( clientSocket != null ) {
          logger.debug( "Submitting new ConnectionHandler Task" );
          executorService.submit( new EventConnectionHandler( clientSocket ) );
        }
      }
    } catch ( Exception e ) {
      logger.error( "Error during socket communication.", e );
    } finally {
      try {
        cleanUp();
        logger.info( "Closing sockets..." );
        serverSocket.close();
        logger.info( "Closing sockets...completed" );
        logger.info( "Shutting down executor service..." );
        executorService.shutdown();
        logger.info( "Shutting down executor service...completed" );
      } catch ( IOException e ) {
        logger.error( "Error during component shutdown.", e );
      }
    }
    logger.exit();
  }

  @Override
  void cleanUp() {
    // shut down EventHandlerService
    EventHandlerService.INSTANCE.shutdown();
  }
}