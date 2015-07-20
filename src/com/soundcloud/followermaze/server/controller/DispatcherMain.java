package com.soundcloud.followermaze.server.controller;

import com.soundcloud.followermaze.dispatcher.BaseDispatcher;
import com.soundcloud.followermaze.dispatcher.EventDispatcher;
import com.soundcloud.followermaze.dispatcher.UserClientDispatcher;

public class DispatcherMain {

  public static void main( String[] args ) {
    final BaseDispatcher eventSourceDispatcher = new EventDispatcher( 9090 );
    new Thread( eventSourceDispatcher ).start();

    final BaseDispatcher userClients = new UserClientDispatcher( 9099 );
    new Thread( userClients ).start();
  }

}
