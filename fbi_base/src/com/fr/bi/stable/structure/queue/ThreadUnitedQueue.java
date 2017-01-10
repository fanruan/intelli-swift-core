/**
 * 
 */
package com.fr.bi.stable.structure.queue;

import com.finebi.cube.common.log.BILoggerFactory;
import com.fr.bi.common.inter.Delete;
import com.fr.bi.common.inter.Release;

import java.util.Deque;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;

/**
 * @author Daniel
 *
 */
public class ThreadUnitedQueue<T extends Delete> implements Release {
	private volatile Deque<ConcurrentUUIDObject<T>> queue = new LinkedBlockingDeque<ConcurrentUUIDObject<T>>();
	private volatile boolean isClear = false;

	public T get() {
		while(queue.isEmpty()){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
			}
		}
		ConcurrentUUIDObject<T> result = queue.peek();
		result.access_plus();
		return result.get();
	}
	
	public void add(T t){
		if(isClear){
			return;
		}
		ConcurrentUUIDObject<T> u = new ConcurrentUUIDObject<T>(t);
		queue.offerFirst(u);
	}
	
	public void releaseObject(){
		ConcurrentUUIDObject<T> result = queue.peek();
		if(result != null){
			result.access_reduce();
			releaseOffuse();
		}
	}

	public void forceReleaseObject(){
		ConcurrentUUIDObject<T> result = queue.peek();
		if(result != null){
			result.forceSetZero();
			releaseOffuse();
		}
	}

    public boolean isEmpty(){
        return queue.isEmpty();
    }

	/**
	 * 
	 */
	private void releaseOffuse() {
		if(queue.size() > 1){
			new Thread(){
				@Override
				public void run(){
					releaseInThread();
				}
			}.start();
		}
	}
	
	private void releaseInThread(){
		Iterator<ConcurrentUUIDObject<T>> iter = queue.descendingIterator();
		while(iter.hasNext()){
			ConcurrentUUIDObject<T> o = iter.next();
			if(!iter.hasNext()){
				break;
			}
			if(o.isZeroCount()){
				iter.remove();
				synchronized (o) {
					if(!o.isClear()){
						o.clear();
						o.get().clear();
						o.get().delete();
					}
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.common.inter.Release#clear()
	 */
	@Override
	public void clear() {
		isClear = true;
		releaseInThread();
		if(!queue.isEmpty()){
			queue.pop().get().clear();
		}
	}

}