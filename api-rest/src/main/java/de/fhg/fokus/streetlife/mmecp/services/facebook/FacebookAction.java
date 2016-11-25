package de.fhg.fokus.streetlife.mmecp.services.facebook;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;

/**
 * Created by csc on 22.10.2015.
 */

@Path("facebook/")
public interface FacebookAction {

    @POST
    @Path("saveaccesstoken")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    Response saveFacebookAccessToken(@FormParam("deviceid") String deviceId,
                                     @FormParam("fb_userid") String facebookUserId,
                                     @FormParam("fb_shortaccesstoken") String facebookAcceesToken);

    @POST
    @Path("sendfeedbackemail")
    @Consumes("application/x-www-form-urlencoded")
    @Produces("text/plain")
    Response sendFeedbackEMail(@DefaultValue("not specified") @FormParam("sendername") String senderName,
                             @DefaultValue("no subject") @FormParam("messagesubject") String messageSubject,
                             @DefaultValue("no message") @FormParam("messagebody") String messageBody,
                             @DefaultValue("no e-mail") @FormParam("email") String eMail);

}
