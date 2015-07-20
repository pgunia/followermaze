package com.soundcloud.followermaze.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.charset.Charset;

public class ClientSocket extends BaseSocket {

  private static int READ_TIMEOUT = 2000;

  private final String userId;

  public ClientSocket( final String userId, final int port ) {
    super( port );

    this.userId = userId;
  }

  @Override
  public void run() {
    logger.entry();

    final boolean registered = registerAtServer();
    if ( registered ) {
      final String result = retrieveMessages();
      TestCoordinatorService.INSTANCE.addRetrievedMessages( userId.trim(), result );
    }
  }

  /**
   * Method sends the userid to the server to registr for message retrievals
   * 
   * @throws IOException
   */
  private boolean registerAtServer() {
    logger.entry();
    logger.info( "Register User at server" );
    try {
      final CharBuffer buffer = CharBuffer.wrap( userId );

      while ( buffer.hasRemaining() ) {
        clientSocket.write( Charset.forName( "UTF-8" ).encode( buffer ) );
      }
    } catch ( Exception e ) {
      logger.error( "Error during registration at server!", e );
      logger.exit( false );
      return false;
    }
    logger.info( "Registered " + userId.trim() );
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
        messageBuffer.append( Charset.forName( "UTF-8" ).decode( tempBuffer ) );
        tempBuffer.compact();
      }
    } catch ( SocketTimeoutException ex ) {
      logger.info( "No more bytes read from the server, closing connection!" );
      logger.info( "Read " + count + " bytes from server" );
    } catch ( IOException ex ) {
      logger.error( "Error during reading from socket!", ex );
    } finally {
      try {
        clientSocket.close();
      } catch ( IOException e ) {
        logger.error( "Error during socket close", e );
      }
    }
    logger.exit();
    return messageBuffer.toString();
  }
}
