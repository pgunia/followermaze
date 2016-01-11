package followermaze.helper;

import java.io.IOException;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.util.List;

/**
 * 
 * Instances of this class are used to simulate event sources that connect to the server on the event port (9090). The instance establishes the connection using procvided implementation of its base
 * class.
 */
public class EventSocket extends BaseSocket {

  /** List holds all messages to be sent to the Server */
  private final List<String> messagesToSend;

  /**
   * Constructs an EventSocket
   * 
   * @param messageList
   *          Messages to be sent to the server
   * @param port
   *          Port on which to connect to the server
   */
  public EventSocket( final List<String> messageList, int port ) {
    super( port );
    messagesToSend = messageList;
  }

  /**
   * Method sends the messages from the passed in message list via the socket channel to the server.
   */
  @Override
  public void run() {
    logger.entry();

    // send all messages in the list to the server
    for ( final String message : messagesToSend ) {
      logger.debug( "Sending message " + message );
      final CharBuffer buffer = CharBuffer.wrap( message );
      try {
        while ( buffer.hasRemaining() ) {
          clientSocket.write( Charset.forName( "UTF-8" ).encode( buffer ) );
        }
      } catch ( Exception e ) {
        logger.error( "Error while sending data! " + e );
      }
    }
    try {
      logger.info( "Closing event source connection to server!" );
      clientSocket.close();
    } catch ( IOException e ) {
      logger.error( "Error while closing socket! " + e );
    }
    logger.exit();
  }

}
