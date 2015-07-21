package com.soundcloud.followermaze.server.dispatcher;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;
import com.soundcloud.followermaze.server.service.EventHandlerService;
import com.soundlcoud.followermaze.server.model.event.Event;
import com.soundlcoud.followermaze.server.model.event.EventFactory;

public class EventConnectionHandler extends ConnectionHandler {

  private static final Logger logger = LogManager.getLogger( EventConnectionHandler.class );

  private static final String SEPARATOR = "\\|";

  public EventConnectionHandler( SocketChannel clientSocket ) {
    super( clientSocket );
  }

  @Override
  void processMessage( ByteBuffer message ) throws Exception {

    logger.entry( message );
    int numberOfBytesToRead = message.position();
    final byte[] byteBuffer = new byte[numberOfBytesToRead];

    message.flip();
    message.get( byteBuffer, 0, numberOfBytesToRead );

    final String messageStr = new String( byteBuffer, "UTF-8" );
    final String trimmedMessageStr = messageStr.trim();

    logger.info( "Processing message: " + messageStr );

    // check for type based on String parsing
    final String[] components = trimmedMessageStr.split( SEPARATOR );
    final String eventTypeIdentifier = components[1];
    Event result = null;
    switch ( eventTypeIdentifier ) {
      case "F":
        result = EventFactory.createFollowEvent( messageStr );
        break;
      case "U":
        result = EventFactory.createUnFollowEvent( messageStr );
        break;
      case "B":
        result = EventFactory.createBroadcastEvent( messageStr );
        break;
      case "P":
        result = EventFactory.createPrivateMessageEvent( messageStr );
        break;
      case "S":
        result = EventFactory.createStatusUpdateEvent( messageStr );
        break;
      default:
        throw new IllegalStateException( "Identifier: " + messageStr + " does not describe a valid event!" );
    }

    EventHandlerService.INSTANCE.addEvent( result );
    logger.exit();
  }

  @Override
  public int getMaxBufferSize() {
    return ConfigService.INSTANCE.getMaxBufferLengthEventSocketInByte();
  }
}
