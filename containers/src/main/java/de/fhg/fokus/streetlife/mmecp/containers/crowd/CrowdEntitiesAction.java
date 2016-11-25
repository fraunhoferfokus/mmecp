package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

public interface CrowdEntitiesAction {

    @GET
    @Path("/entities")
    @Produces("application/json")
    Response getAllEntities();

}
