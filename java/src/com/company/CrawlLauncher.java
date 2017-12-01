package com.company;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.ParseException;
import java.util.List;

public class CrawlLauncher {

    CrawlLauncher() throws ParseException, IOException, InterruptedException {
        GuardianReader guardian = new GuardianReader();
        guardian.crawl();
        writeToCsv(GuardianReader.getDB(),guardian.getDatabaseArticles());

        SnopesReader snopes = new SnopesReader();
        snopes.crawl();
        writeToCsv(SnopesReader.getDB(),snopes.getDatabaseArticles());
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
