package com.soundcloud.followermaze.server.endtoend;

import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class BaseSocket implements Runnable {

  protected static final Logger logger = LogManager.getLogger( BaseSocket.class );

  protected SocketChannel clientSocket = null;

  public BaseSocket( int port ) {
    super();
    logger.entry();
    try {
      clientSocket = SocketChannel.open();

      // connect on localhost loopback
      clientSocket.connect( new InetSocketAddress( InetAddress.getLoopbackAddress(), port ) );
    } catch ( Exception ex ) {
      logger.error( "Error during connection creation! " + ex );
    }
    logger.exit();
  }

  /**
   * Returns whether or not the current socket is connected to a server
   * 
   * @return
   */
  public boolean isConnected() {
    return clientSocket.isConnected();

  }

}