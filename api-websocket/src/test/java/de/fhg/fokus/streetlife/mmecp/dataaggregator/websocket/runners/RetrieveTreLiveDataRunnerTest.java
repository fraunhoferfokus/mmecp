package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.runners;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.DataAggregatorClientImpl;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine.TreLiveParkingStationEngine;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.ToPanelEndpoint;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage.SessionManagerException;
import mockit.Expectations;
import mockit.Mocked;
import mockit.Verifications;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasItems;

public class RetrieveTreLiveDataRunnerTest {
	
	protected RetrieveTreLiveDataRunner runner;

	@Mocked ToPanelEndpoint endpointCallback;

	@BeforeMethod
	public void init() {
		runner = new RetrieveTreLiveDataRunner(null, new Event());

		// runner.setEndpointCallback(endpointCallback);
	}

	@Test
	public void shouldCallCipApi(
		@Mocked DataAggregatorClientImpl dac){

		runner.run();

		new Verifications(){{
			dac.getScalarValuesCIP(withArgThat(hasItems("time", "trend", "status", "code", "name1", "x", "y")));
		}};
	}

	@Test
	public void shouldCallTreLiveDataEngineWithCIPResult(
		@Mocked DataAggregatorClientImpl dac,
		@Mocked TreLiveParkingStationEngine tlpse) throws IOException, SessionManagerException{

		new Expectations(){{

			Map<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("time", "2015-07-01T18:33:59+03:00");
			resultMap.put("trend", "stable");
			resultMap.put("name1", "P-Plevna");
			List<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
			resultList.add(resultMap);

			dac.getScalarValuesCIP((List<String>)any); result = resultList;

			tlpse.parseResponse(anyString); result = "response";
		}};

		runner.run();

		new Verifications(){{
			dac.getScalarValuesCIP(withArgThat(hasItems("time", "trend", "status", "code", "name1", "x", "y")));
			tlpse.parseResponse(withArgThat(containsString("\"name1\":\"P-Plevna\"")));
			//endpointCallback.callback(anyString, anyString);
		}};

	}

	@Test
	public void twoThreadsShouldCallToPanelEndpointCallbackTwice(
		@Mocked DataAggregatorClientImpl dac,
		@Mocked TreLiveParkingStationEngine tlpse) throws Exception{
		
		new Expectations(){{

			Map<String, String> resultMap = new HashMap<String, String>();
			resultMap.put("time", "2015-07-01T18:33:59+03:00");
			resultMap.put("trend", "stable");
			resultMap.put("name1", "P-Plevna");
			List<Map<String, String>> resultList = new ArrayList<Map<String,String>>();
			resultList.add(resultMap);

			dac.getScalarValuesCIP((List<String>)any); result = resultList;

			tlpse.parseResponse(anyString); result = "response";
		}};

		ScheduledExecutorService executor = Executors.newScheduledThreadPool(2);

		int threads = 2;

		CountDownLatch latch = new CountDownLatch(threads);

		for(int i=0;i<threads;i++){
			executor.scheduleAtFixedRate(new Runnable(){
				public void run(){
					runner = new RetrieveTreLiveDataRunner(null, new Event());

					// runner.setEndpointCallback(endpointCallback);

					runner.run();

					latch.countDown();
				}
			}, 0L, 1L, TimeUnit.SECONDS);
		}

		latch.await();

		new Verifications(){{
			dac.getScalarValuesCIP(withArgThat(hasItems("time", "trend", "status", "code", "name1", "x", "y"))); times = 2;
			tlpse.parseResponse(withArgThat(containsString("\"name1\":\"P-Plevna\""))); times = 2;
			//endpointCallback.callback(anyString, anyString); times = 2;
		}};
	}

}