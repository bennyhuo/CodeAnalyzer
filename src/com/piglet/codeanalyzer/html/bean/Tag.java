package com.piglet.codeanalyzer.html.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Tag extends HtmlObject{
	public static final String TAG_OPEN = "<#{tagname}#{properties}>";
	public static final String TAG_CLOSE = "</#{tagame}>";
	
	private String tagName;
	private HashMap<String, Object> properties = new HashMap<String,Object>();
	private Tag parent;
	private ArrayList<HtmlObject> children;
	
	public Tag(String tagName){
		this.tagName = tagName;
	}

	public String getTagName() {
		return tagName;
	}

	public void setTagName(String tagName) {
		this.tagName = tagName;
	}

	public HashMap<String, Object> getProperties() {
		HashMap<String,Object> ret = new HashMap<String,Object>();
		ret.putAll(properties);
		return ret;
	}

	public void addProperties(String key, Object value){
		this.properties.put(key, value);
	}
	
	public void removeProperties(String key){
		this.properties.remove(key);
	}

	public Tag getParent() {
		return parent;
	}

	public void setParent(Tag parent) {
		this.parent = parent;
	}

	public ArrayList<HtmlObject> getChildren() {
		ArrayList<HtmlObject> ret = new ArrayList<HtmlObject>();
		Collections.copy(ret, children);
		return ret;
	}
	
	public void addChild(HtmlObject object){
		this.children.add(object);
	}
	
	public void addChild(HtmlObject object, int index){
		this.children.add(index, object);
	}
	
	public int getChildIndex(HtmlObject object){
		return this.children.indexOf(object);
	}
	
	public HtmlObject removeChildAt(int index){
		return this.children.remove(index);
	}
	
	public boolean removeChild(HtmlObject object){
		return this.children.remove(object);
	}
}
