package followermaze.server.main;

import followermaze.server.dispatcher.ServerManager;

/**
 * Main class, starts up the server.
 *
 */
public class DispatcherMain {

  /** Event Dispatcher Port */
  private static final int EVENT_DISPATCHER_PORT = 9090;

  /** User Client Dispatcher Port */
  private static final int USER_CLIENT_DISPATCHER_PORT = 9099;

  public static void main( String[] args ) {

    final ServerManager serverManager = new ServerManager();
    serverManager.startUpServers( EVENT_DISPATCHER_PORT, USER_CLIENT_DISPATCHER_PORT );
  }
}
