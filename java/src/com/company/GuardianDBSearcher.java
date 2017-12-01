package com.company;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;

class GuardianDBSearcher extends DBsearcher {
    private static final String DB = System.getProperty("user.dir") + "/databases/" + "guardian.csv";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd");

    GuardianDBSearcher() throws ParseException {
        super(DB, DATE_FORMAT);
    }

    @Override
    Article searchArticles(String[] keywords) {
        Article result = super.getDatabaseArticles().get(0);
        double score = 0;
        for (Article article : super.getDatabaseArticles()) {
            double nextScore = super.getMatchScore(article,keywords);
            if (nextScore > score) {
                result = article;
                score = nextScore;
            }
        }
        return result;
    }

    @Override
    String getHTMLDiv(Article article) {
        String path = System.getProperty("user.dir") + "/files/div_text.txt";
        String idPlaceholder = "$i";
        String titlePlaceholder = "<!-- $title -->";
        String bodyPlaceHolder = "<!-- $articleBody -->";
        String urlPlaceHolder = "$url";

        File file = new File(path);
        StringBuilder output = new StringBuilder();
        try {
            FileInputStream fis = new FileInputStream(file);
            InputStreamReader isr = new InputStreamReader(fis);
            BufferedReader text = new BufferedReader(isr);
            String line;
            while ((line = text.readLine()) != null) {
                output.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        String articleDiv = output.toString();

        articleDiv = articleDiv.replace(idPlaceholder, String.valueOf(super.getDatabaseArticles().indexOf(article)));
        articleDiv = articleDiv.replace(titlePlaceholder,article.getHeadline().replaceAll("['\"]","&#39;"));
        articleDiv = articleDiv.replace(bodyPlaceHolder,article.getDescription().replaceAll("['\"]","&#39;"));
        articleDiv = articleDiv.replace(urlPlaceHolder,article.getUrl());



        return articleDiv;
    }

}
