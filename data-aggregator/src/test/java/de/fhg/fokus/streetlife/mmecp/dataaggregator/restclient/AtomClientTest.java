package de.fhg.fokus.streetlife.mmecp.dataaggregator.restclient;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.DataAggregatorClientImpl;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Channel;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Form;
import org.jboss.resteasy.plugins.providers.atom.Content;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.testng.Assert;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.lang.reflect.Field;
import java.net.URISyntaxException;
import java.util.List;

/**
* Created by benjamin on 19.08.14.
*/
public class AtomClientTest {

	private static final int FIRST_ELEMENT = 0;
	public static final boolean DELETE = true;
	public static final boolean ARCHIVE = false;
	private DataAggregatorClient dac;
	private Feed notification;

	@BeforeTest
	public void beforeTest() throws Exception {
		// Properties props = new Properties();
		// props.setProperty(Constants.PROPERTY_STORAGE_URL_BASE, "http://private-f5ae7-streetlifemmecp.apiary-mock.com");
		//System.setProperty("storage.url.base", "http://private-f5ae7-streetlifemmecp.apiary-mock.com");
		// System.setProperty("generic.data.api.url", "http://private-f559f-streetlifehistoricdataapi.apiary-mock.com");
		dac = DataAggregatorFactory.newInstance("POJO").getClient();

		// use reflections to set protected fields
		// necessary because no ee runtime is available to inject default values

		Field genericDataApiUrl = DataAggregatorClientImpl.class.getDeclaredField("genericDataApiUrl");
		Field storageBaseUrl = DataAggregatorClientImpl.class.getDeclaredField("storageBaseUrl");
		Field liveDataApiUrl = DataAggregatorClientImpl.class.getDeclaredField("liveDataApiUrl");
		Field cipClientUrl = DataAggregatorClientImpl.class.getDeclaredField("cipClientUrl");
		Field cipUserName = DataAggregatorClientImpl.class.getDeclaredField("cipUserName");
		Field cipPassword = DataAggregatorClientImpl.class.getDeclaredField("cipPassword");
		genericDataApiUrl.setAccessible(true);
		storageBaseUrl.setAccessible(true);
		liveDataApiUrl.setAccessible(true);
		cipClientUrl.setAccessible(true);
		cipUserName.setAccessible(true);
		cipPassword.setAccessible(true);
		genericDataApiUrl.set(dac, "http://private-f559f-streetlifehistoricdataapi.apiary-mock.com");
		storageBaseUrl.set(dac, "http://private-fc206-streetlifemmecp.apiary-mock.com");
		liveDataApiUrl.set(dac, "http://private-91266-streetlifelivedataapi.apiary-mock.com");
		cipClientUrl.set(dac, "https://www.cityintelligenceplatform.siemens.com/cip-mobility/rest/streetlife/parkride");
		cipUserName.set(dac, "mobility");
		cipPassword.set(dac, "<--ENTER CIP PASSWORD HERE-->"");



		dac.init();
	}

	@Test(enabled = false)
	public void getNotifications() {
		Feed notification = dac.getNotifications("some channel id");
		Assert.assertEquals(notification.getTitle(), "Titel des Weblogs");
	}

	@Test(enabled = false)
	public void getChannels() {
		List<Channel> channels = dac.getChannels();
		Assert.assertNotNull(channels.get(FIRST_ELEMENT).isStandard());
	}

	@Test(enabled = false)
	public void getNotification() {
		Entry notification = dac.getNotification("some channel id", "a field", "an ordering");
		Assert.assertEquals(notification.getTitle(), "Traffic Jam - last one");
	}

	@Test(enabled = false)
	public void postNotification() throws IOException, URISyntaxException {
		Entry entry = new Entry();
		entry.setTitle("Hello World");
		Content content = new Content();
		content.setType(MediaType.TEXT_HTML_TYPE);
		content.setText("Nothing much");
		entry.setContent(content);
		Response response = dac.postNotification("some channel Id", entry);
		Assert.assertEquals(response.getStatus(), Response.Status.CREATED.getStatusCode());
	}

	@Test(enabled = false)
	public void deleteNotification() {
		Assert.assertEquals(dac.deleteNotification("some channel Id", "some notification Id", DELETE).getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
	}

	@Test(enabled = false)
	public void archiveNotification() {
		Assert.assertEquals(dac.deleteNotification("some channel Id", "some notification Id", ARCHIVE).getStatus(),
				Response.Status.NO_CONTENT.getStatusCode());
	}

	@Test(enabled = false)
	public void getChannelForm() {
		List<Form> channelForms = dac.getChannelForm("some channel id");

		Assert.assertEquals(channelForms.get(FIRST_ELEMENT).getValue(), "rov_routing");
	}
}
