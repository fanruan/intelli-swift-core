package com.fr.swift.cloud.result;

import com.fr.swift.cloud.source.Row;
import com.fr.swift.cloud.util.IoUtil;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author Lyon
 * @date 2018/6/19
 */
public class SwiftRowIteratorImpl<T extends Pagination<List<Row>>> implements SwiftRowIterator {

    private T source;
    private Iterator<Row> iterator = new ArrayList<Row>().iterator();

    public SwiftRowIteratorImpl(T source) {
        this.source = source;
    }

    @Override
    public boolean hasNext() {
        if (iterator.hasNext()) {
            return true;
        } else if (source != null && source.hasNextPage()) {
            List<Row> page = source.getPage();
            if (page != null && !page.isEmpty()) {
                iterator = page.iterator();
            }
        }
        return iterator.hasNext();
    }

    @Override
    public Row next() {
        return iterator.next();
    }

    @Override
    public void remove() {
    }

    @Override
    public void close() {
        IoUtil.close(source);
    }
}
