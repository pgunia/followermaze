package com.soundlcoud.followermaze.server.model.event;

import com.soundcloud.followermaze.server.model.client.Client;

public class ClientNotification implements Runnable {

  final private String message;

  final private Client client;

  public ClientNotification( final String message, final Client client ) {
    this.message = message;
    this.client = client;
  }

  @Override
  public void run() {
    this.client.notify( message );
  }
}
