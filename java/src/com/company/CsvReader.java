package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CsvReader {
    private String path;
    private List<String[]> data = new ArrayList<>();
    public CsvReader(String path, boolean skipHeaders) throws IOException {
        this.path = path;
        File file = new File(this.path);
        FileInputStream fis = new FileInputStream(file);
        InputStreamReader isr = new InputStreamReader(fis);
        BufferedReader text = new BufferedReader(isr);
        if(skipHeaders) {
            text.readLine();
        }
        String line;
        while((line=text.readLine())!=null) {
            this.data.add(line.substring(1,line.length()-1).split("\",\""));
        }
        fis.close();
        text.close();
        isr.close();
    }
    public List<String[]> getData(){
        return this.data;
    }
}
