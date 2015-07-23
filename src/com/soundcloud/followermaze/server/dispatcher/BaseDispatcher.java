package com.soundcloud.followermaze.server.dispatcher;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class of the EventDispatcher and the UserClientDispatcher classes. This class creates the ServerSocketChannel on the passed in port and configures the channel for further processing
 * in its subclasses.
 *
 */
public abstract class BaseDispatcher implements Runnable, Dispatcher {

  private static final Logger logger = LogManager.getLogger( BaseDispatcher.class );

  /** ServerSocketChannel which is used for listening for incoming connections */
  protected ServerSocketChannel serverSocket = null;

  /**
   * Constructor creates the ServerSocketChannel and binds it to the passed in port
   * 
   * @param port
   *          Port on which the socket channel is waiting for incoming connections
   */
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
}
