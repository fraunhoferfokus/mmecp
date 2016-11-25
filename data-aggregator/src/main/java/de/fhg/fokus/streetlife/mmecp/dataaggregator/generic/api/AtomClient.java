package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Channel;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Form;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;

/**
 * Created by benjamin on 19.08.14.
 */
public interface AtomClient {

	@GET
	@Path("subscription/channel/{channelId}/notification")
	@Produces(MediaType.APPLICATION_ATOM_XML)
	public Feed getNotifications(@PathParam("channelId") String channelId);

    @POST
    @Path("subscription/channel/{channelId}/notification")
    @Consumes(MediaType.APPLICATION_ATOM_XML)
    public Response postNotification(@PathParam("channelId") String channelId, Entry notification);

    @GET
    @Path("subscription/channel/{channelId}/notification")
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public Entry getNotification(@PathParam("channelId") String channelId, @QueryParam("field") String field, @QueryParam("order") String order);

    @DELETE
    @Path("subscription/channel/{channelId}/notification/{notificationId}")
    public Response deleteNotification(@PathParam("channelId") String channelId, @PathParam("notificationId") String notificationId, @QueryParam("force") boolean force);

	@GET
	@Path("subscription/channel")
	public List<Channel> getChannels();

    @GET
    @Path("subscription/form/{channelId}")
    public List<Form> getChannelForm(@PathParam("channelId") String channelId);
}
