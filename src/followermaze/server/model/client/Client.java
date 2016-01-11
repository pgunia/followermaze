package followermaze.server.model.client;

import java.io.IOException;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent a connected user.
 *
 */
public class Client {

  private static final Logger logger = LogManager.getLogger( Client.class );

  /** Charset used for encoding */
  private static final String CHARSET = "UTF-8";

  /** ID of current client */
  private final Integer id;

  /** Socketchannel over which the client is connected */
  private final SocketChannel clientSocket;

  /**
   * 
   * @param id
   *          ID of current user
   * @param clientSocket
   *          Socket over which the current user is connected
   */
  public Client( int id, final SocketChannel clientSocket ) {
    this.id = id;
    this.clientSocket = clientSocket;
  }

  public Integer getId() {
    return id;
  }

  /**
   * Method sends message to current user
   * 
   * @param messageStr
   *          Message to be sent to the user
   * @return TODO
   */
  public int notify( String messageStr ) {

    logger.entry( messageStr );
    int bytesWritten = 0;
    try {
      logger.debug( "Sending message " + messageStr + " to " + id );
      bytesWritten = clientSocket.write( Charset.forName( CHARSET ).encode( messageStr ) );
      logger.debug( "Sent " + bytesWritten + " bytes to client!" );
    } catch ( Exception e ) {
      // Client is no longer connected, remove it from the userregistry
      logger.error( "Error notifying client.", e );
      UserRegistryService.INSTANCE.removeClient( this );
    }
    logger.exit( bytesWritten );
    return bytesWritten;
  }

  /**
   * Closes the socket connection to the user client
   */
  public void closeConnection() {
    try {
      if ( clientSocket != null ) {
        clientSocket.close();
      }
    } catch ( IOException e ) {
      logger.error( "Error closing sokcet to client " + getId(), e );
    }
  }

  /**
   * Only hash based on user id
   * 
   * @return Hashcode
   */
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + id;
    return result;
  }

  /**
   * Only compare based on user id
   * 
   * @param obj
   *          object to compare to
   * @return True, if the objects are equal, else false
   */
  @Override
  public boolean equals( Object obj ) {
    if ( this == obj )
      return true;
    if ( obj == null )
      return false;
    if ( getClass() != obj.getClass() )
      return false;
    Client other = (Client) obj;
    if ( id != other.id )
      return false;
    return true;
  }

}
