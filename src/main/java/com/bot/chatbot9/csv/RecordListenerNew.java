package com.bot.chatbot9.csv;

import org.datavec.api.records.writer.RecordWriter;

import java.io.Serializable;

public interface RecordListenerNew extends Serializable {
	
	/**
     * Get if listener invoked.
     */
    boolean invoked();

    /**
     * Change invoke to true.
     */
    void invoke();

    /**
     * Event listener for each record to be read.
     * @param reader the record reader
     * @param record in raw format (Collection, File, String, Writable, etc)
     */
    void recordRead(RecordReaderNew reader, Object record);

    /**
     * Event listener for each record to be written.
     * @param writer the record writer
     * @param record in raw format (Collection, File, String, Writable, etc)
     */
    void recordWrite(RecordWriter writer, Object record);

}
