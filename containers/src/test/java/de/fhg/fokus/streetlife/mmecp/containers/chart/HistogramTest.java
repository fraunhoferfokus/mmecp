package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.ArrayList;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * Created by csc on 25.09.2015.
 */
public class HistogramTest {

    @Test(enabled = true)
    public void testHistogram() {
        //Histogram hist = new Histogram();
        ArrayList<Long> list = new ArrayList<Long>() {{
            add(1L);
            add(2L);
            add(3L);
            add(4L);
            add(5L);
            add(6L);
            add(7L);
            add(8L);
        }};
        int[] hist = Histogram.computeHistogram(list, 4);

        int max = Integer.MIN_VALUE;
        for( int value : hist ) {
            if( value > max )
                max = value;
        }
        assertThat(max, is(2));
    }


    @Test(enabled = true)
    public String testHistogramToJson1() throws IOException {
        ArrayList<Long> list = new ArrayList<Long>() {{
            add(0L);
            add(10L);
            add(20L);
            add(30L);
            add(40L);
            add(50L);
            add(60L);
            add(70L);
            add(80L);
            add(90L);
            add(100L);
        }};
        ChartData cd = Histogram.makeChartDataWithFixedNumberOfBins("title", list, 10);
        String json = Util.prettify(Util.toJsonString(cd));
        System.out.println(json);

        assertThat(json, containsString(("\"label\" : \"0-10\",")));
        assertThat(json, containsString(("\"label\" : \"91-100\",")));
        assertThat(json, not(containsString("101")));
        return json;
    }


    private void addFromTo(ArrayList<Long> longs, long start, long end, long step) {
        for( Long ll = start; ll <= end; ll += step) { longs.add(ll); }
    }

    @Test(enabled = true)
    public void testHistogramToJson2() throws IOException {
        ArrayList<Long> list = new ArrayList<Long>() {{
                addFromTo(this, 0L, 100L, 10L);
        }};
        ChartData cd = Histogram.makeChartDataWithFixedNumberOfBins("title", list, 10);
        String json = Util.prettify(Util.toJsonString(cd));
        System.out.println(json);

        assertThat(json, containsString(("\"label\" : \"0-10\",")));
        assertThat(json, containsString(("\"label\" : \"91-100\",")));
        assertThat(json, not(containsString("101")));
        assertThat(json, is(testHistogramToJson1()));
    }

    @Test(enabled = true)
    public void testHistogramToJson3() throws IOException {
        ArrayList<Long> list = new ArrayList<Long>() {{
            addFromTo(this, 0L, 6L, 1L);
        }};
        ChartData cd = Histogram.makeChartDataWithFixedNumberOfBins("title", list, 10);
        String json = Util.prettify(Util.toJsonString(cd));
        System.out.println(json);

        assertThat(json, containsString(("\"label\" : \"0-0\",")));
        assertThat(json, containsString(("\"label\" : \"1-1\",")));
        assertThat(json, not(containsString(("\"label\" : \"7-7\","))));
    }


    @Test(enabled = true)
    public void testHistogramToJson4() throws IOException {
        ArrayList<Long> list = new ArrayList<Long>() {{
            addFromTo(this, 0L, 9L, 1L);
        }};
        ChartData cd = Histogram.makeChartDataWithFixedSizeOfBins("title", list, 1);
        String json = Util.prettify(Util.toJsonString(cd));
        System.out.println(json);

        assertThat(json, containsString(("\"label\" : \"0-0\",")));
        assertThat(json, containsString(("\"label\" : \"9-9\",")));
        assertThat(json, not(containsString(("\"label\" : \"10\","))));
    }

}
