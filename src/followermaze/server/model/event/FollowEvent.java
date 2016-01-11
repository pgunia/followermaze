package followermaze.server.model.event;

import followermaze.server.model.client.Client;
import followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent follow events.
 *
 */
public class FollowEvent extends AbstractEvent {

  public FollowEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId, String messageStr ) {
    super( sequenceNumber, fromUserId, toUserId, messageStr );
  }

  /**
   * Method processes the follow event and adds a new follower to the list of followers of the "to user" before adding a new notification job to the executor service of the "to user" client instance.
   */
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
