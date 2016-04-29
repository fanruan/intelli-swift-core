package com.finebi.cube.engine.map;




import com.fr.bi.stable.io.newio.read.IntNIOReader;
import com.fr.bi.stable.io.newio.write.IntNIOWriter;
import com.fr.bi.stable.structure.collection.list.BIList;
import com.fr.bi.stable.structure.collection.list.IntList;

import java.io.FileNotFoundException;

/**
 * 对应value的类型是IntList的外排抽象实现
 * Created by FineSoft on 2015/7/15.
 */
public abstract class ExternalMapIOIntList<K> extends ExternalMapIOBIList<K, Integer> {

    public ExternalMapIOIntList(String ID_path) {
        super(ID_path);
    }


    @Override
	protected void initialValueReader() throws FileNotFoundException {
        if (valueFile.exists()) {
            valueReader = new IntNIOReader(valueFile);
        } else {
            throw new FileNotFoundException();
        }

    }

    @Override
	protected void initialValueWriter() {
        valueWriter = new IntNIOWriter(valueFile);
    }

    @Override
	Integer recordAmount(BIList<Integer> value) {
        return value.size();
    }

    @Override
	Boolean compare(int i, Integer amount) {
        return i < amount;
    }

    @Override
	BIList generateList() {
        return new IntList();
    }

}