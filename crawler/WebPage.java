package crawler;

import java.io.Serializable;
import java.util.ArrayList;

public class WebPage implements Serializable
{
    private static final long serialVersionUID = 5645555302029964072L;

    private int id;
    private String title;
    private ArrayList<String> links;

    WebPage(int id, String title, ArrayList<String> links)
    {
        this.id    = id;
        this.title = title;
        this.links = links;
    }

    public String getTitle(){ return this.title; }
}
