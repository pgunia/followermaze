package com.soundcloud.followermaze.dispatcher;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseDispatcher implements Runnable, Dispatcher {

  private static final Logger logger = LogManager.getLogger( BaseDispatcher.class );

  protected ServerSocketChannel serverSocket = null;

  public BaseDispatcher( int port ) {
    super();
    logger.entry( port );
    try {
      // use NIO ServerSockets
      serverSocket = ServerSocketChannel.open();
      serverSocket.socket().bind( new InetSocketAddress( port ) );
      serverSocket.configureBlocking( false );
    } catch ( Exception ex ) {
      logger.error( "Error during connection creation! " + ex );
    }
    logger.exit();
  }

  /**
   * Method return the socket address of the current socket
   * 
   * @return
   */
  public SocketAddress getSocketAdress() {
    try {
      return serverSocket.getLocalAddress();
    } catch ( Exception ex ) {
      logger.error( "Error retrieving socket address!", ex );
      return null;
    }
  }
}
