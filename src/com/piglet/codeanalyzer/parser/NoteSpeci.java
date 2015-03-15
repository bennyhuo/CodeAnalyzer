package com.piglet.codeanalyzer.parser;

import java.util.Scanner;

public class NoteSpeci {
	public static final String SINGLE_LINE = "^\\s*//.*";
	public static final String MULTI_BEGIN = "^\\s*/\\*(^\\*.*)?";
	public static final String MULTI_END= ".*\\*/\\s*$";
	public static final String MULTI_INLINE = "^\\s*/\\*.*\\*/\\s*$";
	public static final String REF_BEGIN = "^\\s*/\\*\\*.*";
	public static final String REF_END = ".*\\*/\\s*$";
	

	public static boolean isSingleLine(String line){
		return line.matches(SINGLE_LINE) || line.matches(MULTI_INLINE);
	}
	
	public static boolean isMultiBegin(String line){
		return line.matches(MULTI_BEGIN);
	}
	
	public static boolean isMultiEnd(String line){
		return line.matches(MULTI_END);
	}
	
	public static boolean isRefBegin(String line){
		return line.matches(REF_BEGIN);
	}
	
	public static boolean isRefEnd(String line){
		return line.matches(REF_END);
	}
	
	public static void main(String[] args){
		Scanner sc = new Scanner(System.in);
		int i = 0;
		
		while(sc.hasNext()){
			String line = sc.nextLine();
			if(line.equals("exit"))break;
			System.out.println(test(line, i));
			i = ++i %5;
		}
		sc.close();
	}
	
	public static boolean test(String input, int index){
		String matcher = "";
		switch(index){
		case 0:
			matcher = SINGLE_LINE;
			break;
		case 1:
			matcher = MULTI_BEGIN;
			break;
		case 2:
			matcher = MULTI_END;
			break;
		case 3:
			matcher = MULTI_INLINE;
			break;
		case 4:
			matcher = REF_BEGIN;
			break;
		}
		return input.matches(matcher);
	}
}
