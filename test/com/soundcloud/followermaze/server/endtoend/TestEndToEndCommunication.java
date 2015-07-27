package com.soundcloud.followermaze.server.endtoend;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.soundcloud.followermaze.server.dispatcher.ServerManager;

/**
 * 
 * Tests end-to-end communication by creating a BaseDispatcher and a UserClientDispatcher, clients and an evend source. All test types are tested and send to the server which forwards them to the
 * clients. The correct arrival of the messages at the client is used for test verification.
 */
public class TestEndToEndCommunication {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( TestEndToEndCommunication.class );

  /** Port on which clients connect to the server */
  final static int CLIENT_PORT = 9099;

  /** Port on which the event source connects to the server */
  final static int EVENT_SOURCE_PORT = 9090;

  /** Time to wait for the server to start up before starting the tests */
  final static long RAMP_UP_SERVER_WAIT_TIME = 1000;

  /** Handles startup and shutodnw of server instances */
  private static ServerManager serverManager = null;

  private static final String MESSAGE_TERMINATOR = "\r\n";;

  /** Handles client connections, is constructed during setUp per test */
  private ClientManager clientManager = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // startup servers
    serverManager = new ServerManager();
    serverManager.startUpServers( EVENT_SOURCE_PORT, CLIENT_PORT );
  }

  @AfterClass
  public static void tearDownAfterClass() throws Exception {
    // shutdown servers
    serverManager.stopServers();
  }

  @Before
  public void setUp() throws Exception {
    clientManager = new ClientManager();
    clientManager.connectClients( CLIENT_PORT );
  }

  @After
  public void tearDown() throws Exception {
    reset();
    clientManager = null;
  }

  @Test
  public void testFollowMessageHandling() {
    logger.entry();
    logger.info( "Testing Follow Message Handling..." );

    Thread eventSourceThread = null;

    try {

      final List<String> messagesSent = new ArrayList<String>();
      messagesSent.add( "1|F|1|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "2|F|2|1" + MESSAGE_TERMINATOR );
      messagesSent.add( "3|F|3|1" + MESSAGE_TERMINATOR );
      messagesSent.add( "4|F|4|1" + MESSAGE_TERMINATOR );
      messagesSent.add( "5|F|5|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "6|F|6|2" + MESSAGE_TERMINATOR );

      // shuffle the message list to simulate unordered message sending
      Collections.shuffle( messagesSent );

      final StringBuilder messagesRetrievedClient1 = new StringBuilder( "" );
      final StringBuilder messagesRetrievedClient2 = new StringBuilder( "" );

      // messages for client 1
      messagesRetrievedClient1.append( "2|F|2|1" + MESSAGE_TERMINATOR );
      messagesRetrievedClient1.append( "3|F|3|1" + MESSAGE_TERMINATOR );
      messagesRetrievedClient1.append( "4|F|4|1" + MESSAGE_TERMINATOR );

      // messages for client 2
      messagesRetrievedClient2.append( "1|F|1|2" + MESSAGE_TERMINATOR );
      messagesRetrievedClient2.append( "5|F|5|2" + MESSAGE_TERMINATOR );
      messagesRetrievedClient2.append( "6|F|6|2" + MESSAGE_TERMINATOR );

      // create Event Source, that sends the messages to the server
      final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
      logger.info( "Start sending events to server..." );

      eventSourceThread = new Thread( eventSource );
      eventSourceThread.start();

      try {
        clientManager.getClientTimeOutLatch().await();
      } catch ( InterruptedException e ) {
        logger.error( "Error while waiting for clients to disconnect!", e );
      }

      // compare the send and retrieved messages for both clients
      final String send1 = messagesRetrievedClient1.toString();
      final String retrieved1 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 1 );
      assertTrue( "ERROR: Send and retrieved messages are not equal: Send: " + send1 + " , Retrieved: " + retrieved1 + " END", send1.equals( retrieved1 ) );

      final String send2 = messagesRetrievedClient2.toString();
      final String retrieved2 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 2 );
      assertTrue( "ERROR: Send and retrieved messages are not equal: Send: " + send2 + " , Retrieved: " + retrieved2 + " END", send2.equals( retrieved2 ) );

    } catch ( Exception e ) {
      logger.error( "Error during test execution!", e );
    } finally {
      eventSourceThread.interrupt();
      logger.info( "Testing Follow Message Handling...done" );
    }
    logger.exit();

  }

  @Test
  public void testUnFollowMessageHandling() {

    logger.info( "Testing Unfollow Message Handling..." );
    Thread eventSourceThread = null;

    try {
      final List<String> messagesSent = new ArrayList<String>();
      messagesSent.add( "1|U|1|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "2|U|2|1" + MESSAGE_TERMINATOR );
      messagesSent.add( "3|U|3|4" + MESSAGE_TERMINATOR );
      messagesSent.add( "4|U|4|3" + MESSAGE_TERMINATOR );
      messagesSent.add( "5|U|5|6" + MESSAGE_TERMINATOR );
      messagesSent.add( "6|U|6|5" + MESSAGE_TERMINATOR );

      // shuffle the message list to simulate unordered message sending
      Collections.shuffle( messagesSent );

      // create Event Source, that sends the messages to the server
      final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
      logger.info( "Start sending events to server..." );

      eventSourceThread = new Thread( eventSource );
      eventSourceThread.start();

      try {
        clientManager.getClientTimeOutLatch().await();
      } catch ( InterruptedException e ) {
        logger.error( "Error while waiting for clients to disconnect!", e );
      }

      // compare the send and retrieved messages for both clients
      final String retrieved1 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 1 );
      assertTrue( "ERROR: Messages retrieved for User 1!" + retrieved1, retrieved1 == null );

      final String retrieved2 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 2 );
      assertTrue( "ERROR: Messages retrieved for User 2!", retrieved2 == null );

      final String retrieved3 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 3 );
      assertTrue( "ERROR: Messages retrieved for User 3!" + retrieved3, retrieved3 == null );

      final String retrieved4 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 4 );
      assertTrue( "ERROR: Messages retrieved for User 4!", retrieved4 == null );

      final String retrieved5 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 5 );
      assertTrue( "ERROR: Messages retrieved for User 5!" + retrieved5, retrieved5 == null );

      final String retrieved6 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 6 );
      assertTrue( "ERROR: Messages retrieved for User 6!", retrieved6 == null );

    } catch ( Exception e ) {
      logger.error( "Error during test execution!", e );
    } finally {
      eventSourceThread.interrupt();
      logger.info( "Testing Unfollow Message Handling...done" );
    }
  }

  @Test
  public void testBroadcastMessage() {

    logger.info( "Testing Broadcast Meesage Handling..." );
    Thread eventSourceThread = null;

    try {
      final List<String> messagesSent = new ArrayList<String>();
      messagesSent.add( "1|B|1|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "2|B|2|1" + MESSAGE_TERMINATOR );

      // create Event Source, that sends the messages to the server
      final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
      logger.info( "Start sending events to server..." );

      eventSourceThread = new Thread( eventSource );
      eventSourceThread.start();

      try {
        clientManager.getClientTimeOutLatch().await();
      } catch ( InterruptedException e ) {
        logger.error( "Error while waiting for clients to disconnect!", e );
      }

    } catch ( Exception e ) {
      logger.error( "Error during test execution!", e );
    } finally {
      eventSourceThread.interrupt();
      logger.info( "Testing Broadcast Meesage Handling...done" );
    }
  }

  /**
   * Resets all components after a test has been completed
   */
  private void reset() {
    clientManager.disconnectAllClients();
    serverManager.resetServers();
    TestCoordinatorService.INSTANCE.clear();
  }
}
