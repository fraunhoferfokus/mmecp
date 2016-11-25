package de.fhg.fokus.streetlife.mmecp.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Properties;

/**
 * Created by benjamin on 21.08.14.
 */
@ApplicationScoped
public class SystemConfiguration {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

    private Properties props;

    @PostConstruct
    public void init(){
		InputStream in = null;
		props = new Properties();
		String path = System.getenv("MMECP_HOME");
		if (path != null && !path.isEmpty()) {
			try {
				in = new FileInputStream(path + "/mmecp.properties");
				props.load(in);
				LOG.info("User defined configuration loaded successfully!");
				return;
			} catch (FileNotFoundException e) {
				LOG.error("Can't find user defined configuration in {}", path);
			} catch(IOException e){
				LOG.error("Can't find user defined configuration in {}", path);
			}
		} else {
			LOG.warn("Environment variable MMECP_HOME not set!");
		}
		// maybe classloader bug?
		// in = this.getClass().getResourceAsStream("/mmecp.properties");
        in = SystemConfiguration.class.getResourceAsStream("/mmecp.properties");
        try{
        	props.load(in);
        }catch(IOException e){
        	LOG.warn("Could not load mmecp properties.",e);
        }
		LOG.info("Default configuration will be used!");
	}

    public Properties getProperties () {
        return props;
    }

	/**
	 * Get the basis URL for the storage.
	 *
	 * @return The storage URL as URL
	 * @throws MalformedURLException
	 *             Thrown if the URL is not a valid URL
	 */
    public URL getStorageBaseUrl() throws MalformedURLException {
        return new URL(props.getProperty("storage.url.base"));
    }

	/**
	 * Get the basis URL for the websocket.
	 *
	 * @return The websocket URL as URL
	 * @throws MalformedURLException
	 *             Thrown if the URL is not a valid URL
	 */
    public URL getWebSocketBaseUrl() throws MalformedURLException {
        return new URL(props.getProperty("mmecp.backend.api.websocket.url"));
    }

	/**
	 * Get the relative context path of the the PanelUi endpoint.
	 *
	 * @return The panelUi endpoint context path as String
	 */
    public String getEndPointPanelUi(){
        return props.getProperty("mmecp.backend.api.websocket.endpoint.panelui");
    }

}
