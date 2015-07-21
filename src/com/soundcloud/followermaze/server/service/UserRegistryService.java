package com.soundcloud.followermaze.server.service;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.soundcloud.followermaze.server.model.client.Client;
import com.soundlcoud.followermaze.server.model.event.ClientNotification;

/**
 * UserRegistryService handles everything that is connected to users. It handles registration and removal of users and implements the follower logic. Implemented using singleton enum pattern.
 *
 */
public enum UserRegistryService {

  INSTANCE;

  /** Logger */
  private static final Logger logger = LogManager.getLogger( UserRegistryService.class );

  /** needs synchronization due to highly concurrent access during registration and removal */
  private final Map<Integer, Client> registeredClients = new ConcurrentHashMap<Integer, Client>();

  /** does not need synchronization due to single threaded event processing */
  private final Map<Integer, Set<Integer>> followers = new HashMap<Integer, Set<Integer>>();

  /** Map holds ExecutorService insatnces per connected user */
  private final Map<Integer, ExecutorService> notificationQueues = new HashMap<Integer, ExecutorService>();

  /**
   * Register a client in the registry
   * 
   * @param client
   *          Client instance to be registered
   */
  public void registerClient( final Client client ) {
    logger.entry( client );
    registeredClients.put( client.getId(), client );
    logger.info( "Registered Client: " + client.getId() );
    logger.exit();
  }

  public Client getClientById( final Integer id ) {
    return registeredClients.get( id );
  }

  /**
   * Remove client from registered users list
   * 
   * @param client
   *          Client that is removed from registry
   */
  public void removeClient( final Client client ) {
    logger.entry( client );
    registeredClients.remove( client.getId() );
    notificationQueues.remove( client.getId() );
    logger.exit();
  }

  /**
   * Method retrieves all followers of the client
   * 
   * @param integer
   *          Client, whose followers have to be retrieved
   * @return Set containing all following clients, null, if no followers have been registered
   */
  public Set<Integer> getFollowers( final Integer followedId ) {
    return followers.get( followedId );
  }

  /**
   * Method a collection with all registered clients
   * 
   * @return Collection with all Clients in the User Registry
   */
  public Collection<Client> getAllRegisteredUser() {
    return registeredClients.values();
  }

  /**
   * Method is not thread-safe, this is not necessary because of the single-thread processing of events. No race-conditions are possible, thus the overhead and complexity of synchronization does not
   * need to be implemented. Handles processing gracefully, if the follower is already in the list, this is silently accepted.
   * 
   * @param followed
   *          Client who gets a new follower
   * @param follower
   *          Client who starts to follow antoher client
   */
  public void addFollower( final Integer followedId, final Integer followerId ) {
    logger.entry( followedId, followerId );
    Set<Integer> followerOfClient = followers.get( followedId );
    if ( followerOfClient == null ) {
      followerOfClient = new HashSet<Integer>();
      followers.put( followedId, followerOfClient );
      logger.debug( "Created empty follower list for user " + followedId );
    }
    final boolean added = followerOfClient.add( followerId );
    if ( !added ) {
      logger.warn( "User  " + followerId + " has already been following user " + followedId );
    }
    logger.debug( "Added " + followerId + " to follower list of " + followedId );
    logger.exit();
  }

  /**
   * Method is not thread-safe, this is not necessary because of the single-thread processing of events. No race-conditions are possible, thus the overhead and complexity of synchronization does not
   * need to be implemented. Handles processing gracefully, if follower is not in the list, it silently accepts that.
   * 
   * @param followed
   *          Client who loses a follower
   * @param follower
   *          Client who stops following another client
   */
  public void removeFollower( final Integer followedId, final Integer followerId ) {
    logger.entry( followedId, followerId );
    final Set<Integer> followerOfClient = followers.get( followedId );
    if ( followerOfClient != null ) {
      final boolean removed = followerOfClient.remove( followerId );
      if ( !removed ) {
        logger.warn( "User  " + followerId + " has not been following user " + followedId );
      }
      if ( followerOfClient.size() == 0 ) {
        followers.remove( followedId );
        logger.debug( "Removed empty follower list for user " + followedId );
      }
    }
    logger.debug( "Removed " + followerId + " from follower list of " + followedId );
    logger.entry();
  }

  /**
   * Method adds a notification task to the executors running per thread. This increases concurrency and throughput, but guarantees to keep the ordering. Using one central executor can cause race
   * conditions depending on JVM thread scheduling.
   * 
   * @param message
   *          Message to be transmitted
   * @param client
   *          Client to be notified
   */
  public void addNotificationJob( final String message, final Client client ) {

    ExecutorService executor = notificationQueues.get( client.getId() );
    if ( executor == null ) {
      executor = Executors.newSingleThreadExecutor();
      notificationQueues.put( client.getId(), executor );
    }
    executor.submit( new ClientNotification( message, client ) );
  }

}
