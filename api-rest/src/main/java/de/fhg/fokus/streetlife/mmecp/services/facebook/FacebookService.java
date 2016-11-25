package de.fhg.fokus.streetlife.mmecp.services.facebook;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

/**
 * Created by csc on 11.02.2016.
 */
@RequestScoped
public class FacebookService implements FacebookAction {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final ObjectMapper mapper = new ObjectMapper();

    private static final HttpHost target = new HttpHost("graph.facebook.com", 443, "https");

    private static final String facebookBaseUrl = "/v2.5/oauth/access_token?grant_type=fb_exchange_token" +
            "&client_id=927583730682087&client_secret=e13d4201599a50e29d10405997d9192b&fb_exchange_token=";

    @Inject
    SqLiteAdapter sqLiteAdapter;

    @Override
    public Response saveFacebookAccessToken(String deviceId, String facebookUserId, String facebookAccessToken) {

        LOG.info("deviceid IS: " + deviceId);
        LOG.info("fb_userid IS: " + facebookUserId);
        LOG.info("fb_shortaccesstoken IS: " + facebookAccessToken);

        HttpGet getRequest = new HttpGet(facebookBaseUrl + facebookAccessToken);

        try (CloseableHttpClient httpClient = HttpClientBuilder.create().build()) {

            HttpResponse response = httpClient.execute(target, getRequest);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode < 200 || statusCode >= 300) {
                throw new IOException("Error during facebook access: http " + statusCode);
            }

            HttpEntity entity = response.getEntity();
            if (entity == null) {
                throw new IOException("got no data back via facebook http GET access_token request");
            }

            // Ergebnis wenn erfolgreich ist im Format:
            // {"access_token":"oreigreouierqozugbvob","token_type":"bearer","expires_in":5176374}

            JsonNode root = mapper.readTree(EntityUtils.toString(entity));
            String longToken = root.get("access_token").asText();
            String tokenType = root.get("token_type").asText();
            Integer expires = root.get("expires_in").asInt();

            sqLiteAdapter.updateFacebookEntry(facebookUserId, facebookAccessToken, longToken, tokenType, expires, deviceId);

            String reply = "Okay. got long token and stored it in database; long token size=" + longToken.length();
            return Response.ok(reply, MediaType.TEXT_PLAIN).build();

        } catch (IOException e) {
            LOG.error("Could not safe Facebook token: " + e.toString());
            return Response.serverError().status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }


    @Override
    public Response sendFeedbackEMail(String senderName, String messageSubject, String messageBody, String eMail) {

        LOG.info("sendername IS: " + senderName);
        LOG.info("messagesubject IS: " + messageSubject);
        LOG.info("messagebody IS: " + messageBody);
        LOG.info("email IS: " + eMail);

        try {
            sendEMail(senderName, messageSubject, messageBody, eMail);
            return Response.ok("Feedback E-Mail was sent.", MediaType.TEXT_PLAIN).build();
        } catch (MessagingException e) {
            LOG.warn("Could not send feedback E-Mail because: " + e.toString());
            return Response.serverError().status(Response.Status.SERVICE_UNAVAILABLE).build();
        }
    }


    /**
     * Send email using the site-local SMTP server.
     *
     * @param senderName name of the message author
     * @param subject title of the message
     * @param body message to be sent
     * @throws AddressException if the email address parse failed
     * @throws MessagingException if the connection is dead or not in the connected state or if the message is not a MimeMessage
     */
    public void sendEMail(String senderName, String subject, String body, String eMail) throws MessagingException {

        final String MailServer      = "smtpsrv.fokus.fraunhofer.de";
        final String FromSenderEmail = "no-reply-sl@fokus.fraunhofer.de";

        final String[] RecipientEmails = {
//                "manuel.polzhofer@fokus.fraunhofer.de",
//                "carsten.schmoll@fokus.fraunhofer.de",
                "streetlife-fhg-tech@fokus.fraunhofer.de" };

        Properties properties = new Properties();
        properties.put("mail.smtp.host", MailServer);
        properties.put("mail.smtp.port", "25");
        properties.put("mail.smtp.auth", true);
        properties.put("mail.smtps.quitwait", "false");
        properties.put("mail.smtp.starttls.enable", "false");
        properties.put("mail.smtp.auth", "false");
        properties.put("mail.debug", "true");

        Session session = Session.getDefaultInstance(properties);
        LOG.info("SESSION IS: " + session);

        final MimeMessage message = new MimeMessage(session);
        LOG.info("MESSAGE IS: " + message);

        message.setFrom(new InternetAddress(FromSenderEmail));

        //message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RecipientEmail, false));
        for (String s : RecipientEmails) {
            message.addRecipient(Message.RecipientType.TO, new InternetAddress(s));
        }

        message.setSubject(subject);
        message.setText("Sender called him/herself: " + senderName + ", e-mail: " + eMail +
                          "\n\nMessage is:\n" + body, "utf-8");
        message.setSentDate(new Date());

        Transport.send(message);
    }


}
