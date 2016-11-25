package de.fhg.fokus.streetlife.mmecp.configurator;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import javax.inject.Inject;
import java.io.File;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class MMECPAnnotationTest extends Arquillian{

	private static final Logger LOG = LoggerFactory.getLogger(MMECPAnnotationTest.class);

	@Inject
	@MMECPConfig(value = "test.annotation")
	public String property;

	@Deployment
    public static WebArchive createDeployment() {

    	File[] libs
            = Maven.resolver().loadPomFromFile( "pom.xml" )
            .resolve("org.hamcrest:hamcrest-all")
            .withTransitivity()
            .asFile();

        WebArchive jar = ShrinkWrap.create(WebArchive.class, "mmecp-configurator-test.war");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.configurator");

        // add test resources:
        jar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        jar.addAsLibraries(libs);
        jar.addAsResource("mmecp.properties");

        LOG.info(jar.toString(true));
        return jar;
    }


	@Test
	public void shouldSetOutsideCorrectValue(){
		assertThat(property, is("This is a test annotation"));
	}
	
}