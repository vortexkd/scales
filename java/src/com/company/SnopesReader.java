package com.company;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SnopesReader extends ArticleWebsiteReader {
    private static final String ROOT = "https://www.snopes.com/";
    private static final String DB = System.getProperty("user.dir")+"/databases/" + "snopes.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");
    private static final String HREF = "href";
    private static final String IMG_PATH = System.getProperty("user.dir") + "/snopes_images/";

    static String getDB() {
        return DB;
    }

    SnopesReader() throws ParseException {
        super(DB,DATE_FORMAT);

    }

    private boolean getArticleData(String url) throws IOException {
        if(!isSnopesArticle(url) || !isUnique(url)) {
            return false;
        }
        Document doc = Jsoup.connect(url).get();
        Element articleTitle = doc.getElementsByClass("article-title").first();

        System.out.println("Title : " + articleTitle.text());
        Element articleDescription = doc.getElementsByClass("article-description").first();
        System.out.println(articleDescription.text());

        Elements metaTags = doc.getElementsByTag("meta");
        String newsKeywordsIdentifier = "news_keywords";
        String keywordsIdentifier = "keywords";
        String verdictIdentifier = "alternateName";
        String dateIdentifier = "datePublished";


        String verdict = "";
        String keywords = "";
        String date = "";

        for (Element metaTag : metaTags) {
            if(dateIdentifier.equals(metaTag.attr("itemprop"))) {
                date = metaTag.attr("content");
                System.out.println(date);
                break;
            }
        }

        for (Element metaTag : metaTags) {
            if(newsKeywordsIdentifier.equals(metaTag.attr("name"))) {
                System.out.println(metaTag.attr("content"));
                keywords = metaTag.attr("content");
                break;
            }
        }
        if (keywords.equals("")) {
            for (Element metaTag : metaTags) {
                if(keywordsIdentifier.equals(metaTag.attr("name"))) {
                    System.out.println(metaTag.attr("content"));
                    keywords = metaTag.attr("content");
                    break;
                }
            }
        }

        Elements spanTags = doc.getElementsByTag("span");
        for (Element spanTag : spanTags) {
            if(verdictIdentifier.equals(spanTag.attr("itemprop"))) {
                System.out.println(spanTag.text());
                verdict = spanTag.text();
            }
        }
        if(verdict.equals("")) {
            return false;
        }
        return addArticleData(date,url,keywords,articleTitle.text(),articleDescription.text(),verdict);
    }

    private boolean addArticleData(String datePublished, String url, String keywords, String articleTitle,
                                   String articleDescription, String verdict) {
        try {
            Date date = getDateFromString(datePublished);
            String[] urlRootKey = getUrlRootKey(url);
            String[] keywordsArray = keywords.split(",");
            String appendedVerdict = articleDescription + " | " + verdict;
            super.addDatabaseArticle(new Article(date, urlRootKey[1], urlRootKey[0], keywordsArray, articleTitle,
                    appendedVerdict));
            return true;
        } catch (ParseException p) {
            System.out.println("Unparseable Date");
            System.out.println(datePublished);
            System.out.println(url);
        }
        return false;
    }

    @Override
    void crawl() throws IOException, InterruptedException {
        int limit = 40;
        String url = "https://www.snopes.com/category/facts/";
        List<String> pageLinks = new ArrayList<>();
        for(int page = 20; page <= limit; page++) {
             pageLinks.addAll(getArticleList(url + "page/" + page + "/"));
            Thread.sleep(1000);
        }
        for (String link : pageLinks) {
            getArticleData(link);
            Thread.sleep(1000);
        }
    }

    private List<String> getArticleList(String url) throws IOException {
        List<String> listOfLinks = new ArrayList<>();

        Document doc = Jsoup.connect(url).get();
        Elements articleElements = doc.getElementsByTag("article");
        for (Element articleElement : articleElements) {
            Element link = articleElement.getElementsByTag("a").first();
            listOfLinks.add(link.attr("href"));
        }

        return listOfLinks;
    }

    private Date getDateFromString(String dateString) throws ParseException {
        SimpleDateFormat dateReader = new SimpleDateFormat("yyyy-MM-dd");
        Date date = dateReader.parse(dateString.substring(0,10));
        return date;
    }

    private String[] getUrlRootKey(String url) {
        String[] result = new String[2];
        Pattern p = Pattern.compile("(https://www\\.snopes\\.com/)([^/]+/$)");
        Matcher m = p.matcher(url);
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

    private boolean isSnopesArticle(String url) {
        Pattern p = Pattern.compile("(https://www\\.snopes\\.com/)([^/]+/$)");
        Matcher m = p.matcher(url);
        if(m.find()) {
            return true;
        }
        return false;
    }


    //イメージを集めるメソッド（不具合あり、80%) - 一応動いてる。
    void getSnopesImages() {
        //get list of verdicts.
        List<String> verdictList = new ArrayList<>();
        for (Article article : super.getDatabaseArticles()) {
            String verdict = article.getSplitDescription()[1];
            if(!verdictList.contains(verdict)) {
                verdictList.add(verdict);
//                System.out.println(verdict);
            }
        }
        for ( String s : verdictList) {
            System.out.println(createImageUrl(s));
            downloadImage(createImageUrl(s),IMG_PATH + "det-" + s.toLowerCase().replaceAll(" ", "") + ".gif");
        }
    }

    private String createImageUrl(String verdict){
        String path = ROOT + "content/themes/snopes/dist/images/det-" + verdict.toLowerCase().replaceAll(" ", "") + ".gif";
        return path;
    }

    private boolean downloadImage(String url, String destinationPath) {
        try {
            URL imgUrl = new URL(url);
            BufferedImage img = ImageIO.read(imgUrl);
            File file = new File(destinationPath);
            ImageIO.write(img, "gif", file);
            return true;
        } catch (Exception e) {
            System.out.println("Download failed : " + url );
            System.out.println(destinationPath);
            return false;
        }
    }

}
