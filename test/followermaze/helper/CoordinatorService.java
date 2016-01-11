package followermaze.helper;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class is used to store message lists send to and retrieve from the server. Implemented using singleton enum.
 *
 */
public enum CoordinatorService {

  INSTANCE;

  /** Buffer holds all messages send to the server for a specific client id */
  private final Map<Integer, String> sendMessages = new ConcurrentHashMap<Integer, String>();

  /** Buffer holds all messages retrieved by the client for a specific client id */
  private final Map<Integer, String> retrievedMessages = new ConcurrentHashMap<Integer, String>();

  public void addSendMessages( final Integer userId, final String messages ) {
    sendMessages.put( userId, messages );
  }

  public void addRetrievedMessages( final Integer toUserId, final String messages ) {
    retrievedMessages.put( toUserId, messages );
  }

  public String getSendMessagesForUserId( final Integer toUserId ) {
    return sendMessages.get( toUserId );
  }

  public String getRetrievedMessagesByUserId( final Integer toUserId ) {
    return retrievedMessages.get( toUserId );
  }

  /**
   * Cleans up all data stored in the service
   */
  public void clear() {
    sendMessages.clear();
    retrievedMessages.clear();
  }

}
