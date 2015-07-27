package com.soundcloud.followermaze.server.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.soundcloud.followermaze.server.endtoend.TestEndToEndCommunication;
import com.soundcloud.followermaze.server.service.TestUserRegistryServiceLocal;
import com.soundcloud.followermaze.server.service.TestUserRegistryServiceSocket;
import com.soundcoud.followermaze.server.event.TestEventFactory;

@RunWith( Suite.class )
@SuiteClasses( { TestEventFactory.class, TestUserRegistryServiceLocal.class, TestUserRegistryServiceSocket.class, TestEndToEndCommunication.class } )
public class FollowerMazeTestsuite {

}
