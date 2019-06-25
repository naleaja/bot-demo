package com.bot.stopwords;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class StopwordsIND {
	
	private static List<String> stopWords;

    @SuppressWarnings("unchecked")
    public static List<String> getStopWords() {

        try {
            if (stopWords == null)
                stopWords = IOUtils.readLines(StopwordsIND.class.getResourceAsStream("/stopwords"));
            	//stopWords = IOUtils.readLines(new InputStreamReader(new FileInputStream("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\stopwords")));
			
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return stopWords;
    }

}
