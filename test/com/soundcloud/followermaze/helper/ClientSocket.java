package com.soundcloud.followermaze.helper;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class ClientSocket extends BaseSocket {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( ClientSocket.class );

  /** Maximum wait time in seconds until the clients disconnect from the server if they don´t receive messages, uses same timeout as test soundcloud testclient */
  private static int READ_TIMEOUT = 5000;

  /** Message terminator */
  private static String MESSAGE_TERMINATOR = "\r\n";

  /** Encoding used in communication with the server */
  private static String ENCODING = "UTF-8";

  /** Uses count down latch to pause test processing until all clients are accepting connections */
  private final CountDownLatch connectedLatch;

  /** Uses count down latches to wait with further processing until all clients disconnected from the server */
  private final CountDownLatch timeoutLatch;

  /** ID of the client as it is sent */
  private final int userId;

  public ClientSocket( final int id, final int port, final CountDownLatch connectedLatch, final CountDownLatch timeoutLatch ) {
    super( port );
    this.userId = id;
    this.connectedLatch = connectedLatch;
    this.timeoutLatch = timeoutLatch;
  }

  @Override
  public void run() {
    logger.entry();

    // reister at server
    final boolean registered = registerAtServer();
    if ( registered ) {

      // decrement latch
      connectedLatch.countDown();
      final String result = retrieveMessages();
      if ( result.length() > 0 ) {
        CoordinatorService.INSTANCE.addRetrievedMessages( userId, result );
      }
    }
  }

  /**
   * Method sends the userid to the server to registr for message retrievals
   * 
   * @throws IOException
   */
  private boolean registerAtServer() {
    logger.entry();
    logger.debug( "Register User at server" );
    try {
      final CharBuffer buffer = CharBuffer.wrap( String.valueOf( userId ) + MESSAGE_TERMINATOR );

      while ( buffer.hasRemaining() ) {
        clientSocket.write( Charset.forName( ENCODING ).encode( buffer ) );
      }
    } catch ( Exception e ) {
      logger.error( "Error during registration at server!", e );
      logger.exit( false );
      return false;
    }
    logger.debug( "Registered " + userId );
    logger.exit( true );
    return true;
  }

  /**
   * Method waits for the server to forward the notifications and reads them from the socket channel
   * 
   * @param curKey
   * 
   * @return
   * 
   * @throws InterruptedException
   * @throws IOException
   */
  private String retrieveMessages() {
    logger.entry();
    final StringBuilder messageBuffer = new StringBuilder( "" );
    ByteBuffer tempBuffer = ByteBuffer.allocate( 64 );
    int bytesRead = 0;
    int count = 0;

    try {
      // wrap clientSocket in InputStream to make timeout work
      clientSocket.socket().setSoTimeout( READ_TIMEOUT );
      final InputStream inStream = clientSocket.socket().getInputStream();
      final ReadableByteChannel wrappedChannel = Channels.newChannel( inStream );
      while ( true ) {
        bytesRead = wrappedChannel.read( tempBuffer );
        if ( bytesRead < 0 ) {
          break;
        }
        count += bytesRead;

        // flip the buffer to start reading
        tempBuffer.flip();
        messageBuffer.append( Charset.forName( ENCODING ).decode( tempBuffer ) );
        tempBuffer.compact();
      }
    } catch ( SocketTimeoutException ex ) {
      logger.info( "No more bytes read from the server, closing connection!" );
      logger.info( "Read " + count + " bytes from server" );
    } catch ( IOException ex ) {
      logger.error( "Error during reading from socket!", ex );
    } finally {
      try {
        timeoutLatch.countDown();
        clientSocket.close();
      } catch ( IOException e ) {
        logger.error( "Error during socket close", e );
      }
    }
    logger.exit();
    return messageBuffer.toString();
  }

  /**
   * 
   * @return Id of connected user
   */
  public int getUserId() {
    return userId;
  }

}
