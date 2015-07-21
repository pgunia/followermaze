package com.soundlcoud.followermaze.server.model.event;

import java.util.Collection;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.server.service.UserRegistryService;

public class BroadcastEvent extends AbstractEvent {

  public BroadcastEvent( Integer sequenceNumber, String message ) {
    super( sequenceNumber, -1, -1, message );
  }

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
