package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api;

import org.codehaus.jackson.JsonNode;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ScalarAttributes;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.NotificationAttributes;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LatestScalars;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LatestNotifications;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LiveDataRequest;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LiveDataResponse;


/*
 * created by flo (florian.thiemer@fokus.fraunhofer.de) - 20.5.2015
 */
public interface LiveDataClient {
	
	@GET
	@Path("scalar_attributes")
	@Produces(value = MediaType.APPLICATION_JSON)
	public ScalarAttributes getScalarAttributes();

	@GET
	@Path("notification_attributes")
	@Produces(value = MediaType.APPLICATION_JSON)
	public NotificationAttributes getNotificationAttributes();

	@POST
	@Path("latest_scalars")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public LatestScalars getLatestScalars(LiveDataRequest scalarsRequest);

	@POST
	@Path("latest_notifications")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public LatestNotifications getLatestNotifications(LiveDataRequest scalarsRequest);

	@POST
	@Path("subscribe")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public LiveDataResponse subscribe(LiveDataRequest subscribeRequest);

	@POST
	@Path("unsubscribe")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public LiveDataResponse unsubscribe(LiveDataRequest unSubscribeRequest);

}