package com.soundcloud.followermaze.server.endtoend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * 
 * Class creates client instances and connects them to an UserClientDispatcher
 */
public class ClientManager {

  /** Logger */
  private static final Logger logger = LogManager.getLogger( ClientManager.class );

  /** Number of clients to create and connect to the server */
  private static final int NUMBER_OF_CLIENTS = 10;

  /** Max wait time in ms for executor shutdown */
  private static final int SHUTDOWN_TIMEOUT = 1000;

  /** List creates ClientSocket instances which connect to the server */
  private static final List<ClientSocket> clients = new ArrayList<ClientSocket>();

  /** Executor service that connects the clients to the sever by calling their run() method */
  private ExecutorService connectionExecutor = null;

  /**
   * Creates a configured number of clients and connected them to the server on the specified port
   * 
   * @param port
   *          Port on which the clients connect to the server
   */
  public CountDownLatch connectClients( final int port ) {
    connectionExecutor = Executors.newFixedThreadPool( NUMBER_OF_CLIENTS );
    final CountDownLatch registerClientsLatch = new CountDownLatch( NUMBER_OF_CLIENTS );
    final CountDownLatch clientTimeoutLatch = new CountDownLatch( NUMBER_OF_CLIENTS );
    for ( int i = 0; i < NUMBER_OF_CLIENTS; i++ ) {
      final ClientSocket client = createClient( i, port, registerClientsLatch, clientTimeoutLatch );
      clients.add( client );
      connectionExecutor.submit( client );
    }
    try {
      registerClientsLatch.await();
    } catch ( InterruptedException e ) {
      logger.error( "Error while registering clients!", e );
    }
    return clientTimeoutLatch;
  }

  /**
   * Creates a ClientSocket instance
   * 
   * @param id
   *          ID unter which the client registers itself at the server
   * @param port
   *          Port on which the client connects to the server
   * @param registerClientLatch
   *          Latch is used to wait until all Clients are connected to the server
   * 
   * @return ClientSocket instance used for further processing
   */
  private ClientSocket createClient( int id, int port, final CountDownLatch registerClientLatch, final CountDownLatch clientTimeoutLatch ) {
    return new ClientSocket( id, port, registerClientLatch, clientTimeoutLatch );
  }

  public void disconnectAllClients() {

    connectionExecutor.shutdown();
    try {
      connectionExecutor.awaitTermination( SHUTDOWN_TIMEOUT, TimeUnit.MILLISECONDS );
    } catch ( InterruptedException e ) {
      logger.error( "Error while shutting down client executor!", e );
    }
  }
}
