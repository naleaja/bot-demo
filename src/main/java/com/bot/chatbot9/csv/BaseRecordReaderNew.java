package com.bot.chatbot9.csv;

import org.datavec.api.writable.Writable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public abstract class BaseRecordReaderNew implements RecordReaderNew {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected List<RecordListenerNew> listeners = new ArrayList<>();

    /** Invokes {@link RecordListenerNew#recordRead(RecordReaderNew, Object)} on all listeners. */
    protected void invokeListeners(Object record) {
        for (RecordListenerNew listener : listeners) {
            listener.recordRead(this, record);
        }
    }

    @Override
    public List<RecordListenerNew> getListeners() {
        return listeners;
    }

    @Override
    public void setListeners(Collection<RecordListenerNew> listeners) {
        this.listeners = (listeners instanceof List ? (List<RecordListenerNew>) listeners : new ArrayList<>(listeners));
    }

    @Override
    public void setListeners(RecordListenerNew... listeners) {
        setListeners(Arrays.asList(listeners));
    }


    @Override
    public boolean batchesSupported() {
        return false;
    }

    @Override
    public List<List<Writable>> next(int num) {
        throw new UnsupportedOperationException();
    }

}
