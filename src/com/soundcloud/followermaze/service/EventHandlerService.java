package com.soundcloud.followermaze.service;

import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundlcoud.followermaze.server.model.event.Event;
import com.soundlcoud.followermaze.server.model.event.EventWorker;

public enum EventHandlerService {
  INSTANCE;

  private final Logger logger = LogManager.getLogger( EventHandlerService.class );

  /** Queue that holds events, due to single threaded processing, no need for synchronization */
  final SortedSet<Event> eventQueue = new TreeSet<Event>();

  int nextSequenceNumber = 1;

  // due to sequential processing of incoming events, use a one-thread-executor
  final ExecutorService executorService = Executors.newSingleThreadExecutor();

  int eventsAdded = 0;

  private EventHandlerService() {
  }

  public void addEvent( final Event event ) {

    logger.entry( event );
    eventsAdded++;
    // push to queue
    eventQueue.add( event );

    if ( eventQueue.size() > 5000 ) {
      try {
        Thread.sleep( 500 );
      } catch ( InterruptedException e ) {
        e.printStackTrace();
      }
    }

    // start worker every time an event is added
    executorService.submit( new EventWorker() );
    if ( (eventsAdded % 10) == 0 ) {
      logger.debug( "Added " + eventsAdded + " events." );
    }
    logger.error( "Events on Queue: " + eventQueue.size() );
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
      return eventQueue.first();
    } else {
      return null;
    }
  }

  public boolean remove( final Event event ) {
    logger.entry( event );
    final boolean result = eventQueue.remove( event );
    logger.debug( "Events on Queue: " + eventQueue.size() );
    logger.exit( result );
    return result;
  }
}
