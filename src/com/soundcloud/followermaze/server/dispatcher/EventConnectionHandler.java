package com.soundcloud.followermaze.server.dispatcher;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.config.ConfigService;
import com.soundcloud.followermaze.server.service.EventHandlerService;
import com.soundlcoud.followermaze.server.model.event.EventFactory;

/**
 * 
 * Instance of this class handles incoming messages sent on port 9090. These messages are sent by the event source. This class implements the processMessage method which handles event data and creates
 * event for further processing
 */
public class EventConnectionHandler extends ConnectionHandler {

  private static final Logger logger = LogManager.getLogger( EventConnectionHandler.class );

  /**
   * @param clientSocket
   *          Socket instance over which data is received from the event source
   */
  public EventConnectionHandler( SocketChannel clientSocket ) {
    super( clientSocket );
  }

  /**
   * Processes the incoming message, converts it into its string representation and creates an event instance which is then processed by the EventHandlerService
   * 
   * @param message
   *          Message in byte format
   */
  @Override
  void processMessage( ByteBuffer message ) throws Exception {

    logger.entry( message );
    int numberOfBytesToRead = message.position();
    final byte[] byteBuffer = new byte[numberOfBytesToRead];

    message.flip();
    message.get( byteBuffer, 0, numberOfBytesToRead );

    final String messageStr = new String( byteBuffer, "UTF-8" );
    logger.debug( "Processing message: " + messageStr );

    EventHandlerService.INSTANCE.addEvent( EventFactory.createEvent( messageStr ) );
    logger.exit();
  }

  /**
   * Allocation size for bytebuffers used to read data from the input stream
   */
  @Override
  public int getMaxBufferSize() {
    return ConfigService.INSTANCE.getMaxBufferLengthEventSocketInByte();
  }
}
