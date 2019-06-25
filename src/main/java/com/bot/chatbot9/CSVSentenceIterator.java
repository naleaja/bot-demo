package com.bot.chatbot9;


import org.datavec.api.split.FileSplit;
import org.deeplearning4j.text.sentenceiterator.BaseSentenceIterator;
import org.deeplearning4j.text.sentenceiterator.SentencePreProcessor;

import com.bot.chatbot9.csv.CSVRecordReaderNew;

import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;

public class CSVSentenceIterator extends BaseSentenceIterator {
    private final CSVRecordReaderNew recordReader;

    public CSVSentenceIterator(File inputFile) throws Exception {
        recordReader = new CSVRecordReaderNew(1, ',');
        recordReader.initialize(new FileSplit(inputFile));
    }

    @Override
    public String nextSentence() {
        if (!recordReader.hasNext()) {
            throw new NoSuchElementException();
        }

        String rawSentence = recordReader.next().get(1).toString();

        SentencePreProcessor preProcessor = getPreProcessor();

        if (preProcessor != null) {
            return preProcessor.preProcess(rawSentence);
        }

        return rawSentence;
    }

    @Override
    public boolean hasNext() {
        return recordReader.hasNext();
    }

    @Override
    public void reset() {
        recordReader.reset();
    }
}
