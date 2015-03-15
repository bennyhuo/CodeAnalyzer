package com.piglet.codeanalyzer.parser;

public enum FileType {
	UNKNOWN("unknown"),CPP("cpp"), HEADER("h"), HEADER1("hpp"),C("c"), JAVA("java"), CSHARP("cs"), PHP("php"), XML("xml"), JS(
			"js");

	private String endix;

	private FileType(String endix) {
		this.endix = endix;
	}
	
	public String getEndix(){
		return endix;
	}
	
	public static FileType getFileType(String filename){
		if(filename != null && !filename.equals("")){
			String endix = filename.substring(filename.lastIndexOf('.')+1);
			for(FileType type : FileType.values()){
				if(type.getEndix().equals(endix)){
					return type;
				}
			}
		}
		return UNKNOWN;
	}
}
