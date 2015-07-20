package com.soundcloud.followermaze.service;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.soundcloud.followermaze.dispatcher.BaseDispatcher;
import com.soundcloud.followermaze.dispatcher.EventDispatcher;
import com.soundcloud.followermaze.dispatcher.UserClientDispatcher;

public class TestEndToEndCommunication {

  private static final Logger logger = LogManager.getLogger( TestEndToEndCommunication.class );

  final private static List<Thread> threads = new ArrayList<Thread>();

  final static int CLIENT_PORT = 9099;

  final static int EVENT_SOURCE_PORT = 9090;

  final static long RAMP_UP_SERVER_WAIT_TIME = 2000;

  @Before
  public void setUp() throws Exception {
    logger.entry();

    // start both servers
    final BaseDispatcher eventDispatcher = new EventDispatcher( EVENT_SOURCE_PORT );
    Thread eventDispatcherThread = new Thread( eventDispatcher );
    threads.add( eventDispatcherThread );
    eventDispatcherThread.start();

    final UserClientDispatcher clientDispatcher = new UserClientDispatcher( CLIENT_PORT );
    Thread userClientDispatcherThread = new Thread( clientDispatcher );
    threads.add( userClientDispatcherThread );
    userClientDispatcherThread.start();

    // give the servers some time to start up
    Thread.sleep( RAMP_UP_SERVER_WAIT_TIME );
    logger.exit();
  }

  @After
  public void tearDown() throws Exception {
    logger.entry();
    // kill the collected server threads
    for ( Thread curThread : threads ) {
      // it´s deprecated, but does the job, no need for gracefully shutdown
      curThread.stop();
    }
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

    // create two clients connected to the server
    final String userId1 = "1" + lineSeparator;
    final BaseSocket clientSocket1 = new ClientSocket( userId1, CLIENT_PORT );
    logger.info( "Start retrieving messages for client 1 from server..." );
    Thread clientSocket1Thread = new Thread( clientSocket1 );
    threads.add( clientSocket1Thread );
    clientSocket1Thread.start();

    final String userId2 = "2" + lineSeparator;
    final BaseSocket clientSocket2 = new ClientSocket( userId2, CLIENT_PORT );
    logger.info( "Start retrieving messages for client 2 from server..." );
    Thread clientSocket2Thread = new Thread( clientSocket2 );
    threads.add( clientSocket2Thread );
    clientSocket2Thread.start();

    // create Event Source, that sends the messages to the server
    final BaseSocket eventSource = new EventSocket( messagesSent, EVENT_SOURCE_PORT );
    logger.info( "Start sending events to server..." );

    Thread eventSourceThread = new Thread( eventSource );
    threads.add( eventSourceThread );
    eventSourceThread.start();

    // wait a while to check results, make sure to exceed the client read timeouts
    try {
      Thread.sleep( 10000 );
    } catch ( InterruptedException e ) {
      e.printStackTrace();
    }

    // compare the send and retrieved messages for both clients
    final String send1 = messagesRetrievedClient1.toString();
    final String retrieved1 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( userId1.trim() );
    assertTrue( "ERROR: Send and retrieved messages are not equal: Send: " + send1 + " , Retrieved: " + retrieved1 + " END", send1.equals( retrieved1 ) );

    final String send2 = messagesRetrievedClient2.toString();
    final String retrieved2 = TestCoordinatorService.INSTANCE.getRetrievedMessagesByUserId( userId2.trim() );
    assertTrue( "ERROR: Send and retrieved messages are not equal: Send: " + send2 + " , Retrieved: " + retrieved2 + " END", send2.equals( retrieved2 ) );
    logger.exit();
  }

}
