package com.soundcloud.followermaze.dispatcher;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.controller.EventConnectionHandler;

public class EventDispatcher extends BaseDispatcher {

  private static final Logger logger = LogManager.getLogger( EventDispatcher.class );

  public EventDispatcher( final int port ) {
    super( port );
  }

  @Override
  public void run() {
    logger.entry();
    // use single thread executor, there´s only one client connecting to the server
    final ExecutorService executorService = Executors.newSingleThreadExecutor();

    try {
      logger.info( "EventDispatcher is waiting for incoming connections..." );
      while ( true ) {
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
}