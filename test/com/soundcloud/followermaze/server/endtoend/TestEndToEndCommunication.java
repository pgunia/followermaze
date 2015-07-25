package com.soundcloud.followermaze.server.endtoend;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
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

  private static final String MESSAGE_TERMINATOR = "\r\n";;

  @Before
  public void setUp() throws Exception {
    logger.entry();

    logger.exit();
  }

  @After
  public void tearDown() throws Exception {
    logger.entry();
    logger.exit();
  }

  @Test
  public void testFollowMessageHandling() {
    logger.entry();

    final ServerManager serverManager = new ServerManager();
    serverManager.startUpServers( EVENT_SOURCE_PORT, CLIENT_PORT );

    final ClientManager clientManager = new ClientManager();
    final CountDownLatch clientTimeoutLatch = clientManager.connectClients( CLIENT_PORT );
    Thread eventSourceThread = null;

    try {

      final List<String> messagesSent = new ArrayList<String>();
      messagesSent.add( "1|F|1|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "2|F|2|1" + MESSAGE_TERMINATOR );

      final StringBuilder messagesRetrievedClient1 = new StringBuilder( "" );
      final StringBuilder messagesRetrievedClient2 = new StringBuilder( "" );

      // only to user gets informed
      messagesRetrievedClient1.append( "2|F|2|1" + MESSAGE_TERMINATOR );
      messagesRetrievedClient2.append( "1|F|1|2" + MESSAGE_TERMINATOR );

      // create Event Source, that sends the messages to the server
      final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
      logger.info( "Start sending events to server..." );

      eventSourceThread = new Thread( eventSource );
      eventSourceThread.start();

      try {
        clientTimeoutLatch.await();
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
    } finally {

      clientManager.disconnectAllClients();
      serverManager.stopServers();
      eventSourceThread.interrupt();
      TestCoordinatorService.INSTANCE.clear();
    }
    logger.exit();
  }

  @Test
  public void testUnFollowMessageHandling() {
    final ServerManager serverManager = new ServerManager();
    serverManager.startUpServers( EVENT_SOURCE_PORT, CLIENT_PORT );

    final ClientManager clientManager = new ClientManager();
    final CountDownLatch clientTimeoutLatch = clientManager.connectClients( CLIENT_PORT );
    Thread eventSourceThread = null;

    try {
      final List<String> messagesSent = new ArrayList<String>();
      messagesSent.add( "1|U|1|2" + MESSAGE_TERMINATOR );
      messagesSent.add( "2|U|2|1" + MESSAGE_TERMINATOR );

      // create Event Source, that sends the messages to the server
      final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
      logger.info( "Start sending events to server..." );

      eventSourceThread = new Thread( eventSource );
      eventSourceThread.start();

      try {
        clientTimeoutLatch.await();
      } catch ( InterruptedException e ) {
        logger.error( "Error while waiting for clients to disconnect!", e );
      }

      // compare the send and retrieved messages for both clients
      final String retrieved1 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 1 );
      assertTrue( "ERROR: Messages retrieved for User 1!" + retrieved1, retrieved1 == null );

      final String retrieved2 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( 2 );
      assertTrue( "ERROR: Messages retrieved for User 2!", retrieved2 == null );

    } finally {
      eventSourceThread.interrupt();
      clientManager.disconnectAllClients();
      serverManager.stopServers();
      TestCoordinatorService.INSTANCE.clear();
    }
  }
}
