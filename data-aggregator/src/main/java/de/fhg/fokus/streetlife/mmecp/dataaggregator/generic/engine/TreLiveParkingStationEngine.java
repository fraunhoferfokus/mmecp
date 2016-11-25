package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.JsonNodeFactory;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * Created by flo on 07/01/15.
 */
public class TreLiveParkingStationEngine implements ResponseParseEngine {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	private static final String USECASENAME = "LiveParkingStation@Trento";
	public String getUseCaseID() {return USECASENAME;}

	// private JsonNode exampleData = null;

	private static final ObjectMapper Mapper = new ObjectMapper();
	private static final JsonNodeFactory Factory = JsonNodeFactory.instance;

	public TreLiveParkingStationEngine() {
	}

	/**
	 * Parses the given response in order to render it as a panel ui component. Response must be an json array representation. For each entry
	 * in the array the transform method will be executed. If an entry can not be parsed it will be omitted.
	 *
	 * @param response the response to parse
	 * @return the parsed response
	 */
    public String parseResponse(String response) {
		if (response == null) return null;
		if (response.isEmpty()) return "";

		LOG.info("Parsing response.");
		JsonNode jsonInput = toJson(response);
    	ArrayNode result = new ArrayNode(Factory);

    	for (JsonNode element : jsonInput){
    		String transformed = transform(element.toString());

    		try {
	    		JsonNode transformedJson = Mapper.readTree(transformed);
				// LOG.info("--- {} ---", transformedJson);
	    		result.add(transformedJson);
    		} catch(IOException e){
    			LOG.warn("Could not read json {}",transformed,e);
    		}
    	}
		// 	LOG.info(Util.shorten(result.toString(),1111)) ;
		return result.toString();
    }

	@Override
	public String handleEvent(Event event) {
		return null;
	}

	/**
     * Transform a given tre live data entry into a panel ui component. If the mapping can not be accomplished it returns an empty string.
	 *
	 * @param input the live data entry as json string
	 * @return the transformed entry or null if there was an error.
	 * 
     */
	protected String transform(String input) {
		if (input == null) return null;
		if (input.isEmpty()) return "";

		JsonNode jsonInput = toJson(input);
		
		ObjectNode root = new ObjectNode(Factory);

		root.put("type", "mapobject");
		root.put("objectID", jsonInput.get("code"));
		root.put("objectType", "ParkingStation");
		root.put("objectSubtype", "tre_park_live");
		root.put("description", "Parkingslot");

		ObjectNode location = new ObjectNode(Factory);

		ArrayNode coordinates = new ArrayNode(Factory);
		coordinates.add(jsonInput.get("x").asInt());
		coordinates.add(jsonInput.get("y").asInt());

		location.put("type", "Point");
		location.put("coordinateSystem", "EPSG:2393");
		location.put("coordinates", coordinates);

		root.put("location", location);

		ArrayNode elements = new ArrayNode(Factory);

		String status = jsonInput.get("status").asText();
		String trend  = jsonInput.get("trend").asText();
		String name1  = jsonInput.get("name1").asText();

		elements.add(createElementNode("Status", status));
		elements.add(createElementNode("Trend", this.rewriteTrend(trend)));
		elements.add(createElementNode("Name", name1));

		elements.add(createArrowedCircleNode(jsonInput.get("x").asInt(), jsonInput.get("y").asInt(), status, trend));

		root.put("elements", elements);

		try{
			return Mapper.writeValueAsString(root);
		}catch(Exception e){
			LOG.warn("Could not transform the given input string.", e);
			return null;
		}
	}


	private String rewriteTrend( String trend ) {
		if ("increasing".equals(trend)) {
			return "more cars are coming";
		} else if ("decreasing".equals(trend)) {
			return "more cars are leaving";
		} else if ("stable".equals(trend)) {
			return "occupancy is stable";
		} else {
			return trend;
		}
	}


	protected JsonNode toJson(String data) {
		if (data == null) return null;
        try {
            return Mapper.readTree(data);
        } catch (IOException e) {
            LOG.error("deserialize json node ({}): {}", e.getMessage(), data);
	    	return null;
        }
	}


	protected ObjectNode createArrowedCircleNode(int x, int y, String status, String trend) {

		ObjectNode arrowedCircleNode = new ObjectNode(Factory);
		ObjectNode arrowedCircleEntries = new ObjectNode(Factory);
		ObjectNode circle = new ObjectNode(Factory);

		circle.put("x", x);
		circle.put("y", y);

		ObjectNode color = new ObjectNode(Factory);
		String icon = "icon";

		if("full".equals(status)){
			color.put("red", 125);
			color.put("green", 0);
			color.put("blue", 0);
			icon+="_red";
		}else if("spacesAvailable".equals(status)){
			color.put("red", 0);
			color.put("green", 125);
			color.put("blue", 0);
			icon+="_green";
		}

		String arrowType = null;

		if("increasing".equals(trend)){
			arrowType = "down";
			icon+="_arrow_down";
		}else if("stable".equals(trend)){
			arrowType = "middle";
			icon+="_arrow_straight";
		}else if("decreasing".equals(trend)){
			arrowType = "up";
			icon+="_arrow_up";
		}

		arrowedCircleEntries.put("circle", (ObjectNode)circle);
		arrowedCircleEntries.put("color", (ObjectNode)color);
		arrowedCircleEntries.put("arrowType", arrowType);
		arrowedCircleEntries.put("icon", icon);

		arrowedCircleNode.put("arrowedCircle", arrowedCircleEntries);

		return arrowedCircleNode;
	}


	protected ObjectNode createElementNode(String label, String value) {
		ObjectNode arrayNodeElement = new ObjectNode(Factory);
		ObjectNode element = new ObjectNode(Factory);

		if (label != null) {element.put("label", label);}
		if (value != null) {element.put("value", value);}

		arrayNodeElement.put("attribute", element);

		return arrayNodeElement;
	}


}
