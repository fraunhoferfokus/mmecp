package de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.engine;

import de.fhg.fokus.streetlife.mmecp.containers.dto.Event;
import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.generic.impl.ShapeFileReader;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzBikeAccData;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.MapObjectSet;
import de.fhg.fokus.streetlife.mmecp.dataaggregator.model.PanelObject.VmzDtvwData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class ShapeFileReaderTest {
	
	protected static final Logger LOG = LoggerFactory.getLogger(ShapeFileReaderTest.class);

	@Test(enabled = true)
	public void testShapeFileReader() throws Exception {

		File file = ShapeFileReader.getDefaultBerShapeFile();
		MapObjectSet dataset = ShapeFileReader.testShapeFileReading(file);
		String json = Util.prettify(dataset.toJsonString());
		assertThat(json, containsString("\"objectID\" : \"Detail_Links_V03_DTVw_polyline.9001\""));
		LOG.info("SHP success: found objectID Detail_Links_V03_DTVw_polyline.9001 in shape file!");

		List values = dataset.getValueList(VmzDtvwData.TRAFFICRATE);
		LOG.info(values.toString() + "...\nvalues : " + new Integer(values.size()).toString());
		assertThat(values.toString(), containsString("7484"));
		assertThat(values.toString(), containsString("17778"));
		assertThat(values.toString(), containsString("40350"));
	}


	@Test(enabled = true)
	public void testDatabaseFileRead() throws Exception {

		MapObjectSet dataset = VmzBikeAccData.readAccidentDataFromDbfResource("bikeaccdata" + File.separator + "UHS_11_13_P_Kn", new Event());

		//System.out.println(list.toString());
		String json = Util.prettify(dataset.toJsonString());
		assertThat(json, containsString("\"objectType\" : \"accidents\""));
		assertThat(json, containsString("\"objectID\" : \"45580017\""));
		System.out.println(Util.shorten(json, 1111));

		List values = dataset.getValueList(VmzBikeAccData.ACCIDENTS);
		LOG.info(Util.shorten(values.toString(), 111) + "...\nvalues : " + new Integer(values.size()).toString());
		assertThat(values.toString(), containsString("17"));
		assertThat(values.toString(), containsString("22"));
		assertThat(values.toString(), containsString("53"));
		// assertThat(values, everyItem(greaterThanOrEqualTo(new Long(0))));
		// assertThat(values, containsInAnyOrder(1,2,3));
	}


	@BeforeMethod
	public void init(){
	}


}