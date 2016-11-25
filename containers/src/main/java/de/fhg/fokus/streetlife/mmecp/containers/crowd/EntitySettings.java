package de.fhg.fokus.streetlife.mmecp.containers.crowd;

/**
 * Created by csc on 07.10.2015.
 */
public class EntitySettings {

    private Integer streetlifeUserId = 0;
    private String  deviceId = "";
    private String  model = "";
    private String  appVersion = "";
    private String  googleIdHash = "";
    private String  creationTimeStamp = "";

    /*
        "streetlifeUserId": 43,
        "deviceId": "e2e0c9ca08015a79",
        "model": "Nexus 4",
        "appVersion": "0.0.1",
        "googleIdHash": "314694c62571dd5c9c56723a27156c668288020382db056c607bbc3487d049475ac8af22292caa14e47d6f6847f715cb17858dd8c22d86e497ff14ce22949608",
        "creationTimeStamp": "2014-10-24T14:48:02"

     */

    public Integer getStreetlifeUserId() {return streetlifeUserId;}
    public void setStreetlifeUserId(Integer streetlifeUserId) {this.streetlifeUserId = streetlifeUserId;}

    public String getDeviceId() {return deviceId;}
    public void setDeviceId(String deviceId) {this.deviceId = deviceId;}

    public String getModel() {return model;}
    public void setModel(String model) {this.model = model;}

    public String getAppVersion() {return appVersion;}
    public void setAppVersion(String appVersion) {this.appVersion = appVersion;}

    public String getGoogleIdHash() {return googleIdHash;}
    public void setGoogleIdHash(String googleIdHash) {this.googleIdHash = googleIdHash;}

    public String getCreationTimeStamp() {return creationTimeStamp;}
    public void setCreationTimeStamp(String creationTimeStamp) {this.creationTimeStamp = creationTimeStamp;}


}
