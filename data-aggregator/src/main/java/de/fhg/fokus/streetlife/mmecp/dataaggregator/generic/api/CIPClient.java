package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.api;

import org.codehaus.jackson.JsonNode;
import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.Map;



/*
 * created by flo (florian.thiemer@fokus.fraunhofer.de) - 25.5.2015
 * ugly hotfix because of naming convention. 
 */
public interface CIPClient {
	
	@POST
	@Path("getScalarAttributes")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public Map<String, List<String>> getScalarAttributes(Object o);

	@POST
	@Path("getScalarValues")
	@Produces(value = MediaType.APPLICATION_JSON)
	@Consumes(value = MediaType.APPLICATION_JSON)
	public List<Map<String,String>> getScalarValues(Map<String, Map<String, String>> attributeIdentifiers);

}