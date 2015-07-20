package com.soundlcoud.followermaze.server.model.event;

import java.util.Set;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.service.UserRegistryService;

public class StatusUpdateEvent extends AbstractEvent {

  public StatusUpdateEvent( Integer sequenceNumber, Integer fromUserId, String eventStr ) {
    super( sequenceNumber, fromUserId, -1, eventStr );
  }

  @Override
  public void processEvent() {
    logger.entry();

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final Set<Integer> followers = userReg.getFollowers( getFromUserId() );
    if ( followers != null ) {
      for ( Integer curClientId : followers ) {
        final Client cur = userReg.getClientById( curClientId );
        if ( cur != null ) {
          userReg.addNotificationJob( messageStr, cur );
        }
      }
    } else {
      logger.warn( "There are currently no followers registered for User " + getFromUserId() );
    }
    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.STATUS_UPDATE;
  }

}
