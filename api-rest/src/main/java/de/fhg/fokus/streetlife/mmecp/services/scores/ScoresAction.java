package de.fhg.fokus.streetlife.mmecp.services.scores;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

@Path("scores/")
public interface ScoresAction {

    @GET
    @Path("test")
    public String test();

    @GET
    @Produces("application/json")
    @Path("getall")
    public Response getAllScores();

    @GET
    @Produces("text/plain")
    @Path("cutoff")
    public String setCutoffDate(@QueryParam("date") String dateString);

}
