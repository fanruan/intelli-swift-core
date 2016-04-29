package com.fr.bi.cal.analyze.cal.sssecret;

public class PageObject {

	
	private Object key;
	
	private int index = -1;
	
	public static final PageObject NULLOBJECT = new PageObject();
	
	public PageObject(){
	}
	
	public PageObject(Object key, int index){
		this(index);
		this.key = key;
	}
	
	
	public PageObject(int index){
		this();
		this.index = index;
	}
	
	
	public int getIndex(){
		return index;
	}
	
	public Object getObject(){
		return key;
	}
	
	public void setObject(Object key){
		this.key = key;
	}
}