package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic;

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.hamcrest.BaseMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.Description;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import javax.inject.Inject;
import javax.annotation.PostConstruct;

import org.apache.commons.beanutils.BeanUtils;
import javax.ejb.EJB;

import javax.inject.Inject;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.testng.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import de.fhg.fokus.streetlife.mmecp.configurator.MMECPConfig;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.DataAggregatorClientImpl;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Channel;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Keys;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Form;

import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import java.io.File;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;

import java.util.List;
import java.util.ArrayList;

/**
 * This integration tests 
 */
public class DataAggregatorClientImplIT extends Arquillian{

	public static final Logger logger = LoggerFactory.getLogger(DataAggregatorClientImplIT.class);

	public static File[] addLibs(String...deps){
		
		List<File> tmp = new ArrayList<File>();

		for(String dep:deps){
			File[] tmpLibs = 
				Maven.resolver().loadPomFromFile( "pom.xml" )
	            .resolve(dep)
	            .withTransitivity()
	            .asFile();

	        for(File tmpLib:tmpLibs){
	        	tmp.add(tmpLib);
	        }
		}

		return tmp.toArray(new File[0]);
	}

	@Deployment
    public static WebArchive createDeployment() {

        File[] libs = addLibs(
        	"org.hamcrest:hamcrest-all",
        	"commons-beanutils:commons-beanutils",
        	"xerces:xercesImpl",
        	"org.jboss.resteasy:resteasy-jackson-provider",
        	"de.fhg.fokus.streetlife.mmecp:configurator",
        	"org.jboss:jboss-ejb-client",
        	"org.jboss:jboss-remote-naming",
            "javax.xml.bind:jaxb-api",
            "org.jboss.resteasy:resteasy-jaxb-provider",
            "org.jboss.resteasy:resteasy-client");

        WebArchive jar = ShrinkWrap.create(WebArchive.class, "mmecp-data-aggregator-test.war");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.generic");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.json");
        jar.addPackage("de.fhg.fokus.streetlife.mmecp.dataaggregator.model");
        jar.addAsManifestResource("arquillian-manifest.mf", "MANIFEST.MF");

        // add test resources:
        jar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        jar.addAsLibraries(libs);
        jar.addAsResource("ejb-jar.xml");
        //jar.addAsResource("itdataaggregatorclient/mmecp.properties", "mmecp.properties");

        logger.info(jar.toString(true));
        return jar;
    }

    @EJB
    protected DataAggregatorClient dac;

    @Inject
    @MMECPConfig(value = "storage.url.base")
    protected String storageBaseUrl;

    @Inject
    @MMECPConfig(value = "generic.data.api.url")
    protected String genericDataApiUrl;



    // @BeforeTest
    // public void injectedFieldsShouldNotBeNull(){
    // 	assertThat(genericDataApiUrl, notNullValue());
    // 	assertThat(storageBaseUrl, notNullValue());
    // }

    @BeforeMethod
    public void getClient(){
    	// dac = DataAggregatorFactory.newInstance("BEAN").getClient();
    }

	@Test(enabled = true)
	public void shouldInjectGenericDataApiUrl() throws Exception{
		String genericDataApiUrl = dac.getGenericDataApiUrl();

		assertThat(genericDataApiUrl, is(this.genericDataApiUrl));
	}

	@Test(enabled = true)
	public void shouldInjectStorageBaseUrl() throws Exception{
		String storageBaseUrl = dac.getStorageBaseUrl();

		assertThat(storageBaseUrl, is(this.storageBaseUrl));
	}

    @Test(enabled = true)
    public void getNotifications() {
        Feed notification = dac.getNotifications("some channel id");
        assertThat(notification.getTitle(), is("Titel des Weblogs"));
    }

    @Test(enabled = false)
    public void getChannels() {
        List<Channel> channels = dac.getChannels();
        assertThat(channels.get(0).isStandard(), notNullValue());
    }

    @Test(enabled = true)
    public void getNotification() {
        Entry notification = dac.getNotification("some channel id", "a field", "an ordering");
        assertThat(notification.getTitle(), is("Traffic Jam - last one"));
    }

    @Test(enabled = true)
    public void getKeysShouldHaveCorrectFunctions(){
        Keys keys = dac.getKeys();

        assertThat(keys.getFunctions().size(), is(8));
        assertThat(keys.getFunctions(), hasItems("mean", "SD", "n", "median", "Q1", "Q3", "min", "max"));
    }

    @Test(enabled = true)
    public void getKeysShouldHaveCorrectAttributeIdentifiers(){
        Keys keys = dac.getKeys();

        assertThat(keys.getAttributeIdentifiers().size(), is(2));
        assertThat(keys.getAttributeIdentifiers(), hasItems("parkinglotId(8155).occupancy", "bikestationId(515).availability"));
    }

    @Test(enabled = true)
    public void getKeysShouldHaveCorrectConditionKeywords(){
        Keys keys = dac.getKeys();

        assertThat(keys.getConditionKeywords().size(), is(7));
        assertThat(keys.getConditionKeywords(), hasItems("timeOfDay", "dayOfWeek", "dayOfMonth", "monthOfYear", "year", "lastNdays", "all"));
    }

    @Test(enabled = true)
    public void getChannelForm() {
        List<Form> channelForms = dac.getChannelForm("some channel id");

        Assert.assertEquals(channelForms.get(0).getValue(), "rov_routing");
    }

}