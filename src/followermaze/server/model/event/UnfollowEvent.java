package followermaze.server.model.event;

import followermaze.server.service.UserRegistryService;

/**
 * 
 * Instances of this class represent unfollow events
 */
public class UnfollowEvent extends AbstractEvent {

  public UnfollowEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId ) {
    super( sequenceNumber, fromUserId, toUserId, null );
  }

  /**
   * Processes the unfollow event. This only removes the "from user" from the list of followers of the "to user"
   */
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
