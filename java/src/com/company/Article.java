package com.company;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class Article implements Comparable<Article>{


    private static final String HREF = "href";
    private static final String HTTP = "http";
    private static final String DOT = ".";
    private static final String FORWARD_SLASH =  "/";
    private static final String P_TAG = "<p>";
    private static final String P_TAG_CLOSED = "</p>";
    private static final String LINE_SEP = System.getProperty("line.separator");
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

    private final Date postDate;
    private final String urlKey;
    private final String urlRoot;
    private final List<String> keywords;
    private final String headline;
    private final String description;
//    private final String imgLocation; #bonus.


    Article(Date postDate, String urlKey, String urlRoot, String[] keywords, String headline, String description) {
        this.postDate = postDate;
        this.urlKey = urlKey;
        this.urlRoot = urlRoot;
        this.keywords = Arrays.asList(keywords);
        this.headline = headline;
        this.description = description;
    }

    Date getPostDate() {
        return this.postDate;
    }

    String getHeadline() {
        return this.headline;
    }

    String getDescription() {
        return this.description;
    }

    String[] getSplitDescription() {
        return this.description.split(" \\| ");
    }

    String getUrl() {
        return this.urlRoot + this.urlKey;
    }

    List<String> getKeywords() {
        return this.keywords;
    }
    String getKeywordsAsString() {
        StringBuilder string = new StringBuilder(this.keywords.get(0));
        for (String word : this.keywords.subList(1,this.keywords.size())) {
            string.append("," + word);
        }
        return string.toString().toLowerCase();
    }

    @Override
    public String toString() {
        return "\"" +  dateFormat.format(this.postDate)
                + "\",\"" + this.urlKey
                + "\",\"" + this.urlRoot
                + "\",\"" + getKeywordsAsString()
                + "\",\"" + this.headline
                + "\",\"" + this.description + "\"";
    }


    @Override
    public int compareTo(Article other) {
        return this.postDate.compareTo(other.getPostDate());
    }
}
