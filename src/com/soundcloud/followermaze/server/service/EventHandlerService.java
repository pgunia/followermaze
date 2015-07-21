package com.soundcloud.followermaze.server.service;

import java.util.PriorityQueue;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundlcoud.followermaze.server.model.event.Event;
import com.soundlcoud.followermaze.server.model.event.EventWorker;

public enum EventHandlerService {
  INSTANCE;

  private final Logger logger = LogManager.getLogger( EventHandlerService.class );

  /** Queue that holds events, due to single threaded processing, no need for synchronization */
  final PriorityQueue<Event> eventQueue = new PriorityQueue<Event>();

  /** Sequence number of next event to process */
  int nextSequenceNumber = 1;

  private EventHandlerService() {
  }

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

  public int getNextSequenceNumber() {
    return nextSequenceNumber;
  }

  public void setNextSequenceNumber( int nextSequenceNumber ) {
    this.nextSequenceNumber = nextSequenceNumber;
  }

  public Event first() {
    if ( eventQueue.size() > 0 ) {
      return eventQueue.peek();
    } else {
      return null;
    }
  }

  public boolean remove( final Event event ) {
    return eventQueue.remove( event );

  }
}
