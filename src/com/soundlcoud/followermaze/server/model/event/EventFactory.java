package com.soundlcoud.followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventFactory {

  private static final Logger logger = LogManager.getLogger( EventFactory.class );

  private static final String SEPARATOR = "\\|";

  public static Event createFollowEvent( final String eventStr ) throws Exception {
    logger.entry( eventStr );

    final String[] component = eventStr.trim().split( SEPARATOR );

    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + eventStr + " does not describe a valid follow event." );
    }
    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new FollowEvent( sequenceNumber, clientIdFrom, clientIdTo, eventStr );
    logger.exit( result );
    return result;
  }

  public static Event createUnFollowEvent( final String eventStr ) throws Exception {
    logger.entry( eventStr );
    final String[] component = eventStr.trim().split( SEPARATOR );
    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + eventStr + " does not describe a valid unfollow event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new UnfollowEvent( sequenceNumber, clientIdFrom, clientIdTo );
    logger.exit( result );
    return result;
  }

  public static Event createBroadcastEvent( final String eventStr ) throws Exception {
    logger.entry( eventStr );
    final String[] component = eventStr.trim().split( SEPARATOR );

    if ( component.length != 2 ) {
      throw new IllegalArgumentException( "Event " + eventStr + " does not describe a valid broadcast event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final Event result = new BroadcastEvent( sequenceNumber, eventStr );
    logger.exit( result );
    return result;
  }

  public static Event createPrivateMessageEvent( final String eventStr ) throws Exception {
    logger.entry( eventStr );
    final String[] component = eventStr.trim().split( SEPARATOR );

    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + eventStr + " does not describe a valid private message event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new PrivateMsgEvent( sequenceNumber, clientIdFrom, clientIdTo, eventStr );
    logger.exit( result );
    return result;
  }

  public static Event createStatusUpdateEvent( final String eventStr ) throws Exception {
    logger.entry( eventStr );
    final String[] component = eventStr.trim().split( SEPARATOR );

    if ( component.length != 3 ) {
      throw new IllegalArgumentException( "Event " + eventStr + " does not describe a valid status update event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final Event result = new StatusUpdateEvent( sequenceNumber, clientIdFrom, eventStr );
    logger.exit( result );
    return result;
  }

  private static Integer extractSequenceNumber( final String sequenceComponent ) throws IllegalArgumentException {
    logger.entry( sequenceComponent );
    Integer sequenceNumber = -1;
    try {
      sequenceNumber = Integer.valueOf( sequenceComponent );
    } catch ( NumberFormatException ex ) {
      throw new IllegalArgumentException( "Illegal sequence number: " + sequenceComponent );
    }
    logger.exit( sequenceNumber );
    return sequenceNumber;
  }

  private static Integer extractClientId( final String clientId ) throws IllegalArgumentException {
    logger.entry( clientId );
    Integer id = -1;
    try {
      id = Integer.valueOf( clientId );
    } catch ( NumberFormatException ex ) {
      throw new IllegalArgumentException( "Illegal client id: " + clientId );
    }
    logger.exit( id );
    return id;
  }
}
