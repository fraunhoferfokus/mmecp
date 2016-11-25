package de.fhg.fokus.streetlife.mmecp.containers.rovsim;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

@Path("rovsim/action/")
public interface RovSimAction {

    @GET
    @Path("test")
    public String test();

    @POST
    @Produces("application/json")
    @Consumes("application/json")
    @Path("runsim")
    Response runSimulation(String body);

    @GET
    @Produces("application/json")
    @Path("calcdemand")
    Response calcParkingDemand(@QueryParam("rideservices") String rideServices,
                               @QueryParam("cap")          Integer capacity,
                               @QueryParam("flow")         Integer passingFlow,
                               @QueryParam("regulation")   Integer regulation,
                               @QueryParam("alg")          Integer method);

}
