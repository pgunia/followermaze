package com.soundcloud.followermaze.server.service;

import static org.junit.Assert.assertTrue;

import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.soundcloud.followermaze.helper.ClientManager;
import com.soundcloud.followermaze.helper.ClientSocket;
import com.soundcloud.followermaze.helper.CoordinatorService;
import com.soundcloud.followermaze.server.dispatcher.ServerManager;
import com.soundcloud.followermaze.server.model.client.Client;

/**
 * This class tests functionality from the UserRegistryService that needs to have clients connected.
 *
 */
public class TestUserRegistryServiceSocket {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( TestUserRegistryServiceSocket.class );

  /** Port on which clients connect to the server */
  final static int CLIENT_PORT = 9099;

  /** Handles startup and shutodnw of server instances */
  private static ServerManager serverManager = null;

  /** Handles client connections, is constructed during setUp per test */
  private ClientManager clientManager = null;

  @BeforeClass
  public static void setUpBeforeClass() throws Exception {
    // startup user client dispatcher
    serverManager = new ServerManager();
    serverManager.startUpUserClientDispatcher( CLIENT_PORT );
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
    clientManager.disconnectAllClients();
    UserRegistryService.INSTANCE.reset();
  }

  /**
   * Test checks if all clients that have been connected via the ClientManager are registered in the UserRegistry
   */
  @Test
  public void testClientsRegistered() {

    final UserRegistryService userReg = UserRegistryService.INSTANCE;

    // check if all clients that should be connected can be found in the user registry
    final List<ClientSocket> clients = clientManager.getClients();
    for ( ClientSocket curClient : clients ) {
      assertTrue( "Error: Client " + curClient.getUserId() + " is not registered in the user registry!", userReg.getClientById( curClient.getUserId() ) != null );
    }

    try {
      clientManager.getClientTimeOutLatch().await();
    } catch ( InterruptedException e ) {
      logger.error( "Error while waiting for clients to disconnect!", e );
    }

  }

  /**
   * Retrieves a user from the User Registry and sends a string to check if the number of bytes sent to the client via the socket are identical to the length of the string in bytes
   */
  @Test
  public void testClientNotify() {

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final int clientId = 1;

    // check if the client is registered
    final Client client = userReg.getClientById( clientId );
    assertTrue( "Error: Client with ID " + clientId + " is not registered in the user registry!", client != null );

    // send an arbitrary String via the clients notify method and check the number of bytes that have been written
    final String testString = "THIS IS A TESTSTRING";
    final ByteBuffer byteBuffer = Charset.forName( "UTF-8" ).encode( testString );

    final int bytesWritten = client.notify( testString );
    final int testStringLengthInByte = byteBuffer.limit();

    assertTrue( "Error: Bytes send via socket: " + bytesWritten + ", expected number of bytes: " + testStringLengthInByte, bytesWritten == testStringLengthInByte );

    try {
      clientManager.getClientTimeOutLatch().await();
    } catch ( InterruptedException e ) {
      logger.error( "Error while waiting for clients to disconnect!", e );
    }

    // now check if the messages are equal
    final String receivedMessage = CoordinatorService.INSTANCE.getRetrievedMessagesByUserId( clientId );
    assertTrue( "Error: Received and expected message are not identical! Expected: " + testString + ", received: " + receivedMessage, testString.equals( receivedMessage ) );
  }

  /**
   * Retrieves a user from the User Registry and sends a string using the addNotificationJob method from the UserRegistryService, thus the message takes more steps compared to directly sending the
   * message as in testClientNotify()
   */
  @Test
  public void testAddNotificationJob() {

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final int clientId = 1;

    // check if the client is registered
    final Client client = userReg.getClientById( clientId );
    assertTrue( "Error: Client with ID " + clientId + " is not registered in the user registry!", client != null );

    // send an arbitrary String via the clients notify method and check the number of bytes that have been written
    final String testString = "THIS IS A TESTSTRING";

    // send message via UserRegistryService
    userReg.addNotificationJob( testString, client );
    try {
      clientManager.getClientTimeOutLatch().await();
    } catch ( InterruptedException e ) {
      logger.error( "Error while waiting for clients to disconnect!", e );
    }

    final String receivedMessage = CoordinatorService.INSTANCE.getRetrievedMessagesByUserId( clientId );
    assertTrue( "Error: Expected message: " + testString + ", received message" + receivedMessage, testString.equals( receivedMessage ) );

  }
}
