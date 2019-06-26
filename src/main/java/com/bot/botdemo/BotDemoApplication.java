package com.bot.botdemo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import org.apache.commons.io.IOUtils;
import org.datavec.api.split.FileSplit;
import org.datavec.api.util.ClassPathResource;
import org.datavec.api.writable.Writable;
import org.deeplearning4j.bagofwords.vectorizer.BagOfWordsVectorizer;
import org.deeplearning4j.models.embeddings.loader.WordVectorSerializer;
import org.deeplearning4j.models.word2vec.VocabWord;
import org.deeplearning4j.models.word2vec.wordstore.VocabCache;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.deeplearning4j.util.ModelSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

import com.bot.chatbot9.QuestionClassifier;
import com.bot.chatbot9.TextVectorizer;
import com.bot.chatbot9.csv.CSVRecordReaderNew;
import com.bot.stemming.StemmingPreprocessor;
import com.bot.stopwords.StopwordsIND;
import com.linecorp.bot.client.LineMessagingClient;
import com.linecorp.bot.model.ReplyMessage;
import com.linecorp.bot.model.event.MessageEvent;
import com.linecorp.bot.model.event.message.TextMessageContent;
import com.linecorp.bot.model.message.TextMessage;
import com.linecorp.bot.spring.boot.annotation.EventMapping;
import com.linecorp.bot.spring.boot.annotation.LineMessageHandler;

@SpringBootApplication
@LineMessageHandler
public class BotDemoApplication extends SpringBootServletInitializer {

	@Autowired
	private LineMessagingClient lineMessagingClient;

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		return builder.sources(BotDemoApplication.class);
	}
	public static void main(String[] args) {

		SpringApplication.run(BotDemoApplication.class, args);
	}

	@EventMapping
	public void handleTextEvent(MessageEvent<TextMessageContent> messageEvent){
		String pesan = messageEvent.getMessage().getText().toLowerCase();
		//String[] pesanSplit = pesan.split(" ");
		//if(pesanSplit[0].equals("apakah")){
			//String jawaban = getRandomJawaban();
		try {
			String jawaban = getAnswers(pesan);
			String replyToken = messageEvent.getReplyToken();
			balasChatDenganRandomJawaban(replyToken, jawaban);
		//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*private String getRandomJawaban(){
		String jawaban = "";
		int random = new Random().nextInt();
		if(random%2==0){
			jawaban = "Ya";
		} else{
			jawaban = "Nggak";
		}
		return jawaban;
	}*/
	
	private String getAnswers(String question) throws Exception {
		
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
		tokenizerFactory.setTokenPreProcessor(new StemmingPreprocessor());
	    
		BagOfWordsVectorizer vectorizer = new BagOfWordsVectorizer.Builder()
	            .setTokenizerFactory(tokenizerFactory)
	            .setStopWords(StopwordsIND.getStopWords())
	         //   .setVocab(loadVocabulary(new File("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\vectorizer.bin")))
	            .setVocab(loadVocabulary(getFile("vectorizer",".bin")))
	            .build();
	    
	    TextVectorizer textvectorizer = new TextVectorizer(vectorizer);
	    
	    Map<Integer, String> answers = new HashMap<>();
		try (CSVRecordReaderNew reader = new CSVRecordReaderNew(1, ',')) {
	        //reader.initialize(new FileSplit(new ClassPathResource("/data/answers.csv").getFile()));
			reader.initialize(new FileSplit(getFile("/data/answers",".csv")));
	        while(reader.hasNext()) {
	            List<Writable> record = reader.next();
	
	            // Note: The answer index needs a -1, so that we get an offset mapping.
	            // this is required by the neural network. But instead of fixing it there, we're fixing it here.
	            answers.put(record.get(0).toInt() - 1, record.get(1).toString());
	        }
	    }
		File classifierFile = getFile("classifier",".bin");
		//MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(new File ("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\classifier.bin"));
		MultiLayerNetwork network = ModelSerializer.restoreMultiLayerNetwork(classifierFile);

		
		QuestionClassifier classifier = new QuestionClassifier(network, textvectorizer, answers);
		
		return classifier.predict(question);
		
	}

	private void balasChatDenganRandomJawaban(String replyToken, String jawaban){
		TextMessage jawabanDalamBentukTextMessage = new TextMessage(jawaban);
		try {
			lineMessagingClient
					.replyMessage(new ReplyMessage(replyToken, jawabanDalamBentukTextMessage))
					.get();
		} catch (InterruptedException | ExecutionException e) {
			System.out.println("Ada error saat ingin membalas chat");
		}
	}
	
	private static VocabCache<VocabWord> loadVocabulary(File inputFile) throws IOException {
	    return WordVectorSerializer.readVocabCache(inputFile);
	}
	
	private File getFile(String prefix, String suffix) {
		ClassLoader classLoader = getClass().getClassLoader();
		InputStream resourceAsStream = classLoader.getResourceAsStream(prefix+suffix);
		if(resourceAsStream != null){
			File file = null;
			try {
				file =  stream2file(resourceAsStream,prefix,suffix);
			} catch (IOException e) {
				e.printStackTrace();
			}
			return file;
		} else {
			return null;
		}
		
	}
	
	public static File stream2file(InputStream in,String prefix, String suffix) throws IOException {
		final File tempFile = File.createTempFile(prefix, suffix);
		tempFile.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempFile)) {
		    IOUtils.copy(in, out);
		}
		return tempFile;
	}
}
