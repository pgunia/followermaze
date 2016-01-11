package followermaze.server.model.event;

/**
 * Interface for all events generated and processed within the server.
 *
 */
public interface Event {

  /**
   * @return Sequence number of the event instance
   */
  public Integer getSequenceNumber();

  /**
   * @return From user ID of the event instance
   */
  public Integer getFromUserId();

  /**
   * @return To user ID of the event instance
   */
  public Integer getToUserId();

  /**
   * @return Type of the event instance
   */
  public EventType getEventType();

  /**
   * Event-depending processing, needs to be implemented by the concrete subclasses
   */
  public void processEvent();

}
