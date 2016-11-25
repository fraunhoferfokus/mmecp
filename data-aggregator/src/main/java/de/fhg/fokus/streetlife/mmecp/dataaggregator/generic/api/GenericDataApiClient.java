package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api;

import org.codehaus.jackson.JsonNode;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Keys;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.GetDataRequest;

/*
 * created by flo (florian.thiemer@fokus.fraunhofer.de) - 22.4.2015
 */
public interface GenericDataApiClient{
	
	@GET
	@Path("data/keys")
	public Keys getKeys();


	@POST
    @Path("data")
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public JsonNode getData(GetDataRequest request);

}