package de.fhg.fokus.streetlife.mmecp.services;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Channel;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Form;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.List;

@Path("/subscription")
public class SubscriptionApi {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	protected DataAggregatorClient dac;

	// public SubscriptionApi() {
	// init();
	// }

	@PostConstruct
	public void init() {
		dac = DataAggregatorFactory.newInstance("POJO").getClient();
		dac.init();
	}

	@GET
	@Produces("application/atom+xml")
	@Path("channel/{channelId}/notification")
	public Feed getChannelNotifications(@PathParam("channelId") String channelId) {
		return dac.getNotifications(channelId);
	}

	@POST
	@Consumes("application/atom+xml")
	@Path("channel/{channelId}/notification")
	public Response postChannelNotification(@PathParam("channelId") String channelId, Entry notification) {
		LOG.info("Posting new notification with title ({} [{}]) to channel ({})", notification.getTitle(), notification.getId(), channelId);
		return dac.postNotification(channelId, notification);
	}

	@GET
	@Produces("application/atom+xml")
	@Path("channel/{channelId}/notification")
	public Entry getChannelNotification(@PathParam("channelId") String channelId, @QueryParam("field") String field,
			@QueryParam("order") String order) {
		return dac.getNotification(channelId, field, order);
	}

	@GET
	@Produces("application/json")
	@Path("channel")
	public List<Channel> getChannels() {
		return dac.getChannels();
	}

	@DELETE
	@Path("channel/{channelId}/notification/{notificationId}")
	public Response deleteNotification(@PathParam("channelId") String channelId, @PathParam("notificationId") String notificationId,
			@QueryParam("force") boolean force) {
		if (force) {
			LOG.info("Deleting notification ({}) from channel ({})", notificationId, channelId);
		} else {
			LOG.info("Archiving notification ({}) from channel ({})", notificationId, channelId);
		}
		return dac.deleteNotification(channelId, notificationId, force);
	}

	@GET
	@Produces("application/json")
	@Path("form/{channelId}")
	public List<Form> getChannelForm(@PathParam("channelId") String channelId) {
		return dac.getChannelForm(channelId);
	}

	@GET
	@Path("test")
	public String test() {
		return "Hello World!";
	}

}
