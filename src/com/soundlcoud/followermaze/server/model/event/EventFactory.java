package com.soundlcoud.followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class EventFactory {

  private static final Logger logger = LogManager.getLogger( EventFactory.class );

  private static final String SEPARATOR = "\\|";

  /**
   * Creates an event based on the input event message
   * 
   * @param eventStr
   *          Received event message which is parsed and returns an Event instance
   * @return Event instance representing the event to be processed
   */
  public static Event createEvent( final String eventStr ) {
    logger.entry( eventStr );

    // check for type based on String parsing, use trimmed component to remove linebreak at the end
    final String[] components = eventStr.trim().split( SEPARATOR );
    final String eventTypeIdentifier = components[1];
    Event result = null;
    try {
      switch ( eventTypeIdentifier ) {
        case "F":
          result = createFollowEvent( components, eventStr );
          break;
        case "U":
          result = createUnFollowEvent( components, eventStr );
          break;
        case "B":
          result = createBroadcastEvent( components, eventStr );
          break;
        case "P":
          result = createPrivateMessageEvent( components, eventStr );
          break;
        case "S":
          result = createStatusUpdateEvent( components, eventStr );
          break;
        default:
          throw new IllegalStateException( "Identifier: " + eventStr + " does not describe a valid event!" );
      }
    } catch ( Exception ex ) {
      logger.error( "Error during event creation", ex );
    }
    logger.exit( result );
    return result;

  }

  private static Event createFollowEvent( final String[] component, final String message ) throws Exception {
    logger.entry( component, message );

    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + message + " does not describe a valid follow event." );
    }
    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new FollowEvent( sequenceNumber, clientIdFrom, clientIdTo, message );
    logger.exit( result );
    return result;
  }

  private static Event createUnFollowEvent( final String[] component, final String message ) throws Exception {
    logger.entry( component, message );
    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + message + " does not describe a valid unfollow event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new UnfollowEvent( sequenceNumber, clientIdFrom, clientIdTo );
    logger.exit( result );
    return result;
  }

  private static Event createBroadcastEvent( final String[] component, final String message ) throws Exception {
    logger.entry( component, message );

    if ( component.length != 2 ) {
      throw new IllegalArgumentException( "Event " + message + " does not describe a valid broadcast event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final Event result = new BroadcastEvent( sequenceNumber, message );
    logger.exit( result );
    return result;
  }

  private static Event createPrivateMessageEvent( final String[] component, final String message ) throws Exception {
    logger.entry( component, message );

    if ( component.length != 4 ) {
      throw new IllegalArgumentException( "Event " + message + " does not describe a valid private message event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final int clientIdTo = extractClientId( component[3] );
    final Event result = new PrivateMsgEvent( sequenceNumber, clientIdFrom, clientIdTo, message );
    logger.exit( result );
    return result;
  }

  private static Event createStatusUpdateEvent( final String[] component, final String message ) throws Exception {
    logger.entry( component, message );

    if ( component.length != 3 ) {
      throw new IllegalArgumentException( "Event " + message + " does not describe a valid status update event." );
    }

    final int sequenceNumber = extractSequenceNumber( component[0] );
    final int clientIdFrom = extractClientId( component[2] );
    final Event result = new StatusUpdateEvent( sequenceNumber, clientIdFrom, message );
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
