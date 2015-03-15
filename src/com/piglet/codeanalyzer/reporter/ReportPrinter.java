package com.piglet.codeanalyzer.reporter;

import com.piglet.codeanalyzer.bean.CodeReport;

public interface ReportPrinter {

	public abstract void print(CodeReport report);

}