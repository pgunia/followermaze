package followermaze.server.dispatcher;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import followermaze.server.config.ConfigService;
import followermaze.server.model.client.Client;
import followermaze.server.service.UserRegistryService;

/**
 * Instances of this class handle connections from user clients to the server. Every client sends a message that contains its client id, this message is received and parsed to register the client in
 * the UserRegistryService
 *
 */
public class UserClientConnectionHandler extends ConnectionHandler {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( UserClientConnectionHandler.class );

  /**
   * Constructs a UserClientConnectionHandler
   * 
   * @param clientSocket
   *          Socket channel over which a client is connected to the server
   */
  public UserClientConnectionHandler( SocketChannel clientSocket ) {
    super( clientSocket );
  }

  /**
   * Method extracts the user id from the passed in message and registers the user at the UserRegistryService
   */
  @Override
  void processMessage( ByteBuffer message ) throws UnsupportedEncodingException, IllegalArgumentException {
    logger.entry( message );

    final String messageStr = new String( message.array(), ENCODING ).trim();
    Integer userId = null;
    try {
      userId = Integer.valueOf( messageStr );
    } catch ( NumberFormatException ex ) {
      throw new IllegalArgumentException( "Message " + messageStr + " does not contain a valid User ID!" );
    }

    UserRegistryService.INSTANCE.registerClient( new Client( userId, clientSocket ) );
    logger.exit();
  }

  @Override
  public int getMaxBufferSize() {
    return ConfigService.INSTANCE.getMaxBufferLengthClientSocketInByte();
  }
}
