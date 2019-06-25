package com.bot.chatbot9;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;

import com.bot.chatbot9.csv.CSVRecordReaderNew;
import com.bot.stemming.StemmingPreprocessor;
import com.bot.stopwords.StopwordsIND;


public class ChatBotTesting {
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
	    //tokenizerFactory.setTokenPreProcessor(new CommonPreprocessor());
		tokenizerFactory.setTokenPreProcessor(new StemmingPreprocessor());
	    
	    
	    BagOfWordsVectorizer vectorizer = new BagOfWordsVectorizer.Builder()
	            .setTokenizerFactory(tokenizerFactory)
	            .setStopWords(StopwordsIND.getStopWords())
	            .setVocab(loadVocabulary(new File("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\vectorizer.bin")))
	            .build();
	    
	    TextVectorizer textvectorizer = new TextVectorizer(vectorizer);
	    
	    Map<Integer, String> answers = new HashMap<>();
		try (CSVRecordReaderNew reader = new CSVRecordReaderNew(1, ',')) {
	        reader.initialize(new FileSplit(new ClassPathResource("/data/answers.csv").getFile()));
	
	        while(reader.hasNext()) {
	            List<Writable> record = reader.next();
	
	            // Note: The answer index needs a -1, so that we get an offset mapping.
	            // this is required by the neural network. But instead of fixing it there, we're fixing it here.
	            answers.put(record.get(0).toInt() - 1, record.get(1).toString());
	        }
	    }
	//	System.out.println("answers : " +answers);
		//File classifierFile = new ClassPathResource("classifier.bin").getFile();
		MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(new File ("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\classifier.bin"));

		
		QuestionClassifier classifier = new QuestionClassifier(network, textvectorizer, answers);
		
		System.out.println(classifier.predict("yuhuuu"));
	}
	
	private static VocabCache<VocabWord> loadVocabulary(File inputFile) throws IOException {
	    return WordVectorSerializer.readVocabCache(inputFile);
	}

}


