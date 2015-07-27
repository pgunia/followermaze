package com.soundcloud.followermaze.server.service;

import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundlcoud.followermaze.server.model.event.Event;
import com.soundlcoud.followermaze.server.model.event.EventWorker;

/**
 * EventHandlerService handles incoming events and processes them using instances of EventWorker. The events are added to a queue which sorts them based on their sequeunce number. Implemented using
 * enum singleton pattern.
 *
 */
public enum EventHandlerService {
  INSTANCE;

  private final Logger logger = LogManager.getLogger( EventHandlerService.class );

  /** Queue that holds events, due to single threaded processing, no need for synchronization */
  final PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

  /** Sequence number of next event to process */
  int nextSequenceNumber = 1;

  /**
   * Adds an event to the event queue and triggers an event worker run to process currently queued events.
   * 
   * @param event
   *          Event to be added
   */
  public void addEvent( final Event event ) {
    logger.entry( event );

    // push to queue
    eventQueue.add( event );

    // doing this in the current thread blocks and limits the incoming rate on the producer side
    final EventWorker worker = new EventWorker();
    worker.run();

    logger.debug( "Events on Queue: " + eventQueue.size() );
    logger.exit();
  }

  /**
   * @return Number of next event to process
   */
  public int getNextSequenceNumber() {
    return nextSequenceNumber;
  }

  /**
   * 
   * @param nextSequenceNumber
   *          Number of next event to process
   */
  public void setNextSequenceNumber( int nextSequenceNumber ) {
    this.nextSequenceNumber = nextSequenceNumber;
  }

  /**
   * Retrieves the first event in the queue. This is the event with the smallest sequence number of all events in the queue.
   * 
   * @return First event in the queue.
   */
  public Event first() {
    if ( eventQueue.size() > 0 ) {
      return eventQueue.peek();
    } else {
      return null;
    }
  }

  /**
   * Removes the given event from the queue.
   * 
   * @param event
   *          Event to be removed
   * @return True, if the event has been successfully removed, else false
   */
  public boolean remove( final Event event ) {
    return eventQueue.remove( event );
  }

  /**
   * Resets the handler to its startup state
   */
  public void reset() {
    eventQueue.clear();
    nextSequenceNumber = 1;
  }
}
