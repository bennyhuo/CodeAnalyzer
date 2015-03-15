package com.piglet.codeanalyzer.bean;

import java.util.HashMap;
import java.util.TreeSet;

public class CodeReport {
	private String filePath;
	private HashMap<String, Result> total;
	private HashMap<String, TreeSet<Result>> resultMap = new HashMap<String, TreeSet<Result>>();

	public String getFilePath() {
		return filePath;
	}

	public CodeReport filePath(String filePath) {
		this.filePath = filePath;
		return this;
	}

	public HashMap<String, Result> getTotal() {
		return total;
	}

	public CodeReport total(HashMap<String, Result> total) {
		this.total = total;
		return this;
	}

	public HashMap<String, TreeSet<Result>> getResultMap() {
		return resultMap;
	}

	public CodeReport resultMap(HashMap<String, TreeSet<Result>> resultMap) {
		this.resultMap = resultMap;
		return this;
	}

}
