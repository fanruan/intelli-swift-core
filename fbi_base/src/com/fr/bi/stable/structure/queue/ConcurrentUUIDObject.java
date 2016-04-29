/**
 * 
 */
package com.fr.bi.stable.structure.queue;

import com.fr.stable.core.UUID;

/**
 * @author Daniel
 *
 */
public class ConcurrentUUIDObject <T> extends ConcurrentCounterObject<T>{
	
	private String id = UUID.randomUUID().toString();
	
	public ConcurrentUUIDObject(T t){
		super(t);
	}
	
	public String getId(){
		return id;
	}

}