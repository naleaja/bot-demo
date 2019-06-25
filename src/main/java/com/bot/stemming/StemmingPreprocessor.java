package com.bot.stemming;

import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;

public class StemmingPreprocessor extends CommonPreprocessor {
	
	@Override
    public  String preProcess(String token) {
        String prep = super.preProcess(token);
        IndonesianStemmer indoStemmer = new IndonesianStemmer();
        
        String testString = prep;
		char[] stringToCharArray = testString.toCharArray();
		int len = indoStemmer.stem(stringToCharArray, stringToCharArray.length, true);
		String stem = new String(stringToCharArray, 0, len);

        return stem;
    }

}
