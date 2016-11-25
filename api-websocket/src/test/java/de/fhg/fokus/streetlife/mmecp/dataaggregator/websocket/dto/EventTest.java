package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.dto;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import org.testng.annotations.*;

import static org.hamcrest.MatcherAssert.assertThat;

public class EventTest{

	protected Event event;

	@BeforeMethod
	public void init(){
		event = new Event();
	}

}