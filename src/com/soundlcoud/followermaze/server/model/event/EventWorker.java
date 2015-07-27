package com.soundlcoud.followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.service.EventHandlerService;

/**
 * Instances of this class are used to process the events which are created by the EventHandlerService based on the byte data read from event source.
 *
 */
public class EventWorker implements Runnable {

  private static final Logger logger = LogManager.getLogger( EventWorker.class );

  /**
   * Method processes the events which are currently on the event queue handled by the EventHandlerService. The processing first retrieves the next sequence number to be processed and the event with
   * the smallest sequence number on the queue. If the numbers match, the event is processed, the number is increased, and the next event is taken from the queue. This iteration ends when the next
   * sequence number and the smallest event´s sequence number on the queue don´t match or if the queue is empty.
   */
  @Override
  public void run() {
    logger.entry();

    int currentNextSequenceNumber = EventHandlerService.INSTANCE.getNextSequenceNumber();
    // now check, if there´s something on the heap that might get processed now
    Event nextEvent = EventHandlerService.INSTANCE.first();
    logger.debug( "Started Worker Run, nextSequence: " + currentNextSequenceNumber + ", currentTopEvent: " + nextEvent.getSequenceNumber() );
    while ( nextEvent != null && nextEvent.getSequenceNumber() == currentNextSequenceNumber ) {
      EventHandlerService.INSTANCE.remove( nextEvent );

      logger.debug( "Processing Event: " + nextEvent );
      nextEvent.processEvent();
      logger.debug( "Finished processing event: " + nextEvent );

      currentNextSequenceNumber++;
      nextEvent = EventHandlerService.INSTANCE.first();
    }
    EventHandlerService.INSTANCE.setNextSequenceNumber( currentNextSequenceNumber );
    logger.exit();
  }
}
