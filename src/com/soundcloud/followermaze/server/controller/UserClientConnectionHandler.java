package com.soundcloud.followermaze.server.controller;

import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.config.ConfigService;
import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.service.UserRegistryService;

public class UserClientConnectionHandler extends ConnectionHandler {

  private static final Logger logger = LogManager.getLogger( UserClientConnectionHandler.class );

  public UserClientConnectionHandler( SocketChannel clientSocket ) {
    super( clientSocket );
  }

  @Override
  void processMessage( ByteBuffer message ) throws Exception {
    logger.entry( message );

    final String messageStr = new String( message.array(), "UTF-8" ).trim();
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
