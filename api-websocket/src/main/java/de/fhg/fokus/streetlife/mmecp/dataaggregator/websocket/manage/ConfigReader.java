    package de.fhg.fokus.streetlife.mmecp.dataaggregator.websocket.manage;

import de.fhg.fokus.streetlife.mmecp.containers.util.Util;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

    /**
 * Created by csc on 27.10.2015.
 */
public class ConfigReader {

    private static final Logger LOG = LoggerFactory.getLogger(ConfigReader.class);

    private static String CachedConfig = null;

    public static synchronized String readConfigJson() throws IOException {
        // check in cache at first
        if (CachedConfig != null) {
            return CachedConfig;
        }
        //not found? then try to load the config - this will only happen on the first call of this method
        try {
            String config = Util.loadResourceAsString("filters.json");

            JsonNode expandedConfig = buildExtendedConfig(new ObjectMapper().readTree(config));

            CachedConfig = Util.toJsonString(expandedConfig);
            // LOG.info("full MMECP config is: {}", Util.prettify(CachedConfig));

            return CachedConfig;
        } catch(IOException e) {
            LOG.error("\n***************************************************************\n"+
                      "*                                                             *\n"+
                      "*    Could not read/parse MMECP config file (filters.json)    *\n"+
                      "*                                                             *\n"+
                      "*    MMECP will NOT be operational !!!                        *\n"+
                      "*                                                             *\n"+
                      "*    The detected error is:                                   *\n"+
                      "*                                                             *\n"+
                      "*    {}\n"+
                      "*                                                             *\n"+
                      "***************************************************************\n",
                    e.toString());
            throw e;
        }
    }


    private static void checkForDuplicates(JsonNode node, String fieldName) throws IOException {
        if (node == null || fieldName == null || fieldName.isEmpty()) {return;}

        List items = node.findValues(fieldName);
        // LOG.info(fieldName + " elements listed in MMECP config are: {}", items.toString());
        if (!Util.findDuplicates(items).isEmpty()) {
            throw new IOException("Duplicate " + fieldName + " entry found: " + Util.findDuplicates(items).toString());
        }
    }


    private static final String USECASEID   = "useCaseID";
    private static final String OPTIONID    = "optionID";
    private static final String ACTIVATED   = "requestActivated";
    private static final String DEACTIVATED = "requestDeactivated";
    private static final String CHART       = "requestChart";
    private static final String SUBTYPE     = "subType";
    private static final String INNER_FIELDS[] = {ACTIVATED,DEACTIVATED,CHART};

        
    private static JsonNode buildExtendedConfig(JsonNode root) throws IOException {
        if (root == null) {return null;}

        JsonNode opts = root.path("options");
        checkForDuplicates(opts, "cityShortForm");
        checkForDuplicates(opts, "city");

        Iterator<JsonNode> optIter = opts.iterator();
        while (optIter.hasNext()) {
            JsonNode opt = optIter.next();
            JsonNode useCases = opt.path("useCases");
            checkForDuplicates(useCases, USECASEID);
            Iterator<JsonNode> useCaseIter = useCases.iterator();
            while (useCaseIter.hasNext()) {
                JsonNode useCase = useCaseIter.next();
                String useCaseId = useCase.path(USECASEID).asText();
                if (useCase.has(CHART)) {
                    ((ObjectNode)(useCase.path(CHART))).put(USECASEID, useCaseId);
                }
                JsonNode options = useCase.path("options");
                checkForDuplicates(options, OPTIONID);
                Iterator<JsonNode> optionsIter = options.iterator();
                while (optionsIter.hasNext()) {
                    JsonNode option = optionsIter.next();
                    ((ObjectNode)option).put(USECASEID, useCaseId);
                    String subType  = option.path(SUBTYPE).asText();
                    String optionId = option.path(OPTIONID).asText();
                    ((ObjectNode)option).put("id", optionId); // legacy-support, TODO: remove after frontend is adapted!
                    for (String field : INNER_FIELDS) {
                        if (option.has(field)) {
                            ObjectNode node = (ObjectNode)(option.path(field));
                            node.put(USECASEID, useCaseId);
                            node.put(OPTIONID, optionId);
                            node.put(SUBTYPE, subType);
                        }
                    }
                    // set each option to "enabled" : true  unless it already has a given value for "enabled"
                    if (!option.has("enabled")) {
                        ((ObjectNode)(option)).put("enabled", true);
                    }
                    // make sure that every option's requestDeactivated section has "live" : "stop" in it
                    if (option.has(DEACTIVATED)) {
                        ((ObjectNode)(option.path(DEACTIVATED))).put("live", "stop");
                    } else {
                        // no requestDeactivated is present
                        // this is considered an error, if the requestActivated section has specified the option as live
                        if (option.has(ACTIVATED) && option.path(ACTIVATED).has("live")) {
                            throw new IOException("Missing requestDeactivated section for use case: " + useCaseId + " / " + optionId);
                        }
                    }
                    // a requestActivated sections not necessarily need "live" : "start" in it, so don't try to fix that
                }
            }
        }
        return root;
    }


}
