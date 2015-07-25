package com.soundcloud.followermaze.server.endtoend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 
 * Class creates client instances and connects them to an UserClientDispatcher
 */
public class ConnectClients {

  private static final int NUMBER_OF_CLIENTS = 10;

  /** List creates ClientSocket instances which connect to the server */
  private static final List<ClientSocket> clients = new ArrayList<ClientSocket>();

  private static final ExecutorService connectionExecutor = Executors.newFixedThreadPool( NUMBER_OF_CLIENTS );

  /**
   * Creates a configured number of clients and connected them to the server on the specified port
   * 
   * @param port
   *          Port on which the clients connect to the server
   */
  public void connectClients( final int port ) {
    for ( int i = 0; i < NUMBER_OF_CLIENTS; i++ ) {
      final ClientSocket client = createClient( i, port );
      clients.add( client );
      connectionExecutor.submit( client );
    }
  }

  /**
   * Creates a ClientSocket instance
   * 
   * @param id
   *          ID unter which the client registers itself at the server
   * @param port
   *          Port on which the client connects to the server
   * @return ClientSocket instance used for further processing
   */
  private ClientSocket createClient( int id, int port ) {
    return new ClientSocket( id, port );
  }

}
