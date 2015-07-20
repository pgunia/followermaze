package com.soundlcoud.followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public abstract class AbstractEvent implements Event, Comparable<Event> {

  protected static final Logger logger = LogManager.getLogger( AbstractEvent.class );

  final Integer sequenceNumber;

  final Integer toUserId;

  final Integer fromUserId;

  final String messageStr;

  public AbstractEvent( Integer sequenceNumber, Integer fromUserId, Integer toUserId, String messageStr ) {
    super();
    this.sequenceNumber = sequenceNumber;
    this.toUserId = toUserId;
    this.fromUserId = fromUserId;
    this.messageStr = messageStr;
  }

  @Override
  public int compareTo( final Event event ) {
    return this.getSequenceNumber().compareTo( event.getSequenceNumber() );
  }

  @Override
  public Integer getSequenceNumber() {
    return sequenceNumber;
  }

  @Override
  public Integer getToUserId() {
    return toUserId;
  }

  @Override
  public Integer getFromUserId() {
    return fromUserId;
  }

  @Override
  public String toString() {
    return "#Number: " + sequenceNumber + ", Type: " + getEventType() + ", FROM: " + fromUserId + ", TO: " + toUserId;
  }

}
