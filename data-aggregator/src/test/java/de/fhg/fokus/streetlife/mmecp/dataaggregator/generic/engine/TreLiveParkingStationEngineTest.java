package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;


import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;


/**
 * Transforms: 
 * [...{ "time": "2015-07-15T13:37:16+03:00",
 *  "trend": "increasing",
 *  "status": "spacesAvailable",
 *  "code": "FNPK.11",
 *  "name1": "P-Plevna",
 *  "y": "6825825",
 *  "x": "3327571"}, ...]
 * into:
 * "type": "mapobject", 
 *   "objectID": "FNPK.11", 
 *   "objectType": "ParkingStation", 
 *   "objectSubtype": "cardblock", 
 *   "description": "Parkingslot", 
 *   "location": {
 *     "type": "Point",
 *	   "coordinateSystem": "EPSG:2393" 
 *     "coordinates": [
 *       3327571,
 *       6825825 
 *     ]
 *   },
 * 	 "elements": [
 *	      {
 *	        "attribute": {
 *	          "label": "Trend", 
 *	          "value": "decreasing"
 *	        }
 *	      },
 * 		 {
 *	 		"attribute": {
 *				"label": "Status",
 *				"value": "spacesAvailable"
 *	 		}
 * 		 },
 *		 {
 *			"arrowedCircle": "circle": {"x": 3327571, "y": "6825825"}, "color": {...}, "icon": "..."
 *		 }
 * 		 ... 
 * 
 */
public class TreLiveParkingStationEngineTest {
	
	protected static final Logger logger = LoggerFactory.getLogger(TreLiveParkingStationEngineTest.class);

	protected TreLiveParkingStationEngine tlpae;

	protected ObjectMapper mapper;

	@BeforeMethod
	public void init(){
		tlpae = new TreLiveParkingStationEngine();
		mapper = new ObjectMapper();
	}

	@Test(enabled = true)
	public void parseResponseShouldCreateForEachEntryATransformedEntry() throws IOException{
		JsonNode input = loadJsonFile("multipleEntries");

		String strParsedResponse = tlpae.parseResponse(input.toString());

		logger.info("sucessfully parsed response {}", strParsedResponse);

		JsonNode parsedResponse = mapper.readTree(strParsedResponse);

		assertThat(parsedResponse.isArray(), is(true));
		assertThat(parsedResponse.size(), is(3));
	}

	@Test(enabled = true)
	public void transformShouldCreateTheStandardProperties() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, hasJsonProperty("type", "mapobject"));
		assertThat(transformed, hasJsonProperty("objectType", "ParkingStation"));
		assertThat(transformed, hasJsonProperty("objectSubtype", "tre_park_live"));
		assertThat(transformed, hasJsonProperty("description", "Parkingslot"));

	}

	@Test(enabled = true)
	public void transformShouldCopyCodeIntoObjectID() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, notNullValue());
		assertThat(transformed, hasJsonProperty("objectID", "FNPK.11"));
	}

	@Test(enabled = true)
	public void transformShouldCopyTrendAsEntryInElement()throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, notNullValue());
		assertThat(transformed, hasElementAttribute("Trend", "more cars are coming"));
	}

	@Test(enabled = true)
	public void transformShouldCopyStatusAsEntryInElement()throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, notNullValue());
		assertThat(transformed, hasElementAttribute("Status", "spacesAvailable"));
	}

	@Test(enabled = true)
	public void transformShouldCopyName1AsEntryInElement()throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, notNullValue());
		assertThat(transformed, hasElementAttribute("Name", "P-Plevna"));
	}

	@Test(enabled = true)
	public void transformShouldCreateTheLocationElements() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		assertThat(transformed, notNullValue());
		assertThat(transformed.get("location"), notNullValue());
		assertThat(transformed.get("location").get("type").asText(), is("Point"));
		assertThat(transformed.get("location").get("coordinateSystem").asText(), is("EPSG:2393"));
		assertThat(mapper.readValue(transformed.get("location").get("coordinates"), Integer[].class), arrayContaining(3327571, 6825825));
	}

	@Test(enabled = true)
	public void shouldCreateArrowedCircleEntry() throws IOException {
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		assertThat(arrowedCircle, notNullValue());
	}

	@Test(enabled = true)
	public void shouldCreateCirclePropertyInArrowedCircleProperty() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode circle = arrowedCircle.get("circle");

		assertThat(circle, notNullValue());
	}

	@Test(enabled = true)
	public void shouldCreateIconPropertyInArrowedCircleProperty() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode icon = arrowedCircle.get("icon");

		assertThat(icon, notNullValue());
	}

	@Test(enabled = true)
	public void shouldUseCoordinatesForArrowedCircleFromLocationElement() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode circle = arrowedCircle.get("circle");

		assertThat(circle.get("y").asInt(), is(6825825));
		assertThat(circle.get("x").asInt(), is(3327571));
	}

	@Test(enabled = true)
	public void shouldUseColorRedForArrowedCircleWhenStatusIsFull() throws IOException {
		JsonNode input = loadJsonFile("input1_status_full");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode color = arrowedCircle.get("color");

		assertThat(color.get("red").asInt(), is(125));
		assertThat(color.get("green").asInt(), is(0));
		assertThat(color.get("blue").asInt(), is(0));
	}

	@Test(enabled = true)
	public void shouldUseColorGreenForArrowedCircleWhenStatusIsSpacesAvailable() throws IOException {
		JsonNode input = loadJsonFile("input1_status_spacesAvailable");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode color = arrowedCircle.get("color");

		assertThat(color.get("red").asInt(), is(0));
		assertThat(color.get("green").asInt(), is(125));
		assertThat(color.get("blue").asInt(), is(0)); 
	}


	@Test(enabled = true)
	public void shouldCreateArrowTypeProperty() throws IOException{
		JsonNode input = loadJsonFile("input1");

		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("arrowType");

		assertThat(arrowType, notNullValue());
	}

	@Test(enabled = true)
	public void shouldSetArrowTypeUpWhenTrendIsDecreasing() throws IOException {
		JsonNode input = loadJsonFile("input1_trend_decreasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("arrowType");

		assertThat(arrowType.asText(), is("up"));
	}

	@Test(enabled = true)
	public void shouldSetArrowTypeMiddleWhenTrendIsStable() throws IOException {
		JsonNode input = loadJsonFile("input1_trend_stable");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("arrowType");

		assertThat(arrowType.asText(), is("middle"));
	}

	@Test(enabled = true)
	public void shouldSetArrowTypeDownWhenTrendIsIncreasing() throws IOException {
		JsonNode input = loadJsonFile("input1_trend_increasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("arrowType");

		assertThat(arrowType.asText(), is("down"));
	}

	@Test(enabled = true)
	public void shouldSetIconGreenArrowDownWhenStatusIsSpacesAvailableAndTrendIsIncreasing() throws IOException{
		JsonNode input = loadJsonFile("input1_status_spacesAvailable_and_trend_increasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_green_arrow_down"));
	}

	@Test(enabled = true)
	public void shouldSetIconGreenArrowStraightWhenStatusIsSpacesAvailableAndTrendIsStable() throws IOException{
		JsonNode input = loadJsonFile("input1_status_spacesAvailable_and_trend_stable");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_green_arrow_straight"));
	}

	@Test(enabled = true)
	public void shouldSetIconGreenArrowUpWhenStatusIsSpacesAvailableAndTrendIsDecreasing() throws IOException{
		JsonNode input = loadJsonFile("input1_status_spacesAvailable_and_trend_decreasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_green_arrow_up"));
	}

	@Test(enabled = true)
	public void shouldSetIconRedArrowDownWhenStatusIsFullAndTrendIsIncreasing() throws IOException{
		JsonNode input = loadJsonFile("input1_status_full_and_trend_increasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_red_arrow_down"));
	}

	@Test(enabled = true)
	public void shouldSetIconRedArrowStraightWhenStatusIsFullAndTrendIsStable() throws IOException{
		JsonNode input = loadJsonFile("input1_status_full_and_trend_stable");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_red_arrow_straight"));
	}

	@Test(enabled = true)
	public void shouldSetIconRedArrowDownWhenStatusIsFullAndTrendIsDecreasing() throws IOException{
		JsonNode input = loadJsonFile("input1_status_full_and_trend_decreasing");
	
		String strTransformed = tlpae.transform(input.toString());

		JsonNode transformed = mapper.readTree(strTransformed);

		JsonNode arrowedCircle = getArrowedCircleElement(transformed);

		JsonNode arrowType = arrowedCircle.get("icon");

		assertThat(arrowType.asText(), is("icon_red_arrow_up"));
	}

	/**
	 * Extracts the element with the property "arrowedCircle"
	 */
	protected JsonNode getArrowedCircleElement(JsonNode node){
		Iterator<JsonNode> elements = node.path("elements").getElements();
		while(elements.hasNext()){
			JsonNode element = elements.next();
			if(element.get("arrowedCircle")!=null){
				return element.get("arrowedCircle");
			}
		}
		return null;
	}

	/**
	 * Hamcrest Matcher for a JsonNode. It returns true, if the JsonNode has an element entry
	 * with the given label and value property.
	 */
	protected Matcher<JsonNode> hasElementAttribute(final String label, final String value){
		return new BaseMatcher<JsonNode>(){
			@Override
			public boolean matches(final Object item){
				JsonNode node = (JsonNode)item;
				Iterator<JsonNode> elements = node.path("elements").getElements();
				while(elements.hasNext()){
					JsonNode element = elements.next();
					JsonNode attribute = element.get("attribute");
					if(label.equals(attribute.get("label").asText()) && value.equals(attribute.get("value").asText())){
						return true;
					}
				}
				return false;
			}

			@Override
			public void describeTo(final Description description){
				description.appendText("No element attribute with label "+label+" and value "+value+" found.");
			}
		};
	}

	/**
	 * Hamcrest Matcher for a JsonNode. It returns true, if the JsonNode has a direct property with
	 * the value "property" and the value "value" as provided to this method.
	 */
	protected Matcher<JsonNode> hasJsonProperty(final String property, final String value){
		return new BaseMatcher<JsonNode>(){
			@Override
			public boolean matches(final Object item){
				JsonNode node = (JsonNode)item;
				JsonNode propertyNode = node.get(property);
				if(propertyNode == null){
					return false;
				}
				return value.equals(propertyNode.asText());
			}

			@Override
			public void describeTo(final Description description){
				description.appendText("JsonNode item should have property "+property+" with value "+value);
			}

		};
	}

	/*
	 * Loads a json file from the test/resources/json folder. 
	 */
    public static JsonNode loadJsonFile(String fileName){
    	ObjectMapper objectMapper = new ObjectMapper();
    	InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("json/"+fileName+".json");
        try {
            return objectMapper.readTree(is);
        } catch (IOException e) {
            logger.error("deserialize json node ({}): {}", e.getMessage(), is);
        } finally {
			IOUtils.closeQuietly(is);
		}
		return null;
    }

}