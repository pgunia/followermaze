package followermaze.server.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ConfigService provides configuration settings throughout the whole server. The settings are initialized using default values, but can also be modified by changing their value in the
 * server.properties file on the classpath. Implemented via enum singleton pattern.
 *
 */
public enum ConfigService {
  INSTANCE;

  /** Logger */
  private final Logger logger = LogManager.getLogger( ConfigService.class );

  /** Maximum number of threads used by the event dispatcher */
  private final String MAX_THREADS_EVENT_DISPATCHER_VAR = "maxThreadsEventDispatcher";

  /** Maximum number of threads used by the user client dispatcher */
  private final String MAX_THREADS_USER_CLIENT_DISPATCHER_VAR = "maxThreadsUserClientDispatcher";

  /** Maximum buffer size in byte used to read events from an event source */
  private final String MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR = "maxBufferLengthEventSocketInByte";

  /** Maximum buffer size in byte used to read messages from connected clients */
  private final String MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR = "maxBufferLengthClientSocketInByte";

  /** Maximum buffer size for the result buffer used to copy messages from the read buffer */
  private final String RESULT_BUFFER_LENGTH_IN_BYTE_VAR = "resultBufferLengthInByte";

  /** Config properties, used to read settings from a config file */
  private final Properties configProperties;

  /** Default properties used, when no values can be read from the config file */
  private final Properties defaultProperties = new Properties();

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

  /**
   * Initializes the default values to hardcoded settings.
   */
  private void initDefaults() {
    logger.entry();
    defaultProperties.setProperty( MAX_THREADS_EVENT_DISPATCHER_VAR, "1" );
    defaultProperties.setProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR, "200" );
    defaultProperties.setProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR, "1024" );
    defaultProperties.setProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR, "16" );
    defaultProperties.setProperty( RESULT_BUFFER_LENGTH_IN_BYTE_VAR, "20" );
    logger.exit();
  }

  /**
   * 
   * @return Max number of threads used by the event dispatcher
   */
  public int getMaxThreadsEventDispatcher() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_THREADS_EVENT_DISPATCHER_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_THREADS_EVENT_DISPATCHER_VAR ) );
    }
    return result;

  }

  /**
   * 
   * @return Max number of threads used by the client dispatcher
   */
  public int getMaxThreadsUserClientDispatcher() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_THREADS_USER_CLIENT_DISPATCHER_VAR ) );
    }
    return result;

  }

  /**
   * 
   * @return Max size of buffer in byte used to read data from the event source
   */
  public int getMaxBufferLengthEventSocketInByte() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_BUFFER_LENGTH_EVENT_SOCKET_IN_BYTE_VAR ) );
    }
    return result;
  }

  /**
   * 
   * @return Max size of buffer in byte used to read data from connected clients during registration
   */
  public int getMaxBufferLengthClientSocketInByte() {
    int result = -1;
    try {
      result = Integer.valueOf( configProperties.getProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR ) );
    } catch ( NumberFormatException ex ) {
      return Integer.valueOf( defaultProperties.getProperty( MAX_BUFFER_LENGTH_CLIENT_SOCKET_IN_BYTE_VAR ) );
    }
    return result;
  }

  /**
   * 
   * @return Size of temporary buffer used to store a single message for further processing
   */
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
