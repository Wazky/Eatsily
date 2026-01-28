package es.uvigo.esei.tfg.suites;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import es.uvigo.esei.tfg.rest.PeopleResourceTest;
import es.uvigo.esei.tfg.rest.UsersResourceTest;

@SuiteClasses({ 
	PeopleResourceTest.class,
	UsersResourceTest.class
})
@RunWith(Suite.class)
public class IntegrationTestSuite {
}
