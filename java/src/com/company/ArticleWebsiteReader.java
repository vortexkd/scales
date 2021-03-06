package com.company;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

abstract class ArticleWebsiteReader {
    private final List<Article> databaseArticles = new ArrayList<>();


    ArticleWebsiteReader (String dbPath, SimpleDateFormat dateFormat) throws ParseException {
        try {
            CsvReader csvReader = new CsvReader(dbPath,true);
            List<String[]> csvData = csvReader.getData();
            String[] articleMaker = new String[6];
            int count = 0;
            for (String[] row : csvData) {
                for (String entry : row) {
                    articleMaker[count] = entry;
                    count++;
                }
                count = 0;
                Date date = dateFormat.parse(articleMaker[0]);
                String urlKey = articleMaker[1];
                String urlRoot = articleMaker[2];
                String[] keywords = articleMaker[3].split(",");
                String title = articleMaker[4];
                String description = articleMaker[5];

                this.databaseArticles.add(new Article(date, urlKey, urlRoot, keywords, title, description));
            }
        } catch (IOException e) {
            System.out.println("Database not found, using empty set.");
            System.out.println(dbPath);
        }
    }

    List<Article> getDatabaseArticles() {
        return this.databaseArticles;
    }

    void addDatabaseArticle(Article article) {
        this.databaseArticles.add(article); //validation ?
    }

    abstract void crawl() throws IOException, InterruptedException;

}
