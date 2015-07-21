package com.soundlcoud.followermaze.server.model.event;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.server.service.UserRegistryService;

public class FollowEvent extends AbstractEvent {

  public FollowEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId, String messageStr ) {
    super( sequenceNumber, fromUserId, toUserId, messageStr );
  }

  @Override
  public void processEvent() {
    logger.entry();
    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    userReg.addFollower( getToUserId(), getFromUserId() );
    final Client toUser = userReg.getClientById( getToUserId() );
    if ( toUser != null ) {
      userReg.addNotificationJob( messageStr, toUser );
    }
    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.FOLLOW;
  }

}
