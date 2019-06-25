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
import org.deeplearning4j.nn.conf.GradientNormalization;
import org.deeplearning4j.nn.conf.MultiLayerConfiguration;
import org.deeplearning4j.nn.conf.NeuralNetConfiguration;
import org.deeplearning4j.nn.conf.layers.OutputLayer;
import org.deeplearning4j.nn.conf.layers.variational.VariationalAutoencoder;
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork;
import org.deeplearning4j.optimize.listeners.ScoreIterationListener;
import org.deeplearning4j.text.tokenization.tokenizer.preprocessor.CommonPreprocessor;
import org.deeplearning4j.text.tokenization.tokenizerfactory.DefaultTokenizerFactory;
import org.deeplearning4j.text.tokenization.tokenizerfactory.TokenizerFactory;
import org.nd4j.linalg.activations.Activation;
import org.nd4j.linalg.learning.config.RmsProp;
import org.nd4j.linalg.lossfunctions.LossFunctions;

import com.bot.chatbot9.csv.CSVRecordReaderNew;
import com.bot.stemming.StemmingPreprocessor;
import com.bot.stopwords.StopwordsIND;



public class ChatBotAti {
	
	public static void main(String[] args) throws IOException, InterruptedException {

		Map<Integer, String> answers = new HashMap<>();
		try (CSVRecordReaderNew reader = new CSVRecordReaderNew(1, ',')) {
            reader.initialize(new FileSplit(new ClassPathResource("/data/answers.csv").getFile()));

            while(reader.hasNext()) {
                List<Writable> record = reader.next();

                // Note: The answer index needs a -1, so that we get an offset mapping.
                // this is required by the neural network. But instead of fixing it there, we're fixing it here.
                answers.put(record.get(0).toInt()-1, record.get(1).toString());
            }
        }
		
		System.out.println(answers);
		
		TokenizerFactory tokenizerFactory = new DefaultTokenizerFactory();
        tokenizerFactory.setTokenPreProcessor(new StemmingPreprocessor());
		
        File inputFile = new ClassPathResource("/data/").getFile();
       // File vectorizeFile = new ClassPathResource("vectorizer.bin").getFile();
        
        /*for (String output : StopwordsIND.getStopWords()) {
			System.out.println(output);
		}*/
        
        try {
        	BagOfWordsVectorizer vectorizer = new BagOfWordsVectorizer.Builder()
	                .setTokenizerFactory(tokenizerFactory)
	                .setStopWords(StopwordsIND.getStopWords())
	                .setIterator(new CSVSentenceIterator(inputFile))
	                .build();
        	
			TextVectorizer textvectorizer = new TextVectorizer(vectorizer);
			textvectorizer.fit();
			textvectorizer.save(new File("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\vectorizer.bin"));
			
			System.out.println(textvectorizer.vocabularySize());
			
			MultiLayerConfiguration networkConfiguration = new NeuralNetConfiguration.Builder()
	                .seed(1337)
	                .updater(new RmsProp(0.01))
	                .list()
	                .layer(0, new VariationalAutoencoder.Builder()
	                        .nIn(textvectorizer.vocabularySize()).nOut(1024)
	                        .encoderLayerSizes(1024, 512, 256, 128)
	                        .decoderLayerSizes(128, 256, 512, 1024)
	                        .lossFunction(Activation.RELU, LossFunctions.LossFunction.MSE)
	                        .gradientNormalization(GradientNormalization.ClipElementWiseAbsoluteValue)
	                        .dropOut(0.8)
	                        .build())
	                .layer(1, new OutputLayer.Builder()
	                        .nIn(1024).nOut(answers.size())
	                        .activation(Activation.SOFTMAX)
	                        .lossFunction(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD)
	                        .build())
	                .pretrain(true)
	                .backprop(true)
	                .build();

	        MultiLayerNetwork network = new MultiLayerNetwork(networkConfiguration);
	        network.setListeners(new ScoreIterationListener(1));
	        network.init();
	        
	        QuestionClassifier classifier = new QuestionClassifier(network, textvectorizer, answers);
	        File trainFile = new ClassPathResource("/data/questions_train.csv").getFile();
	       // File testFile = new ClassPathResource("/data/questions_test.csv").getFile();
	        
	       // File classifierFile = new ClassPathResource("classifier.bin").getFile();
	        classifier.fit(trainFile);
	        classifier.save(new File ("C:\\app\\chatbot\\source\\Workspace\\chatbot9\\src\\main\\resources\\classifier.bin"));
	        System.out.println(classifier.score(trainFile));
	        System.out.println(classifier.predict("yuhuuuu"));
	        
	        
        } catch(Exception e) {
        	e.printStackTrace();
        }
		
	}
}
