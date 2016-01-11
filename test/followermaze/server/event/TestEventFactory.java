package followermaze.server.event;

import static org.junit.Assert.assertTrue;

import org.junit.Test;

import followermaze.server.model.event.BroadcastEvent;
import followermaze.server.model.event.Event;
import followermaze.server.model.event.EventFactory;
import followermaze.server.model.event.FollowEvent;
import followermaze.server.model.event.PrivateMsgEvent;
import followermaze.server.model.event.StatusUpdateEvent;
import followermaze.server.model.event.UnfollowEvent;

/**
 * 
 * Test class tests the event factory by providing valid / invalid inputs and by checking the returned objetcs.
 */
public class TestEventFactory {

  private static final String MESSAGE_TERMINATOR = "\r\n";;

  // test valid inputs and check the returned objects
  @Test
  public void testCreateFollowEvent() {

    final String eventStr = "1|F|1|2" + MESSAGE_TERMINATOR;
    final Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Invalid event creation, expected FollowEvent, received " + event.getClass().getSimpleName(), event instanceof FollowEvent );

  }

  @Test
  public void testCreateUnFollowEvent() {

    final String eventStr = "1|U|1|2" + MESSAGE_TERMINATOR;
    final Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Invalid event creation, expected UnfollowEvent, received " + event.getClass().getSimpleName(), event instanceof UnfollowEvent );

  }

  @Test
  public void testCreateStatusUpdateEvent() {

    final String eventStr = "1|S|1" + MESSAGE_TERMINATOR;
    final Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Invalid event creation, expected StatusUpdateEvent, received " + event.getClass().getSimpleName(), event instanceof StatusUpdateEvent );

  }

  @Test
  public void testCreateBroadcastUpdateEvent() {

    final String eventStr = "1|B" + MESSAGE_TERMINATOR;
    final Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Invalid event creation, expected BroadcastEvent, received " + event.getClass().getSimpleName(), event instanceof BroadcastEvent );

  }

  @Test
  public void testCreatePrivateMessageEvent() {

    final String eventStr = "1|P|1|2" + MESSAGE_TERMINATOR;
    final Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Invalid event creation, expected PrivateMessageEvent, received " + event.getClass().getSimpleName(), event instanceof PrivateMsgEvent );

  }

  @Test
  public void testInvalidInputs() {

    String eventStr = "ABCDEFG" + MESSAGE_TERMINATOR;
    Event event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Expected null, received " + event, event == null );

    eventStr = "1|F|1|" + MESSAGE_TERMINATOR;
    event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Expected null, received " + event, event == null );

    eventStr = "1|B|1|" + MESSAGE_TERMINATOR;
    event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Expected null, received " + event, event == null );

    eventStr = "1|F|1|F|1|" + MESSAGE_TERMINATOR;
    event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Expected null, received " + event, event == null );

    eventStr = MESSAGE_TERMINATOR;
    event = EventFactory.createEvent( eventStr );
    assertTrue( "ERROR: Expected null, received " + event, event == null );

  }
}
