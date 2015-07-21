package com.soundcloud.followermaze.server.main;

import com.soundcloud.followermaze.server.dispatcher.BaseDispatcher;
import com.soundcloud.followermaze.server.dispatcher.EventDispatcher;
import com.soundcloud.followermaze.server.dispatcher.UserClientDispatcher;

public class DispatcherMain {

  public static void main( String[] args ) {
    final BaseDispatcher eventSourceDispatcher = new EventDispatcher( 9090 );
    new Thread( eventSourceDispatcher ).start();

    final BaseDispatcher userClients = new UserClientDispatcher( 9099 );
    new Thread( userClients ).start();
  }

}
