package followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Abstract base class of all events generated and processed within the server. Immutable
 *
 */
public abstract class AbstractEvent implements Event, Comparable<Event> {

  /** Logger */
  protected static final Logger logger = LogManager.getLogger( AbstractEvent.class );

  /** Sequence number of the event instance */
  final Integer sequenceNumber;

  /** To user ID extracted from the source message */
  final Integer toUserId;

  /** From user ID extracted from the source message */
  final Integer fromUserId;

  /** String representation of the payload of the source message */
  final String messageStr;

  /**
   * 
   * @param sequenceNumber
   *          Sequence number of the event
   * @param fromUserId
   *          From user Id of the event
   * @param toUserId
   *          To user Id of the event
   * @param messageStr
   *          Complete message string send to the server
   */
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
