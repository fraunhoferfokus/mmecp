package de.fhg.fokus.streetlife.mmecp.dataaggregator.model;

import java.io.Serializable;
import java.net.URL;


/**
 * Created by benjamin on 24.08.14.
 * Changed to class by flo on 06.05.15
 */
public class Channel implements Serializable {

    protected String title;

    protected String id;

    protected URL url;

    protected boolean standard;

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public URL getUrl() {
        return this.url;
    }

    public void setUrl(URL url) {
        this.url = url;
    }

    public boolean isStandard() {
        return this.standard;
    }

    public void setStandard(boolean standard) {
        this.standard = standard;
    }
}
