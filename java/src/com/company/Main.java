package com.company;


import java.io.*;
import java.text.ParseException;
import java.util.List;

public class Main {

    public static void main(String[] args) throws InterruptedException, IOException, ParseException {
	// write your code here
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
        }

        //new CrawlLauncher();// will crawl databases.

    }
}
