package com.bot.stopwords;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.commons.io.IOUtils;

public class TestStopwordsIND {

	public static void main(String[] args) {
		// TODO Auto-generated method stubs
		
		try {
			List<String> stopWords = IOUtils.readLines(new InputStreamReader(new FileInputStream("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\stopwords")));
			
			//System.out.println(stopWords.size());
			
			for (String output : stopWords) {
				System.out.println(output);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
