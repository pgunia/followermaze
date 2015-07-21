package com.soundlcoud.followermaze.server.model.event;

import com.soundcloud.followermaze.server.service.UserRegistryService;

public class UnfollowEvent extends AbstractEvent {

  public UnfollowEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId ) {
    super( sequenceNumber, fromUserId, toUserId, null );
  }

  @Override
  public void processEvent() {
    logger.entry();
    UserRegistryService.INSTANCE.removeFollower( getToUserId(), getFromUserId() );
    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.UNFOLLOW;
  }

}
