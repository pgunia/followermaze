package com.soundlcoud.followermaze.server.model.event;

/**
 * Enum describing the different types of events that are handled by the server
 */
public enum EventType {
  FOLLOW, UNFOLLOW, BROADCAST, PRIVATE_MSG, STATUS_UPDATE;
}
