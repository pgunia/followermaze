package com.soundcloud.followermaze.server.dispatcher;

import java.nio.channels.SocketChannel;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;

class UserClientDispatcher extends BaseDispatcher {

  private static final Logger logger = LogManager.getLogger( UserClientDispatcher.class );

  public UserClientDispatcher( int port, final CountDownLatch readyLatch ) {
    super( port, readyLatch );
  }

  @Override
  public void run() {

    final ExecutorService executorService = Executors.newFixedThreadPool( ConfigService.INSTANCE.getMaxThreadsUserClientDispatcher() );

    try {
      logger.info( "UserClientDispatcher is waiting for incoming connections..." );
      // server is ready for incoming connections
      readyLatch.countDown();
      while ( true ) {
        final SocketChannel clientSocket = serverSocket.accept();
        if ( clientSocket != null ) {
          executorService.submit( new UserClientConnectionHandler( clientSocket ) );
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
      } catch ( Exception e ) {
        logger.error( "Error during component shutdown.", e );
      }
    }
  }
}
