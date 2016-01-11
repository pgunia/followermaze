package followermaze.server.testsuite;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import followermaze.server.endtoend.TestEndToEndCommunication;
import followermaze.server.event.TestEventFactory;
import followermaze.server.service.TestUserRegistryServiceLocal;
import followermaze.server.service.TestUserRegistryServiceSocket;

@RunWith( Suite.class )
@SuiteClasses( { TestEventFactory.class, TestUserRegistryServiceLocal.class, TestUserRegistryServiceSocket.class, TestEndToEndCommunication.class } )
public class FollowerMazeTestsuite {

}
