package de.fhg.fokus.streetlife.mmecp.dataaggregator;

import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.DataAggregatorClientImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.naming.Context;
import javax.naming.InitialContext;
import java.util.Hashtable;

/**
 * Created by benjamin on 20.08.14.
 */
public abstract class DataAggregatorFactory {

	/**
	 * Returns a new DataAggregatorFactory instance.
	 * You can instantiate a factory that lookups a bean instance of a DataAggregatorClient
	 * or a simple plain old java object.
	 * The following types are valid:
	 *  - POJO
	 *  - BEAN
	 */
	public static DataAggregatorFactory newInstance(String type){
		if("POJO".equals(type)){
			return new DataAggregatorPOJOFactory();
		}else if("BEAN".equals(type)){
			return new DataAggregatorBeanFactory();
		}else{
			throw new RuntimeException("No valid type provided for DataAggregatorFactory initialization. Valid types are: 'POJO' and 'BEAN'.");
		}
	}

	public abstract DataAggregatorClient getClient();

	protected static class DataAggregatorPOJOFactory extends DataAggregatorFactory{
		public synchronized DataAggregatorClient getClient(){
			DataAggregatorClient client = new DataAggregatorClientImpl();

			return client;
		}
	}

	protected static class DataAggregatorBeanFactory extends DataAggregatorFactory{

		private final Logger logger = LoggerFactory.getLogger(this.getClass());

		public synchronized DataAggregatorClient getClient() {

	        DataAggregatorClient client = lookupClient();

	        return client;
    	}
	    /**
	     * Finds the DataAggregatorClient Bean.
	     */
	    protected synchronized DataAggregatorClient lookupClient(){
	    	try{
		    	final Hashtable jndiProperties = new Hashtable();
		        jndiProperties.put(Context.INITIAL_CONTEXT_FACTORY, "org.jboss.naming.remote.client.InitialContextFactory");
				jndiProperties.put(Context.PROVIDER_URL,"remote://localhost:4447");
				// username
				jndiProperties.put(Context.SECURITY_PRINCIPAL, "flo");
				// password
				jndiProperties.put(Context.SECURITY_CREDENTIALS, "test123.");
		        final Context context = new InitialContext(jndiProperties);
		        final String appName = "";
		        // This is the module name of the deployed EJBs on the server. This is typically the jar name of the
		        // EJB deployment, without the .jar suffix, but can be overridden via the ejb-jar.xml
		        // In this example, we have deployed the EJBs in a jboss-as-ejb-remote-app.jar, so the module name is
		        // jboss-as-ejb-remote-app
		        final String moduleName = "module";
		        // AS7 allows each deployment to have an (optional) distinct name. We haven't specified a distinct name for
		        // our EJB deployment, so this is an empty string
		        final String distinctName = "";
		        // The EJB name which by default is the simple class name of the bean implementation class
		        final String beanName = DataAggregatorClientImpl.class.getSimpleName();
		        // the remote view fully qualified class name
		        final String viewClassName = DataAggregatorClient.class.getName();
		        // let's do the LookupColor
		        final String lookupString = "DataAggregatorClientImpl!de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient";
		        logger.info("Looking up session bean: "+lookupString);
		        return (DataAggregatorClient) context.lookup(lookupString);
	    	}catch(Exception e){
	    		throw new RuntimeException("Could not LookupColor DataAggregatorClient.", e);
	    	}
	    }	
	}

    
}
