package de.fhg.fokus.streetlife.mmecp.containers.util;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by csc on 01.12.2015.
 */
public  class SimpleCache <T> {

    private Map<String, T> theCache = new HashMap<>();

    public void put(String key, T elem) {
        theCache.put(key, elem);
    }

    public T get(String key) {
        return theCache.get(key);
    }

    public void purge() {
        theCache = new HashMap<>();
    }

    public int size() {
        return theCache.size();
    }

}
