package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic;

import de.fhg.fokus.streetlife.mmecp.configurator.MMECPConfig;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api.AtomClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api.CIPClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api.GenericDataApiClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api.LiveDataClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.GetDataResponseImpl;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.json.GetDataResponseBean;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.*;
import org.apache.http.client.HttpClient;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.HttpClientConnectionManager;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.jboss.resteasy.client.jaxrs.engines.ApacheHttpClient4Engine;
import org.jboss.resteasy.plugins.providers.RegisterBuiltin;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;
import org.jboss.resteasy.spi.NotImplementedYetException;
import org.jboss.resteasy.spi.ResteasyProviderFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.*;

/**
 * Created by benjamin on 20.08.14.
 */
@Stateless
public class DataAggregatorClientImpl implements DataAggregatorClient {

    private static final ObjectMapper OM = new ObjectMapper();
    private final static Logger LOG = LoggerFactory.getLogger(DataAggregatorClientImpl.class);
    private AtomClient atom;
    protected GenericDataApiClient genericDataApi;
    protected LiveDataClient liveDataClient;
    protected CIPClient cipClient;
    protected boolean trustAllCertificates = true;
    
    @Inject
    @MMECPConfig(value = "storage.url.base")
    protected String storageBaseUrl;

    @Inject
    @MMECPConfig(value = "generic.data.api.url")
    protected String genericDataApiUrl;

    @Inject
    @MMECPConfig(value = "live.data.api.url")
    protected String liveDataApiUrl;

    @Inject
    @MMECPConfig(value = "cip.api.url")
    protected String cipClientUrl;


    @Inject
    @MMECPConfig(value = "cip.username")
    protected String cipUserName;

    @Inject
    @MMECPConfig(value = "cip.password")
    protected String cipPassword;

    public String getStorageBaseUrl(){
        return this.storageBaseUrl;
    }

    public String getGenericDataApiUrl(){
        return this.genericDataApiUrl;
    }

    protected void initValues(){
        // ugly quick fix as long ejbs are not working
        //<storage.url.base>http://private-f5ae7-streetlifemmecp.apiary-mock.com</storage.url.base>
         // <mmecp.backend.api.websocket.url>ws://localhost:8080/api-websocket/</mmecp.backend.api.websocket.url>
         // <generic.data.api.url></generic.data.api.url>
         // <live.data.api.url>http://private-91266-streetlifelivedataapi.apiary-mock.com</live.data.api.url>
         // <cip.api.url></cip.api.url>
         // <berlin.bike.url>http://private-caca6-mmecpapi1.apiary-mock.com</berlin.bike.url>
         // <berlin.event.url>http://private-caca6-mmecpapi1.apiary-mock.com</berlin.event.url>
         // <berlin.profile.url>http://private-caca6-mmecpapi1.apiary-mock.com</berlin.profile.url>
         // <cip.username>mobility</cip.username>
         // <cip.password></cip.password>
        storageBaseUrl = "http://private-f5ae7-streetlifemmecp.apiary-mock.com";
        genericDataApiUrl = "http://private-f559f-streetlifehistoricdataapi.apiary-mock.com";
        liveDataApiUrl = "http://private-91266-streetlifelivedataapi.apiary-mock.com";
        //cipClientUrl = "https://www.cityintelligenceplatform.siemens.com/app-sl-pr/rest/streetlife/parkride";
        cipClientUrl = "https://www.cityintelligenceplatform.siemens.com/streetlife/parkride";
        cipUserName = "mobility";
        cipPassword = "<--ENTER CIP PASSWORD HERE-->";
    }

    public static <T> T convert(JsonNode node, Class<T> clazz) {
        T obj = null;

        try {
            obj = OM.readValue(node, clazz);
        } catch (IOException e) {
            LOG.error("deserialize json node ({}): {}", e.getMessage(), node);
        }

        return obj;
    }

    protected static class Authenticator implements ClientRequestFilter {

        private final String user;
        private final String password;

        public Authenticator(String user, String password) {
            this.user = user;
            this.password = password;
        }

        public void filter(ClientRequestContext requestContext) throws IOException {
            MultivaluedMap<String, Object> headers = requestContext.getHeaders();
            final String basicAuthentication = getBasicAuthentication();
            headers.add("Authorization", basicAuthentication);

        }

        private String getBasicAuthentication() {
            String token = this.user + ":" + this.password;
            try {
                return "Basic " + DatatypeConverter.printBase64Binary(token.getBytes("UTF-8"));
            } catch (UnsupportedEncodingException ex) {
                throw new IllegalStateException("Cannot encode with UTF-8", ex);
            }
        }
    }

    @Override
    @PostConstruct
    public void init() {

        initValues();
        
        RegisterBuiltin.register(ResteasyProviderFactory.getInstance());
        ResteasyClient client;
        if (trustAllCertificates) {
          LOG.warn("Trusting all certificates. Do not use in production mode!");
          ApacheHttpClient4Engine engine;
          try{
             engine = new ApacheHttpClient4Engine(createAllTrustingClient());
          }catch(Exception e){
            LOG.warn("Could not build DataAggregatorClient due to general security exception.",e);
            return;
          }
          client = new ResteasyClientBuilder().httpEngine(engine).build();
        }
        else {
          client = new ResteasyClientBuilder().build();
        }
        
        LOG.info("Register Authentication cipUserName {} cipPassword {}",cipUserName, cipPassword);
        client.register(new Authenticator(cipUserName, cipPassword));
        
        // atom client
        ResteasyWebTarget targetAtomClient = client.target(storageBaseUrl);
        atom = targetAtomClient.proxy(AtomClient.class);
    
        // generic data api client
        ResteasyWebTarget targetGenericDataApiClient = client.target(genericDataApiUrl);
        genericDataApi = targetGenericDataApiClient.proxy(GenericDataApiClient.class); 

        // live data api client
        ResteasyWebTarget targetLiveDataClient = client.target(liveDataApiUrl);
        liveDataClient = targetLiveDataClient.proxy(LiveDataClient.class); 

        // // cip api client
        ResteasyWebTarget targetcipClient = (ResteasyWebTarget)client.target(cipClientUrl);
        cipClient = targetcipClient.proxy(CIPClient.class);

    }

    private static HttpClient createAllTrustingClient() throws GeneralSecurityException {

        org.apache.http.ssl.SSLContextBuilder builder = new org.apache.http.ssl.SSLContextBuilder();
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

        HttpClientConnectionManager ccm = new BasicHttpClientConnectionManager(registry);
        CloseableHttpClient client = HttpClientBuilder.create().setSSLSocketFactory(sslConnectionFactory).setConnectionManager(ccm).build();
        return client;
    }

    @Override
    public Feed getNotifications(String channelId) {
        return atom.getNotifications(channelId);
    }

    @Override
    public Entry getNotification(String channelId, String field, String order) {
        return atom.getNotification(channelId, field, order);
    }

    @Override
    public List<Channel> getChannels() {
        return atom.getChannels();
    }

    @Override
    public Response postNotification(String channelId, Entry notification) {
        return atom.postNotification(channelId, notification);
    }

    @Override
    public Response deleteNotification(String channelId, String notificationId, boolean force) {
        return atom.deleteNotification(channelId, notificationId, force);
    }

    @Override
    public List<Form> getChannelForm(String channelId) {
        return atom.getChannelForm(channelId);
    }

    @Override
    public JsonNode parseResponse(String response) {
        throw new NotImplementedYetException("Not yet implemented...");
    }

    @Override
    public Keys getKeys(){
        return genericDataApi.getKeys();
    }

    @Override
    public GetDataResponse getData(GetDataRequest request){
        JsonNode response = genericDataApi.getData(request);

        return (GetDataResponseImpl)(new GetDataResponseImpl(convert(response, GetDataResponseBean.class)));
    }

    @Override
    public ScalarAttributes getScalarAttributes(){
        return liveDataClient.getScalarAttributes();
    }

    @Override
    public NotificationAttributes getNotificationAttributes(){
        return liveDataClient.getNotificationAttributes();
    }

    @Override
    public LatestScalars getLatestScalars(LiveDataRequest liveDataRequest){
        return liveDataClient.getLatestScalars(liveDataRequest);
    }

    @Override
    public LatestNotifications getLatestNotifications(LiveDataRequest liveDataRequest){
        return liveDataClient.getLatestNotifications(liveDataRequest);
    }

    @Override
    public LiveDataResponse subscribe(LiveDataRequest liveDataRequest){
        return liveDataClient.subscribe(liveDataRequest);
    }

    @Override
    public LiveDataResponse unsubscribe(LiveDataRequest liveDataRequest){
        return liveDataClient.unsubscribe(liveDataRequest);
    }

    @Override
    public List<String> getScalarAttributesCIP(){

        Map<String, List<String>> attributeIdentifierMap = cipClient.getScalarAttributes((Object)(new String("")));


        List<String> attributeIdentifiers = attributeIdentifierMap.get("attributeIdentifiers");

        LOG.info("getScalarAttributes CIP returned attribute List "+ attributeIdentifiers+ " with "+attributeIdentifiers.size()+" elements.");
        if(attributeIdentifiers.size()!=1){
            // dont know how to handle with multiple elements at the moment
            return new ArrayList<String>();
        }else{
            return splitArrayString("\"["+attributeIdentifiers.get(0)+"]\"");
        }

    }

    protected List<String> splitArrayString(String arrayString){
        String withoutSpacesArrayString = arrayString.trim().replace(" ","");
        String tmp;
        if(withoutSpacesArrayString.startsWith("\"[") && withoutSpacesArrayString.endsWith("]\"")){
            // remove begining and ending
            tmp = withoutSpacesArrayString.substring(2,withoutSpacesArrayString.length()-2);
        }else{
            LOG.info("Could not split arrayString {} due to false format.", arrayString);
            return null;
        }
        String[] splittedArrayString = tmp.split(",");
        return Arrays.asList(splittedArrayString);
    }

    protected String createMultiParamQueryString(List<String> attributes){

        if(attributes == null || attributes.size() == 0){
            return "";
        }

        String concatAttributes = "";

        for(String attribute:attributes){
            concatAttributes+=attribute+",";
        }

        return concatAttributes.substring(0,concatAttributes.length()-1);
    }

    @Override
    public List<Map<String, String>> getScalarValuesCIP(List<String> attributes){
        // Build Request Object
        Map<String, Map<String, String>> requestMap = new HashMap<String, Map<String, String>>();
        Map<String, String> attributeIdentifiers = new HashMap<String, String>();

        requestMap.put("attributeIdentifiers", attributeIdentifiers);

        for(int i=1;i<=attributes.size();i++){
            attributeIdentifiers.put("attribute"+i, attributes.get(i-1));
        }

        LOG.info("getScalarValues requestMap is: {}", requestMap.toString());
        LOG.info("Calling getScalarValuesCIP...");
        List<Map<String, String>> result = cipClient.getScalarValues(requestMap);

        LOG.info("Successfully called getScalarValuesCIP...");
        return result;
    }

}
