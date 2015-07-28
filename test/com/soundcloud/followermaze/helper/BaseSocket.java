package com.soundcloud.followermaze.helper;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class for socket based communication. Connects to a server on a specified host.
 */
public abstract class BaseSocket implements Runnable {

  /** Logger */
  protected static final Logger logger = LogManager.getLogger( BaseSocket.class );

  /** Socket channel which is used for communication with the server */
  protected SocketChannel clientSocket = null;

  /**
   * Connects to a server on the given port
   * 
   * @param port
   *          Port on which the connection with the server should be established
   */
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
   * Disconnects the client by closing the socket.
   */
  public void disconnect() {
    try {
      clientSocket.close();
    } catch ( IOException e ) {
      logger.error( "Error closing socket connection!", e );
    }
  }

}
