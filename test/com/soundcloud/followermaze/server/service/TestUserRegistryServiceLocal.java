package com.soundcloud.followermaze.server.service;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.junit.After;
import org.junit.Test;

import com.soundcloud.followermaze.server.model.client.Client;

/**
 * This test class covers all functionality of the UserRegistryService that does not need clients to be connected to a set-up server.
 *
 */
public class TestUserRegistryServiceLocal {

  @After
  public void tearDown() throws Exception {
    UserRegistryService.INSTANCE.reset();
  }

  @Test
  public void testRegisterUser() {

    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final int clientId = 1;

    // register a client
    final Client client = new Client( clientId, null );
    userReg.registerClient( client );

    // retrieve the client based on its ID and compare
    final Client fromReg = userReg.getClientById( clientId );
    assertTrue( "Error: Client instances are not equal! ", client.equals( fromReg ) );

  }

  @Test
  public void testUnRegisterUser() {

    // register user
    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final int clientId = 1;

    // register a client
    final Client client = new Client( clientId, null );
    userReg.registerClient( client );

    // and remove it
    userReg.removeClient( client );

    // retrieve the client based on its ID and compare
    final Client fromReg = userReg.getClientById( clientId );
    assertTrue( "Error: No client instance should have been returned! ", fromReg == null );
  }

  @Test
  public void testFollowUser() {

    // add some followers for users
    final int userId = 1;
    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final Set<Integer> followersOfUser1 = new HashSet<Integer>( Arrays.asList( 2, 3, 4, 5, 6, 7, 8, 9, 10 ) );

    // register the users
    for ( Integer curUser : followersOfUser1 ) {
      userReg.addFollower( userId, curUser );
    }

    // retrieve the list from User Registry
    final Set<Integer> retrievedFollowers = userReg.getFollowers( userId );

    assertTrue( "Error: No followers retrieved from User Registry!", retrievedFollowers != null );
    assertTrue( "Error: Number of elements is different! ", retrievedFollowers.size() == followersOfUser1.size() );
    assertTrue( "Error: Follower sets don´t contain the same elements!", retrievedFollowers.containsAll( followersOfUser1 ) );
  }

  @Test
  public void testUnFollowUser() {

    // add some followers for users
    final int userId = 1;
    final UserRegistryService userReg = UserRegistryService.INSTANCE;
    final Set<Integer> followersOfUser1 = new HashSet<Integer>( Arrays.asList( 2, 3, 4, 5, 6, 7, 8, 9, 10 ) );

    // add the followers
    for ( Integer curUser : followersOfUser1 ) {
      userReg.addFollower( userId, curUser );
    }

    // remove some users from the following list
    final Set<Integer> removeFollowersFrom1 = new HashSet<Integer>( Arrays.asList( 3, 4, 7, 8 ) );
    // remove the followers
    for ( Integer curUser : removeFollowersFrom1 ) {
      userReg.removeFollower( userId, curUser );
    }

    // retrieve the list from User Registry
    final Set<Integer> retrievedFollowers = userReg.getFollowers( userId );

    final Set<Integer> expectedUsers = new HashSet<Integer>( followersOfUser1 );
    expectedUsers.removeAll( removeFollowersFrom1 );

    assertTrue( "Error: No followers retrieved from User Registry!", retrievedFollowers != null );
    assertTrue( "Error: Number of elements is different! ", retrievedFollowers.size() == expectedUsers.size() );
    assertTrue( "Error: Follower sets don´t contain the same elements!", retrievedFollowers.containsAll( expectedUsers ) );
  }
}
