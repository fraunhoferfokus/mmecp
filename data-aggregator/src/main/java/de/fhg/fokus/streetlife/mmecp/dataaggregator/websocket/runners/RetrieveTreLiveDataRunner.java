package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.runners;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorClient;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.DataAggregatorFactory;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.BaseDataRunner;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.LiveResponseParseEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.TreLiveParkingStationEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class RetrieveTreLiveDataRunner extends BaseDataRunner {

	private static final ObjectMapper Mapper = new ObjectMapper();


	public RetrieveTreLiveDataRunner(LiveResponseParseEngine parent, Event event) {
		super(parent, event);
	}


	public void run() {

		LOG.info("Calling RetrieveTreLiveDataRunner in order to get tampere live data...");

		DataAggregatorFactory dacFactory = DataAggregatorFactory.newInstance("POJO");

		DataAggregatorClient dac = dacFactory.getClient();

		dac.init();

		String[] attributeIdentifiers = new String[] {"time", "trend", "status", "code", "name1", "x", "y"};

		LOG.info("Calling CIP API...");

		List<Map<String, String>> scalarValues = dac.getScalarValuesCIP(Arrays.asList(attributeIdentifiers));

		LOG.info("Successfully called CIP API...");

		String scalarValuesJsonStr;
		try{
			scalarValuesJsonStr = Mapper.writeValueAsString(scalarValues);
		}catch(IOException e){
			LOG.info("Could not parse getScalarValuesCIP response due to false format.", e);
			return;
		}
		try {
			String response = new TreLiveParkingStationEngine().parseResponse(scalarValuesJsonStr);
			response = MapObjectSet.wrapMapObjectsJsonString(response, event);

			LOG.info("Call toPanelEndpoint callback with response {} ", Util.shorten(response,1111));

			callback(response);
		} catch (IOException | SessionManagerException e) {
			LOG.info("Could not publish result of tre live data runner.",e);
		}
	}


}