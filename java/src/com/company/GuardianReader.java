package com.company;


import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

class GuardianReader extends ArticleWebsiteReader {
    private static final List<String> CATEGORIES = new ArrayList<>();
    private static final List<String> MONTHS = new ArrayList<>(
            Arrays.asList("jan", "feb", "mar", "apr", "may", "jun", "jul", "aug", "sep" , "oct" , "nov", "dec"));
    private static final String ROOT = "https://www.theguardian.com/";
    private static final String DB = System.getProperty("user.dir")+"/databases/" + "guardian.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final String HREF = "href";


    private final List<Article> articlesToOutput = new ArrayList<>(); // read from csv.


    static String getDB() {
        return DB;
    }

    private static boolean isGuardianArticle(String url) {
        Pattern p = Pattern.compile("https?://.+theguardian\\.com/[^/]+/(\\d\\d\\d\\d)/(\\w\\w\\w)/(\\d\\d)/(.*-.*)");
        Matcher m = p.matcher(url);
        if(m.find()) {
            System.out.println(m.group());
        }
        return true;
    }

    private static String linkBuilder (String category) {
        return ROOT + category + "/";
    }

    private static void setCategoryList() throws IOException{
        Document doc = Jsoup.connect(ROOT).get();
        Elements onlineCategories = doc.getElementsByClass("top-navigation__action");

        for(Element category : onlineCategories) {
            Pattern p = Pattern.compile(".*/(.*)");
            Matcher m = p.matcher(category.attr("href"));

            if (m.find()) {
                if (!CATEGORIES.contains(m.group(1))) {
                    CATEGORIES.add(m.group(1));
                }
            }
        }
        if(!CATEGORIES.contains("us-news")) {
            CATEGORIES.add("us-news"); //サイトでなぜかこれだけが特殊のクラスをもつ。。。
        }
    }

    GuardianReader() throws IOException, ParseException {
        super(DB,DATE_FORMAT);
        setCategoryList();
        //TODO: take DB path as a variable
    }

    List<Article> getDatabaseArticles() {
        return super.getDatabaseArticles();
    }


    @Override
    void crawl() throws IOException, InterruptedException {
        List<String> linkList = getHomeArticles(ROOT);
        for (String link : linkList) {
            this.addArticleData(link);
            Thread.sleep(1000);
        }
        for(String category : CATEGORIES) {
            linkList = getHomeArticles(GuardianReader.linkBuilder(category));
            for (String link : linkList){
                this.addArticleData(link);
                Thread.sleep(1000);
            }
        }
    }

    private boolean addArticleData(String url) throws IOException, InterruptedException {
        if(!isGuardianArticle(url)) {
            return false;
        }
        if(!isUnique(url)) {
            return false;
        }
        List<String> metaArray = new ArrayList<>(Arrays.asList("article:tag", "og:title", "og:description"));

        String metaAttributeProperty = "property";
        String metaAttributeContent = "content";

        Map<String, String> usefulData = new HashMap<>();
        usefulData.put("article:tag","");
        usefulData.put("og:title","");
        usefulData.put("og:description","");



        Document doc = Jsoup.connect(url).get();
        Elements metaTags = doc.getElementsByTag("meta");
        for (Element metaTag : metaTags) {
            if(metaArray.contains(metaTag.attr(metaAttributeProperty))) {
                usefulData.put(metaTag.attr(metaAttributeProperty),metaTag.attr(metaAttributeContent));
            }
        }
        for (String key : metaArray) {
            System.out.println(usefulData.get(key));
        }

        String [] urlRootKey = getRootKeyFromUrl(url);
        try {
            Date date = getDateFromLink(url);
            //ここの.toLowerCaseが重要でしたT.T
            Article newArticle = new Article(date, urlRootKey[1], urlRootKey[0], usefulData.get("article:tag").toLowerCase().split(","),
                    usefulData.get("og:title"),
                    usefulData.get("og:description"));
            super.addDatabaseArticle(newArticle);
            return true;
        } catch (ParseException e) {
            System.out.println("Unparseable Date : "+url);
            return false;
        }
    }

    private List<String> getHomeArticles (String url) throws IOException {
        //constants for guardian
        String homeArticleLinkIdentifier = "data-link-name";

        Document doc = Jsoup.connect(url).get();
        Elements homeArticleLinks = doc.getElementsByAttributeValueContaining(homeArticleLinkIdentifier,"article");
        List<String> articleLinkList = new ArrayList<>();
        for(Element link : homeArticleLinks) {
            if (!articleLinkList.contains(link.attr(HREF))) {
                articleLinkList.add(link.attr(HREF));
            }
        }
        return articleLinkList;
    }

    private Date getDateFromLink(String messyDate) throws ParseException {
        Pattern p = Pattern.compile("(\\d\\d\\d\\d)/(\\w\\w\\w)/(\\d\\d)");
        Matcher m = p.matcher(messyDate);
        String result = "";
        if(m.find()) {
            System.out.println(m.group(1)+ "/" + m.group(2) +"/" + m.group(3));
            result = m.group(1)+ "/" + (MONTHS.indexOf(m.group(2)) + 1) +"/" + m.group(3);
        }
        Date date;
        date = DATE_FORMAT.parse(result);
        System.out.println(date.toString());
        return date;
    }

    private String[] getRootKeyFromUrl (String url) {
        Pattern p = Pattern.compile("(https?://.+theguardian\\.com/.*/\\d\\d\\d\\d/\\w\\w\\w/\\d\\d/)(.*-.*)");
        Matcher m = p.matcher(url);
        String[] result = new String[2];
        if(m.find()) {
            result[0] = m.group(1);
            result[1] = m.group(2);
        }
        return result;
    }

    private boolean isUnique(String url) {
        for(Article a : super.getDatabaseArticles()) {
            if(a.getUrl().equals(url)) {
                return false;
            }
        }
        return true;
    }
}
