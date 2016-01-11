package followermaze.server.model.event;

import java.util.Set;

import followermaze.server.model.client.Client;
import followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent status update events.
 * 
 */
public class StatusUpdateEvent extends AbstractEvent {

  public StatusUpdateEvent( Integer sequenceNumber, Integer fromUserId, String eventStr ) {
    super( sequenceNumber, fromUserId, -1, eventStr );
  }

  /**
   * Processes status updates by first retrieving a list of followers of the "from user" from the user registry services. It iterates this list and adds a notification job for every following client
   * to the executor queue of those clients if they´re currently connected.
   */
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
      logger.debug( "There are currently no followers registered for User " + getFromUserId() );
    }
    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.STATUS_UPDATE;
  }

}
