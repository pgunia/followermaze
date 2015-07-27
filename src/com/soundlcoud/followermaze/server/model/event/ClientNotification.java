package com.soundlcoud.followermaze.server.model.event;

import com.soundcloud.followermaze.server.model.client.Client;

/**
 * 
 * Instances of this class are runnables that process notification messages for clients. This decouples the message handling from the event processing and thus increases concurrency.
 */
public class ClientNotification implements Runnable {

  /** Message to be sent to the client */
  final private String message;

  /** Client to retrieve the message */
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
