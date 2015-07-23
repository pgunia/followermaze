package com.soundlcoud.followermaze.server.model.event;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundcloud.followermaze.server.service.UserRegistryService;

/**
 * Instances of this class represent private message events
 *
 */
public class PrivateMsgEvent extends AbstractEvent {

  public PrivateMsgEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId, String eventStr ) {
    super( sequenceNumber, fromUserId, toUserId, eventStr );
  }

  /**
   * Method processes private message events by adding a notification job to the executor service of the "to user"
   */
  @Override
  public void processEvent() {
    logger.entry();

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final Client toUser = userReg.getClientById( getToUserId() );
    if ( toUser != null ) {
      userReg.addNotificationJob( messageStr, toUser );
    } else {
      logger.warn( "User with Id " + getToUserId() + " is not connected." );
    }
    logger.exit();
  }

  @Override
  public EventType getEventType() {
    return EventType.PRIVATE_MSG;
  }

}
