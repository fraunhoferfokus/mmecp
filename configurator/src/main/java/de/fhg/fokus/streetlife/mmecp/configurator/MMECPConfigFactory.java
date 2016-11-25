package de.fhg.fokus.streetlife.mmecp.configurator;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.enterprise.inject.spi.InjectionPoint;
import javax.inject.Inject;

/**
 * Created by bdi on 07/11/14.
 */
@ApplicationScoped
public class MMECPConfigFactory {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	@Inject
	private SystemConfiguration systemConfiguration;
	
	@Produces
	@MMECPConfig(value = "", defaultValue = "")
	public String createConfigValue(InjectionPoint injectionPoint) {
		String key = injectionPoint.getAnnotated().getAnnotation(MMECPConfig.class).value();
		String defaultKey = injectionPoint.getAnnotated().getAnnotation(MMECPConfig.class).defaultValue();

		String value;

		if (key.isEmpty()) {
			value = systemConfiguration.getProperties().getProperty(defaultKey);
			LOG.info("Value for key {} is empty. Returning default value: {}", key, value);
		} else {
			value = systemConfiguration.getProperties().getProperty(key);
		}

		return value;
	}

}
