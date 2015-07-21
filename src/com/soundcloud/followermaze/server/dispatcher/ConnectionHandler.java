package com.soundcloud.followermaze.server.dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;

abstract class ConnectionHandler implements Runnable {

  private static final Logger logger = LogManager.getLogger( ConnectionHandler.class );

  /** Holds ClientSocket from this incoming data is read / written */
  protected final SocketChannel clientSocket;

  private int bytesReadTotal = 0;

  abstract public int getMaxBufferSize();

  abstract void processMessage( final ByteBuffer message ) throws Exception;

  public ConnectionHandler( final SocketChannel clientSocket ) {
    this.clientSocket = clientSocket;
  }

  protected int getResultBufferLengthInByte() {
    return ConfigService.INSTANCE.getResultBufferLengthInByte();
  }

  protected ByteBuffer readMessage() throws Exception {

    logger.entry();

    // use implementation dependent sizes to reduce expensive system calls for allocation
    final int maxBufferSize = getMaxBufferSize();
    final int resultBufferSize = getResultBufferLengthInByte();

    int lineBreak = 10;
    ByteBuffer resultBuffer = ByteBuffer.allocate( resultBufferSize );
    ByteBuffer tempBuffer = ByteBuffer.allocate( maxBufferSize );

    try {
      while ( true ) {

        int bytesRead = clientSocket.read( tempBuffer );
        bytesReadTotal += bytesRead;
        if ( bytesRead == -1 ) {
          logger.trace( "No more bytes read from socket." );
          break;
        }

        // flip Buffer before reading
        tempBuffer.flip();

        for ( int i = 0; i < bytesRead; i++ ) {
          final byte curByte = tempBuffer.get( i );
          resultBuffer.put( curByte );

          // this handles additional linebreaks
          if ( curByte == lineBreak ) {
            processMessage( resultBuffer );
            // resultBuffer.compact();
            resultBuffer.clear();
            // resultBuffer = ByteBuffer.allocate( resultBufferSize );
          }
        }
        // tempBuffer.compact();
        // tempBuffer = ByteBuffer.allocate( maxBufferSize );
        tempBuffer.clear();
      }
    } catch ( Exception ex ) {
      throw ex;
    } finally {
      try {
        logger.trace( "Read " + bytesReadTotal + " from Socket." );
        clientSocket.close();
      } catch ( Exception ex ) {
        throw ex;
      }
    }
    logger.exit( resultBuffer );
    return resultBuffer;
  }

  @Override
  public void run() {
    logger.entry();

    try {
      readMessage();
    } catch ( Exception e ) {
      logger.error( "Error reading from socket!", e );
    } finally {
      try {
        clientSocket.close();
      } catch ( IOException e ) {
        logger.error( "Error closing socket!", e );
      }
    }
    logger.exit();
  }

  /**
   * Method creates a copy of the pasted bytebuffer
   * 
   * @param original
   *          ByteBuffer which should be copied
   * @return
   */
  protected static ByteBuffer clone( ByteBuffer original ) {
    ByteBuffer clone = ByteBuffer.allocate( original.capacity() );
    original.rewind();
    clone.put( original );
    original.rewind();
    clone.flip();
    return clone;
  }

}
