package de.fhg.fokus.streetlife.mmecp.containers.chart;

import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;

/**
 * Created by csc on 25.09.2015.
 */
public class MyJsonObjectTest {

    private class MyTestClass extends MyJsonObject {
        private String value;

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    @Test(enabled = true)
    public void testMyJsonObjectToJson() {

        MyTestClass mtc = new MyTestClass();
        mtc.setValue("testValue");
        String json = mtc.toString();
        System.out.println(json);

        assertThat(json, startsWith("{"));
        assertThat(json, containsString("\"value\" : \"testValue\""));
    }

}
