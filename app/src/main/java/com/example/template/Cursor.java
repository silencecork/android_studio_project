
package com.example.template;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Generated("org.jsonschema2pojo")
public class Cursor {

    @SerializedName("resultCount")
    @Expose
    public String resultCount;
    @SerializedName("pages")
    @Expose
    public List<Page> pages = new ArrayList<Page>();
    @SerializedName("estimatedResultCount")
    @Expose
    public String estimatedResultCount;
    @SerializedName("currentPageIndex")
    @Expose
    public Integer currentPageIndex;
    @SerializedName("moreResultsUrl")
    @Expose
    public String moreResultsUrl;
    @SerializedName("searchResultTime")
    @Expose
    public String searchResultTime;

}
