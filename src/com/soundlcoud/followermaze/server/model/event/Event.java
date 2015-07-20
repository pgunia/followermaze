package com.soundlcoud.followermaze.server.model.event;

public interface Event {

  public Integer getSequenceNumber();

  public Integer getFromUserId();

  public Integer getToUserId();

  public EventType getEventType();

  public void processEvent();

}
