package com.piglet.codeanalyzer.bean;

import com.piglet.codeanalyzer.parser.FileType;

public class Result implements Comparable<Result>{
	public static final int COL_NUMS = 5;
	
	public String filepath;
	public FileType type;
	public int lines;
	public int wsLines;
	public int noteLines;
	public int refLines;

	public void add(Result r) {
		this.lines += r.lines;
		this.wsLines += r.wsLines;
		this.noteLines += r.noteLines;
		this.refLines += r.refLines;
	}
	
	public static String toHeadString(){
		return "Path\tType\tLines\tWhiteSpace\tNotes\tRefs\n";
	}
	
	public String get(int index){
		switch(index){
		case 0:
			return filepath;
		case 1:
			return String.valueOf(lines);
		case 2:
			return String.valueOf(wsLines);
		case 3:
			return String.valueOf(noteLines);
		case 4:
			return String.valueOf(refLines);
			default:
				return "";
		}
	}
	
	public static String getHeader(int index){
		switch(index){
		case 0:
			return "路径";
		case 1:
			return "代码（不含空白）";
		case 2:
			return "空白";
		case 3:
			return "注释";
		case 4:
			return "文档";
			default:
				return "";
		}
	}
	
	public String toLines(){
		return new StringBuilder()
		.append(filepath).append('\t')
		.append(type).append('\t')
		.append(lines).append('\t')
		.append(wsLines).append('\t')
		.append(noteLines).append('\t')
		.append(refLines).append('\n').toString();
	}

	public String toString() {
		return new StringBuilder()
				.append("+----------------------------------------------------------+\n")
				.append("| File:\t\t | ")
				.append(filepath)
				.append("\t\t\t|\n")
				.append("| FileType:\t\t | ")
				.append(type.name())
				.append("\t\t\t|\n")
				.append("| Lines:\t\t\t | ")
				.append(lines)
				.append("\t\t\t|\n")
				.append("| White Space Lines:\t | ")
				.append(wsLines)
				.append("\t\t\t|\n")
				.append("| Note Lines:\t\t | ")
				.append(noteLines)
				.append("\t\t\t|\n")
				.append("| Ref Lines:\t\t | ")
				.append(refLines)
				.append("\t\t\t|\n")
				.append("+----------------------------------------------------------+\n")
				.toString();
	}

	public String toSimpleString() {
		String path = filepath.length()>24? "~"+filepath.substring(filepath.length() - 20): filepath;
		if(path.length() < 8){
			path += "\t\t";
		}else if(path.length() < 16){
			path += "\t";
		}
		return new StringBuilder()
				.append("+----------------------------------------------------------+\n|  ")
				.append(path)
				.append("\t| ")
				.append(type.name())
				.append("\t| ")
				.append(lines)
				.append("\t| ")
				.append(wsLines)
				.append("\t| ")
				.append(noteLines)
				.append("\t| ")
				.append(refLines)
				.append("\t|\n")
				.append("+----------------------------------------------------------+\n")
				.toString();
	}

	@Override
	public int compareTo(Result o) {
		return this.filepath.compareTo(o.filepath);
	}
}
