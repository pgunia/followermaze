package com.soundlcoud.followermaze.server.model.event;

import java.util.Collection;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent broadcast events.
 */
public class BroadcastEvent extends AbstractEvent {

  public BroadcastEvent( Integer sequenceNumber, String message ) {
    super( sequenceNumber, -1, -1, message );
  }

  /**
   * Processes the broadcast events. All at a point in time connected clients are notified. Method iterates through the list of connected clients and creates a new notification job for each. Those
   * jobs are processed concurrently by the client specific executor services. This decouples notifications from the first part of event processing.
   */
  @Override
  public void processEvent() {
    logger.entry();

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final Collection<Client> registeredClients = userReg.getAllRegisteredUser();

    // this could be done via ExecutorService...
    for ( Client curClient : registeredClients ) {
      userReg.addNotificationJob( messageStr, curClient );
    }

    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.BROADCAST;
  }

}
