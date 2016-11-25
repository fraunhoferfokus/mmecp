package de.fhg.fokus.streetlife.mmecp.containers.chart;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.testng.annotations.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by csc on 25.09.2015.
 */
public class MarginTest {

    @Test(enabled = true)
    public void testMarginToJson() throws IOException {

        Margin ma = new Margin(1,2,3,4);
        String json = Util.prettify(ma.toString());
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString(("\"top\" : 1")));
        assertThat(json, containsString(("\"right\" : 2")));
        assertThat(json, containsString(("\"bottom\" : 3")));
        assertThat(json, containsString(("\"left\" : 4")));
    }

}
