package followermaze.server.dispatcher;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import followermaze.server.config.ConfigService;

/**
 * ConnectionHandler is the abstract base class for the UserClientConnectionHandler and the EventConnectionHandler. Implements the functionality to read bytes from a SocketChannel into a buffer. These
 * buffers are then processed by subclass speicifc methods.
 *
 */
abstract class ConnectionHandler implements Runnable {

  private static final Logger logger = LogManager.getLogger( ConnectionHandler.class );

  /** Holds ClientSocket from this incoming data is read / written */
  protected final SocketChannel clientSocket;

  /** Linebreak */
  protected static int MESSAGE_TERMINATOR = 10;

  /** Enconding of the incoming and outgoing byte data */
  protected static String ENCODING = "UTF-8";

  /**
   * Returns the subclass specific size of the tempbuffer which reads data from a SocketChannel.
   * 
   * @return Number of bytes used for allocation of the bytebuffer
   */
  abstract public int getMaxBufferSize();

  /**
   * Read messages from the client sockets are passed to this method to be further processed inside the specific subclasses.
   * 
   * @param message
   *          ByteBuffer containing a message
   * @throws Exception
   */
  abstract void processMessage( final ByteBuffer message ) throws Exception;

  public ConnectionHandler( final SocketChannel clientSocket ) {
    this.clientSocket = clientSocket;
  }

  /**
   * Returns the length of the result buffer which is used to hold the content of a single message
   * 
   * @return Resultbuffer size in byte
   */
  protected int getResultBufferLengthInByte() {
    return ConfigService.INSTANCE.getResultBufferLengthInByte();
  }

  /**
   * Method reads data from a connected SocketChannel until no more bytes are received. The bytes are read into a a bytebuffer which is then copied byte-per-byte into a result bytebuffer to find the
   * message terminator byte. The content of the result buffer is further processed by subclass specific implementations of processMessage()
   * 
   * @throws Exception
   *           IOException
   */
  protected void readMessage() throws Exception {

    logger.entry();

    // use implementation dependent sizes to reduce expensive system calls for allocation
    final int maxBufferSize = getMaxBufferSize();
    final int resultBufferSize = getResultBufferLengthInByte();

    int bytesReadTotal = 0;

    ByteBuffer resultBuffer = ByteBuffer.allocate( resultBufferSize );
    ByteBuffer tempBuffer = ByteBuffer.allocate( maxBufferSize );

    boolean running = true;
    try {
      while ( running ) {

        int bytesRead = clientSocket.read( tempBuffer );
        bytesReadTotal += bytesRead;
        if ( bytesRead == -1 ) {
          logger.trace( "No more bytes read from socket." );
          running = false;
        } else {
          // flip Buffer before reading
          tempBuffer.flip();

          for ( int i = 0; i < bytesRead; i++ ) {
            final byte curByte = tempBuffer.get( i );
            resultBuffer.put( curByte );

            // message completely read?
            if ( curByte == MESSAGE_TERMINATOR ) {
              processMessage( resultBuffer );
              resultBuffer.clear();
            }
          }
          tempBuffer.clear();
        }
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

  }

  /**
   * Run method automatically called by the executor service. For every connection established to the server, an instance of either UserClientConnectionHandler of EventConnectionHandler is created and
   * added to the job queue of an executor service.
   */
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
}
