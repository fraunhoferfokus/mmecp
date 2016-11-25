package de.fhg.fokus.streetlife.mmecp.containers.vmzlife;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

public interface VmzAction {

    @POST
    @Path("/")
    @Produces(value = MediaType.TEXT_XML)
    @Consumes(value = MediaType.TEXT_XML)
    Response getContentInfo(Object obj);

}
