package com.soundlcoud.followermaze.server.model.event;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.service.EventHandlerService;

public class EventWorker implements Runnable {

  private static final Logger logger = LogManager.getLogger( EventWorker.class );

  public EventWorker() {
  }

  @Override
  public void run() {
    logger.entry();
    // logger.info( "STARTED WORKER RUN" );
    int currentNextSequenceNumber = EventHandlerService.INSTANCE.getNextSequenceNumber();

    // now check, if there´s something on the heap that might get processed now
    Event nextEvent = EventHandlerService.INSTANCE.first();

    while ( nextEvent != null && nextEvent.getSequenceNumber() == currentNextSequenceNumber ) {
      EventHandlerService.INSTANCE.remove( nextEvent );

      logger.error( "Processing Event: " + nextEvent );
      nextEvent.processEvent();
      logger.error( "Finished processing event: " + nextEvent );

      currentNextSequenceNumber++;
      nextEvent = EventHandlerService.INSTANCE.first();

    }
    EventHandlerService.INSTANCE.setNextSequenceNumber( currentNextSequenceNumber );
    logger.exit();
  }
}
