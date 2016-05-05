/**
 * 
 */
package com.fr.bi.stable.structure.queue;

import com.fr.bi.common.inter.Release;

/**
 * @author Daniel
 *
 */
public class ConcurrentCounterObject<T> implements Release {
	private int count = 0;
	private T t;
	private volatile boolean isClear = false;
	
	public ConcurrentCounterObject(T t){
		this.t = t;
	}
	
	public T get(){
		return t;
	}
	
	public void access_plus(){
		synchronized (this) {
			count++;
		}
	}
	
	public void access_reduce(){
		synchronized (this) {
			count--;
		}
	}
	
	
	
	public boolean isZeroCount(){
		return count == 0;
	}
	

	public boolean isClear(){
		return isClear;
	}

	/* (non-Javadoc)
	 * @see com.fr.bi.common.inter.Release#clear()
	 */
	@Override
	public void releaseResource() {
		// TODO Auto-generated method stub
		isClear = true;
	}
}