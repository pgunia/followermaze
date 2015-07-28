package com.soundcloud.followermaze.server.dispatcher;

import java.net.InetSocketAddress;
import java.nio.channels.ServerSocketChannel;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class of the EventDispatcher and the UserClientDispatcher classes. This class creates the ServerSocketChannel on the passed in port and configures the channel for further processing
 * in its subclasses.
 *
 */
public abstract class BaseDispatcher implements Runnable {

  private static final Logger logger = LogManager.getLogger( BaseDispatcher.class );

  /** ServerSocketChannel which is used for listening for incoming connections */
  protected ServerSocketChannel serverSocket;

  /** Latch is used to signal when the server is ready to accept incoming connections */
  protected final CountDownLatch readyLatch;

  /** Flag controls the main loop */
  protected volatile boolean running = true;

  /** Method is called before thread shutdown and cleans up the components used by the subclass */
  abstract void cleanUp();

  /**
   * Constructor creates the ServerSocketChannel and binds it to the passed in port
   * 
   * @param port
   *          Port on which the socket channel is waiting for incoming connections
   * @param readySignal
   *          CountDownLatch which is used to synchronize processing until the BaseDispatcher instance is ready to accept connections
   */
  public BaseDispatcher( int port, final CountDownLatch readySignal ) {
    super();
    logger.entry( port, readySignal );
    this.readyLatch = readySignal;
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

  /** Sets the main loop control flag to false */
  public void shutdown() {
    running = false;
  }
}
