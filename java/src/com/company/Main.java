package com.company;


import java.io.*;
import java.text.ParseException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
	// write your code here
        //System.out.println("Java says");
        //Thread.sleep(1000);
        if(args.length > 0) {
            String[] keywords;
            if(args[0].contains(" ")) {
                keywords = args[0].split(" ");
            } else {
                keywords = new String[] {args[0]};
            }
            if (keywords.length > 3) {
                DBsearcher gs = new SnopesDBsearcher();
                Article result = gs.searchArticles(keywords);
                System.out.println(gs.getHTMLDiv(result));
            } else {
                DBsearcher gs = new GuardianDBSearcher();
                Article result = gs.searchArticles(keywords);
                System.out.println(gs.getHTMLDiv(result));
            }
            return;
        }
        //Use guardian crawl
//        GuardianReader guardian = new GuardianReader();
//        guardian.crawl();
//        writeToCsv(GuardianReader.getDB(),guardian.getDatabaseArticles());

        //use snopes crawl
//        SnopesReader snopes = new SnopesReader();
//        snopes.crawl();
//        writeToCsv(SnopesReader.getDB(),snopes.getDatabaseArticles());

    }

    private static void writeToCsv(String db, List<Article> articleList) throws IOException{
        String header = "投稿日,urlキー,urlルート,キーワード,タイトル,サマリー";
        String utf8 = "UTF-8";
        String lineSeparator = "line.separator";

        System.out.println("Writing to csv");

        PrintWriter pw = new PrintWriter(db,utf8);
        pw.write(header + System.getProperty(lineSeparator));
        for (Article article : articleList) {
            pw.println(article.toString());
        }
        pw.close();

        System.out.println("Writing complete.");

    }
}
