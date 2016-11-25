package de.fhg.fokus.streetlife.mmecp.containers.vmzlife;

import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.BasicClientConnectionManager;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import java.io.IOException;
import java.security.GeneralSecurityException;


/**
 * Created by csc on 07.10.2015.
 */
public class VmzClient {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String VMZbaseUrl = "https://werkzeug.dcaiti.tu-berlin.de/team/vmz";

    private ResteasyClient resteasyClient;
    private ResteasyWebTarget target;

    public ResteasyClient getResteasyClient() {return resteasyClient;}
    public ResteasyWebTarget getTarget() {return target;}


    private static DefaultHttpClient createAllTrustingClient() throws GeneralSecurityException {
        SchemeRegistry registry = new SchemeRegistry();
        registry.register(new Scheme("http", 80, PlainSocketFactory.getSocketFactory()));

        TrustStrategy trustStrategy = new TrustStrategy() {
            public boolean isTrusted(java.security.cert.X509Certificate[] arg0,
                                     String arg1) throws java.security.cert.CertificateException {
                return true;
            }
        };
        SSLSocketFactory factory = new SSLSocketFactory(trustStrategy,SSLSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);
        registry.register(new Scheme("https", 443, factory));

        BasicClientConnectionManager mgr = new BasicClientConnectionManager(registry);
        DefaultHttpClient client = new DefaultHttpClient(mgr, new DefaultHttpClient().getParams());
        return client;
    }

    private class Authenticator implements ClientRequestFilter {
        public void filter(ClientRequestContext clientRequestContext) throws IOException {
            MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();
            headers.add("content-type", "text/xml; charset=\"iso-8859-1\"");
            headers.add("SOAPAction", "TMCWebServices#getContentInfoResponse");
            headers.add("authorization", "Basic dm16LXN0cmVldGxpZmUtZWxhbjphbGxvd2VkIUFicm9hZCFza2V3ZWQuRXF1aXBz");
        }
    }

    public VmzClient() throws GeneralSecurityException {
        this.init();
    }

    // @PostConstruct
    public void init() throws GeneralSecurityException {
        this.init(true);
    }

    public void init(boolean trustAllCertificates) throws GeneralSecurityException {

        ResteasyClientBuilder cliBuilder;
        if (trustAllCertificates) {
            LOG.warn("Trusting all certificates. Do not use in production mode!");
            cliBuilder = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(createAllTrustingClient()));
        }
        else {
            cliBuilder = new ResteasyClientBuilder();
        }
        // ResteasyClient client = new ResteasyClientBuilder().build().register(new Authenticator());
        this.resteasyClient = cliBuilder.build().register(new Authenticator());
        this.target = resteasyClient.target(VMZbaseUrl);
    }


}
