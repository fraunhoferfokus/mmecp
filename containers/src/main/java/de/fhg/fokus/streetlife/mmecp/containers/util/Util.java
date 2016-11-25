package de.fhg.fokus.streetlife.mmecp.containers.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.map.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;


public class Util {

	private static final Logger LOG = LoggerFactory.getLogger(Util.class);

    /*
     * Loads the contents of a resources as text (String)
     */
    public static synchronized String loadResourceAsString(String resourceName) throws IOException {
        if (resourceName == null || resourceName.isEmpty()) {return null;}

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName);
        if (is == null) {
            LOG.warn("Could not open resource: {}", resourceName);
            return null;
        }
        // else
        return org.apache.commons.io.IOUtils.toString(is, "UTF-8");
    }


    private static final ObjectMapper Mapper = new ObjectMapper();
    /*
     * Loads a json node from the test/resources/json folder.
     */
    public static synchronized JsonNode loadJsonFromFile(String fileName) {
        if (fileName == null || fileName.isEmpty()) {return null;}

        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("json/"+fileName+".json");
            return Mapper.readTree(is);
        } catch (IOException e) {
            LOG.error("deserialize json node ({}): {}", e.getMessage(), is);
            return null;
        } finally {
            IOUtils.closeQuietly(is);
        }
    }


    public static synchronized <T> T loadJsonObjectFromFile(String fileName, Class aClass) {
        if (aClass == null || fileName == null || fileName.isEmpty()) {return null;}

        InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream(fileName);
        if(is == null){
            LOG.warn("loadJsonObjectFromFile() - could not find file: {}", fileName);
            return null;
        }
        LOG.info("Found file: {}", fileName);

        try {
            return (T) Mapper.readValue(is, aClass);
        } catch (IOException e) {
            LOG.error("deserialize event ({}): {}", e.getMessage(), is);
        } finally {
            IOUtils.closeQuietly(is);
        }
        return null;
    }


    public static ArrayNode merge(ArrayNode mainNode, ArrayNode updateNode) {
        if (mainNode == null || updateNode == null) {return null;}

        Iterator<JsonNode> elements = updateNode.elements();
        while (elements.hasNext()) {
            JsonNode value = elements.next();
            ((ArrayNode)mainNode).add(value);
        }
        return mainNode;
    }

    public static ArrayNode merge(List<ArrayNode> nodes) {
        if (nodes == null || nodes.size() == 0) {return null;}

        if (nodes.size() == 1) {
            return nodes.get(0);
        }

        ArrayNode node = nodes.get(0).deepCopy();
        Iterator<ArrayNode> elements = nodes.iterator();
        // skip first one
        elements.next();
        while(elements.hasNext()){
            merge(node,elements.next());
        }
        return node;
    }


    public static String shorten(String txt, int maxlen) {
        if (txt == null || maxlen < 0) {return null;}

        if (txt.length() <= maxlen) {
            return txt;
        } else {
            return txt.substring(0, maxlen) + " ...";
        }
    }


    // final static org.codehaus.jackson.map.ObjectMapper MAPPER = new org.codehaus.jackson.map.ObjectMapper();
    // mapper.enableDefaultTyping(); // defaults for defaults (see below); include as wrapper-array, non-concrete types
    // mapper.enableDefaultTyping(ObjectMapper.DefaultTyping.NON_FINAL, JsonTypeInfo.As.WRAPPER_OBJECT); // all non-final types

    private final static ObjectWriter OW_SHORT = new org.codehaus.jackson.map.ObjectMapper().writer();
    private final static ObjectWriter OW_NICE  = new org.codehaus.jackson.map.ObjectMapper().writer().withDefaultPrettyPrinter();

    public static synchronized String toJsonString(Object obj) {
        if (obj == null) {return null;}

        try {
            return OW_SHORT.writeValueAsString(obj);
        } catch (IOException e) {
            LoggerFactory.getLogger(Util.class).warn("Could not serialize object to JSON: {}", obj.toString());
            return null;
        }
    }

    private static org.codehaus.jackson.map.ObjectMapper NiceMapper = new org.codehaus.jackson.map.ObjectMapper();
    public static synchronized String prettify(String input) throws IOException {
        if (input == null) {return null;}
        if (input.isEmpty()) {return "{}";}

        return OW_NICE.writeValueAsString(NiceMapper.readTree(input));
    }


    public static Set<String> findDuplicates(List<org.codehaus.jackson.JsonNode> nodes) {
        if (nodes == null) {return null;}

        List<String> list = new LinkedList<>();
        for (org.codehaus.jackson.JsonNode node : nodes) {
            list.add(node.asText());
        }
        return findDuplicates(list);
    }

    public static <T> Set<T> findDuplicates(Collection<T> list) {
        if (list == null) {return null;}

        Set<T> duplicates = new LinkedHashSet<T>();
        Set<T> uniques = new HashSet<T>();

        for(T t : list) {
            if(!uniques.add(t)) {
                duplicates.add(t);
            }
        }
        return duplicates;
    }


    public static String readFromGzipFile(String fileName) {
        File file = new File(fileName);
        if (!file.exists() || !file.canRead()) {
            return null;
        }

        FileInputStream input = null;
        try {
            input = new FileInputStream(fileName);
            GZIPInputStream unzipped = new GZIPInputStream(input);
            InputStreamReader is = new InputStreamReader(unzipped, "UTF-8");
            BufferedReader reader = new BufferedReader(is);

            StringBuffer sbuf = new StringBuffer();
            String content;
            while ((content = reader.readLine()) != null) {
                sbuf.append(content);
            }
            return sbuf.toString();
        } catch (IOException e) {
            // do nothing
            return null;
        } finally {
            if (input != null) try {
                input.close();
            } catch (IOException e) {
                // do nothing
                return null;
            }
        }
    }


    private static void makeDirsAsNeeded(String filePath) {
        new File(filePath).getParentFile().mkdirs();
    }


    public static void writeToFile(String message, String fileName) {
        makeDirsAsNeeded(fileName);

        if (fileName == null || fileName.isEmpty()) return;
        if (message == null || message.isEmpty()) return;

        try {
            PrintWriter pw = new PrintWriter(fileName);
            pw.print(message);
            pw.close();
        } catch (FileNotFoundException e) {
            // do nothing in this version of the method
        }
    }


    public static void writeToGzipFile(String message, String fileName) {
        makeDirsAsNeeded(fileName);

        FileOutputStream output = null;
        try {
            output = new FileOutputStream(fileName);
            Writer writer = new OutputStreamWriter(new GZIPOutputStream(output), "UTF-8");
            try {
                writer.write(message);
            } finally {
                writer.close();
            }
        } catch (IOException e) {
            // do nothing
        } finally {
            if (output != null) try {
                output.close();
            } catch (IOException e) {
                // do nothing
            }
        }
    }


    public static void writeToFile(String response) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter("your_filename.txt"));
            writer.write(response);

        } catch (IOException e) {
        }
        finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
            }
        }
    }


}