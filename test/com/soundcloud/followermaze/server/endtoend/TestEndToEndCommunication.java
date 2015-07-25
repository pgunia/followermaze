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

  /** List keeps all started threads to terminate them during tearDown */
  final private static List<Thread> threads = new ArrayList<Thread>();

  /** Manager to handle startup and shutdown of servers */
  final private ServerManager serverManager = new ServerManager();

  /** Manager to create and handle clients */
  final private ClientManager clientManager = new ClientManager();

  /** Port on which clients connect to the server */
  final static int CLIENT_PORT = 9099;

  /** Port on which the event source connects to the server */
  final static int EVENT_SOURCE_PORT = 9090;

  /** Time to wait for the server to start up before starting the tests */
  final static long RAMP_UP_SERVER_WAIT_TIME = 1000;

  /** Latch which is decremented, when all clients disconnect due to connection timeouts */
  private CountDownLatch clientTimeoutLatch = null;

  @Before
  public void setUp() throws Exception {
    logger.entry();

    serverManager.startUpServers( EVENT_SOURCE_PORT, CLIENT_PORT );
    clientTimeoutLatch = clientManager.connectClients( CLIENT_PORT );

    logger.exit();
  }

  @After
  public void tearDown() throws Exception {
    logger.entry();
    serverManager.stopServers();
    clientManager.disconnectAllClients();
    logger.exit();
  }

  @Test
  public void testFollowMessageHandling() {
    logger.entry();
    final String lineSeparator = "\r\n";
    final List<String> messagesSent = new ArrayList<String>();
    messagesSent.add( "1|F|1|2" + lineSeparator );
    messagesSent.add( "2|F|2|1" + lineSeparator );

    final StringBuilder messagesRetrievedClient1 = new StringBuilder( "" );
    final StringBuilder messagesRetrievedClient2 = new StringBuilder( "" );

    // only to user gets informed
    messagesRetrievedClient1.append( "2|F|2|1" + lineSeparator );
    messagesRetrievedClient2.append( "1|F|1|2" + lineSeparator );

    // create Event Source, that sends the messages to the server
    final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
    logger.info( "Start sending events to server..." );

    Thread eventSourceThread = new Thread( eventSource );
    threads.add( eventSourceThread );
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
    logger.exit();
  }

}
