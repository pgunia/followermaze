package com.soundcloud.followermaze.server.dispatcher;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;
import com.soundcloud.followermaze.server.service.EventHandlerService;
import com.soundlcoud.followermaze.server.model.event.EventFactory;

public class EventConnectionHandler extends ConnectionHandler {

  private static final Logger logger = LogManager.getLogger( EventConnectionHandler.class );

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
    logger.info( "Processing message: " + messageStr );

    EventHandlerService.INSTANCE.addEvent( EventFactory.createEvent( messageStr ) );
    logger.exit();
  }

  @Override
  public int getMaxBufferSize() {
    return ConfigService.INSTANCE.getMaxBufferLengthEventSocketInByte();
  }
}
