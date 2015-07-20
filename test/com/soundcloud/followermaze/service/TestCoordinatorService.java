package com.soundcloud.followermaze.service;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class is used to store message lists send to and retrieve from the server. Implemented using singleton enum.
 *
 */
public enum TestCoordinatorService {

  INSTANCE;

  /** Buffer holds all messages send to the server for a specific client id */
  private final Map<String, String> sendMessages = new ConcurrentHashMap<String, String>();

  /** Buffer holds all messages retrieved by the client for a specific client id */
  private final Map<String, String> retrievedMessages = new ConcurrentHashMap<String, String>();

  public void addSendMessages( final String toUserId, final String messages ) {
    sendMessages.put( toUserId, messages );
  }

  public void addRetrievedMessages( final String toUserId, final String messages ) {
    retrievedMessages.put( toUserId, messages );
  }

  public String getSendMessagesForUserId( final String toUserId ) {
    return sendMessages.get( toUserId );
  }

  public String getRetrievedMessagesByUserId( final String toUserId ) {
    return retrievedMessages.get( toUserId );
  }

}
