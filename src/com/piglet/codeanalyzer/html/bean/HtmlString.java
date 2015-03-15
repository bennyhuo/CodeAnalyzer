package com.piglet.codeanalyzer.html.bean;

public class HtmlString extends HtmlObject{
	private String content;
	
	public HtmlString(String content){
		this.content = content;
	}
	
	public String getContent(){
		return content;
	}
}
