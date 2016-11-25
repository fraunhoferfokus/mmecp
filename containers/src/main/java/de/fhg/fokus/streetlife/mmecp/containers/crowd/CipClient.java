package de.fhg.fokus.streetlife.mmecp.containers.crowd;

import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.ssl.SSLContextBuilder;
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
public class CipClient {

    private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private final String CIPbaseUrl = "https://www.cityintelligenceplatform.siemens.com";
    //private final String CIPbaseUrl = "https://www.cip.siemens.com/";

    private ResteasyClient resteasyClient;
    private ResteasyWebTarget target;

    public CipClient() throws GeneralSecurityException {
        this.init();
    }

    public ResteasyClient getResteasyClient() {
        return resteasyClient;
    }


    private static HttpClient createAllTrustingClient() throws GeneralSecurityException {

        SSLContextBuilder builder = new SSLContextBuilder();
        builder.loadTrustMaterial(null, new TrustStrategy() {
            @Override
            public boolean isTrusted(java.security.cert.X509Certificate[] chain, String authType) throws java.security.cert.CertificateException {
                return true;
            }
        });

        SSLConnectionSocketFactory sslConnectionFactory = new SSLConnectionSocketFactory(builder.build(), SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER);

        Registry<ConnectionSocketFactory> registry = RegistryBuilder.<ConnectionSocketFactory>create()
                .register("https", sslConnectionFactory)
                .build();

        HttpClientConnectionManager ccm = new PoolingHttpClientConnectionManager(registry);
        CloseableHttpClient client = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionFactory).setConnectionManager(ccm).build();
        return client;
    }

    public ResteasyWebTarget getTarget() {
        return target;
    }

//    public HttpClient createAllTrustingClient() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
//        HttpClientBuilder b = HttpClientBuilder.create();
//
//        // setup a Trust Strategy that allows all certificates.
//        //
//        SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (arg0, arg1) -> true).build();
//        b.setSslcontext(sslContext);
//
//        // don't check Hostnames, either.
//        //      -- use SSLConnectionSocketFactory.getDefaultHostnameVerifier(), if you don't want to weaken
//        HostnameVerifier hostnameVerifier = SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER;
//
//        // here's the special part:
//        //      -- need to create an SSL Socket Factory, to use our weakened "trust strategy";
//        //      -- and create a Registry, to register it.
//        //
//        SSLConnectionSocketFactory sslSocketFactory = new SSLConnectionSocketFactory(sslContext, hostnameVerifier);
//        Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
//                .register("http", PlainConnectionSocketFactory.getSocketFactory())
//                .register("https", sslSocketFactory)
//                .build();
//
//        // now, we create connection-manager using our Registry.
//        //      -- allows multi-threaded use
//        PoolingHttpClientConnectionManager connMgr = new PoolingHttpClientConnectionManager(socketFactoryRegistry);
//        b.setConnectionManager(connMgr);
//
//        // finally, build the HttpClient;
//        //      -- done!
//        HttpClient client = b.build();
//        return client;
//    }

    // @PostConstruct
    public void init() throws GeneralSecurityException {
        this.init(true);
    }

    public void init(boolean trustAllCertificates) throws GeneralSecurityException {

        ResteasyClientBuilder cliBuilder;
        if (trustAllCertificates) {
            LOG.warn("Trusting all certificates. Do not use in production mode!");
            cliBuilder = new ResteasyClientBuilder().httpEngine(new ApacheHttpClient4Engine(createAllTrustingClient()));
//            cliBuilder = new ResteasyClientBuilder();
        } else {
            cliBuilder = new ResteasyClientBuilder();
        }
        // ResteasyClient client = new ResteasyClientBuilder().build().register(new Authenticator());
        this.resteasyClient = cliBuilder.build().register(new Authenticator());
        this.target = resteasyClient.target(CIPbaseUrl);
    }

    private class Authenticator implements ClientRequestFilter {
        public void filter(ClientRequestContext clientRequestContext) throws IOException {
            MultivaluedMap<String, Object> headers = clientRequestContext.getHeaders();
            headers.add("content-type", "application/json");
            headers.add("authorization", "Basic <--ADD CIP PASSWORD HASH -->");
        }
    }


}
