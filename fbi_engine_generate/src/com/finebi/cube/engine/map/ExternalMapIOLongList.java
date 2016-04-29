package com.finebi.cube.engine.map;


import com.fr.bi.stable.io.newio.read.LongNIOReader;
import com.fr.bi.stable.io.newio.write.LongNIOWriter;
import com.fr.bi.stable.structure.collection.list.BIList;
import com.fr.bi.stable.structure.collection.list.LongList;

import java.io.FileNotFoundException;

/**
 * Created by Connery on 2015/12/2.
 */
public abstract class ExternalMapIOLongList<K> extends ExternalMapIOBIList<K, Long> {

    public ExternalMapIOLongList(String ID_path) {
        super(ID_path);
    }

    @Override
	protected void initialValueReader() throws FileNotFoundException {
        if (valueFile.exists()) {
            valueReader = new LongNIOReader(valueFile);
        } else {
            throw new FileNotFoundException();
        }

    }

    @Override
	protected void initialValueWriter() {
        valueWriter = new LongNIOWriter(valueFile);
    }

    @Override
	Long recordAmount(BIList<Long> value) {
        return Long.valueOf(value.size());
    }

    @Override
	Boolean compare(int i, Long amount) {
        return i < amount;
    }

    @Override
	BIList generateList() {
        return new LongList();
    }
}