package com.soundcloud.followermaze.server.model.client;

import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent a connected user. Instances of class are immutable.
 *
 */
public class Client {

  private static final Logger logger = LogManager.getLogger( Client.class );

  /** ID of current client */
  private final Integer id;

  /** Socketchannel over which the client is connected */
  private final SocketChannel clientSocket;

  private final Object lock = new Object();

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
   */
  public void notify( String messageStr ) {

    // lock ensures that there will be no race conditions during notification can happen
    synchronized ( lock ) {
      logger.entry( messageStr );
      try {
        logger.info( "Sending message " + messageStr + " to " + id );
        int bytesWritten = this.clientSocket.write( Charset.forName( "UTF-8" ).encode( messageStr ) );
        logger.info( "Sent " + bytesWritten + " bytes to client!" );
      } catch ( Exception e ) {
        logger.error( "Error notifying client.", e );
        UserRegistryService.INSTANCE.removeClient( this );
      }
      logger.exit();
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
   * @return
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
