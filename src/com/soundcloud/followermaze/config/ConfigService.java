package com.soundcloud.followermaze.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public enum ConfigService {
  INSTANCE;

  private final String MAX_THREADS_EVENT_DISPATCHER_VAR = "maxThreadsEventDispatcher";

  private final String MAX_THREADS_USER_CLIENT_DISPATCHER_VAR = "maxThreadsUserClientDispatcher";

  private final String MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR = "maxBufferLengthEventSocketInByte";

  private final String MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR = "maxBufferLengthClientSocketInByte";

  private final String RESULT_BUFFER_LENGTH_IN_BYTE_VAR = "resultBufferLengthInByte";

  private final Properties configProperties;

  private final Properties defaultProperties = new Properties();

  private final Logger logger = LogManager.getLogger( ConfigService.class );

  private ConfigService() {
    initDefaults();

    // make sure all necessary properties are set, use defaults as fallback
    configProperties = new Properties( defaultProperties );

    // read config file from classpath
    final InputStream in = this.getClass().getClassLoader().getResourceAsStream( "server.properties" );
    try {
      configProperties.load( in );
    } catch ( IOException e ) {
      logger.error( "Could not load properties from file.", e );
    }
  }

  private void initDefaults() {
    logger.entry();
    defaultProperties.setProperty( MAX_THREADS_EVENT_DISPATCHER_VAR, "1" );
    defaultProperties.setProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR, "100" );
    defaultProperties.setProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR, "1024" );
    defaultProperties.setProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR, "16" );
    defaultProperties.setProperty( RESULT_BUFFER_LENGTH_IN_BYTE_VAR, "20" );
    logger.exit();
  }

  public int getMaxThreadsEventDispatcher() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_THREADS_EVENT_DISPATCHER_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_THREADS_EVENT_DISPATCHER_VAR ) );
    }
    return result;

  }

  public int getMaxThreadsUserClientDispatcher() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR ) );
    }
    return result;

  }

  public int getMaxBufferLengthEventSocketInByte() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR ) );
    }
    return result;
  }

  public int getMaxBufferLengthClientSocketInByte() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR ) );
    }
    return result;
  }

  public int getResultBufferLengthInByte() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( RESULT_BUFFER_LENGTH_IN_BYTE_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( RESULT_BUFFER_LENGTH_IN_BYTE_VAR ) );
    }
    return result;
  }

}
