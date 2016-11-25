package de.fhg.fokus.streetlife.mmecp.dataaggregator;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Channel;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Form;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.Keys;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.GetDataRequest;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.GetDataResponse;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.ScalarAttributes;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.NotificationAttributes;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LatestNotifications;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LatestScalars;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LiveDataRequest;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.LiveDataResponse;
import org.codehaus.jackson.JsonNode;
import org.jboss.resteasy.plugins.providers.atom.Entry;
import org.jboss.resteasy.plugins.providers.atom.Feed;

import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Map;

public interface DataAggregatorClient {

	public void init();

    public String getStorageBaseUrl();

    public String getGenericDataApiUrl();

	public Feed getNotifications(String channelId);

    public Entry getNotification(String channelId, String field, String order);

	public List<Channel> getChannels();

    public Response postNotification(String channelId, Entry notification);

    public Response deleteNotification(String channelId, String notificationId, boolean force);

    public List<Form> getChannelForm(String channelId);

    public JsonNode parseResponse(String response);

    public Keys getKeys();

    public GetDataResponse getData(GetDataRequest request);

    public ScalarAttributes getScalarAttributes();

    public NotificationAttributes getNotificationAttributes();

    public LatestScalars getLatestScalars(LiveDataRequest liveDataRequest);

    public LatestNotifications getLatestNotifications(LiveDataRequest liveDataRequest);

    public LiveDataResponse subscribe(LiveDataRequest liveDataRequest);

    public LiveDataResponse unsubscribe(LiveDataRequest liveDataRequest);

    public List<String> getScalarAttributesCIP();

    public List<Map<String, String>> getScalarValuesCIP(List<String> attributes);
}
