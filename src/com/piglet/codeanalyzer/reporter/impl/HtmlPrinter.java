package com.piglet.codeanalyzer.reporter.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import jxl.Workbook;
import jxl.format.Alignment;
import jxl.format.Colour;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.RowsExceededException;

import com.piglet.codeanalyzer.bean.CodeReport;
import com.piglet.codeanalyzer.bean.Result;
import com.piglet.codeanalyzer.reporter.ReportPrinter;

public class HtmlPrinter implements ReportPrinter {
	private static final String ENDIX = ".html";
	
	class CellSizeAdapter {
		private static final int MIN_WIDTH = 5;
		private static final int MIN_HEIGHT = 1;
		
		private ArrayList<Integer> colWidths = new ArrayList<Integer>();
		private ArrayList<Integer> rowHeights = new ArrayList<Integer>();
		private WritableSheet sheet;

		public CellSizeAdapter(WritableSheet sheet) {
			this.sheet = sheet;
		}

		public void invalidate() {
			for (int i = 0; i < colWidths.size(); i++) {
				sheet.setColumnView(i, colWidths.get(i));
			}
			for (int i = 0; i < rowHeights.size(); i++) {
				try {
					sheet.setRowView(i, rowHeights.get(i));
				} catch (RowsExceededException e) {
					e.printStackTrace();
				}
			}
		}

		public void adaptWidth(int col, int width) {
			putToList(colWidths, col, width, MIN_WIDTH);
		}

		public void adaptHeight(int row, int height) {
			putToList(rowHeights, row, height,MIN_HEIGHT);
		}

		private void putToList(List<Integer> list, int index, int value, int defaultValue) {
			float valueF = value * 1.1f;
			value = (int) (valueF + 1);
			if (index >= list.size()) {
				for (int i = 0; i < index - list.size(); i++) {
					list.add(defaultValue);
				}
				list.add(value);
			} else {
				if (list.get(index) < value) {
					list.set(index, value);
				}
			}
		}
	}

	public void print(CodeReport report) {

		String filePath = report.getFilePath();
		HashMap<String, Result> total = report.getTotal();
		HashMap<String, TreeSet<Result>> resultMap = report.getResultMap();
		try {
			File xlsFile = new File(filePath + ENDIX);
			int i = 1;
			while (xlsFile.exists()) {
				xlsFile = new File(filePath + i + ENDIX);
				i++;
			}
			WritableWorkbook book = Workbook.createWorkbook(xlsFile);
			for (Map.Entry<String, Result> entry : total.entrySet()) {
				String type = entry.getKey();
				Result totalResult = entry.getValue();
				TreeSet<Result> detailResultSet = resultMap.get(type);

				WritableSheet sheet = book.createSheet(type, 0);
				CellSizeAdapter adapter = new CellSizeAdapter(sheet);

				WritableFont font = new WritableFont(WritableFont.TIMES);
				font.setBoldStyle(WritableFont.BOLD);
				font.setPointSize(11);
				WritableCellFormat headFormat = new WritableCellFormat(font);
				headFormat.setBackground(Colour.GRAY_25);
				headFormat.setAlignment(Alignment.LEFT);
				for (i = 0; i < Result.COL_NUMS; i++) {
					String content = Result.getHeader(i);
					adapter.adaptWidth(i, getSupportedStringLen(content));
					Label label = new Label(i, 0, content);
					label.setCellFormat(headFormat);
					sheet.addCell(label);
				}

				WritableCellFormat lineFormat = new WritableCellFormat();
				lineFormat.setAlignment(Alignment.LEFT);
				Iterator<Result> it = detailResultSet.iterator();
				int j = 1;
				while (it.hasNext()) {
					Result result = it.next();
					for (i = 0; i < Result.COL_NUMS; i++) {
						String content = result.get(i);
						adapter.adaptWidth(i, getSupportedStringLen(content));
						Label label = new Label(i, j, content);
						label.setCellFormat(lineFormat);
						sheet.addCell(label);
					}
					j++;
				}
				for (i = 0; i < Result.COL_NUMS; i++) {
					String content = totalResult.get(i);
					adapter.adaptWidth(i, getSupportedStringLen(content));
					Label label = new Label(i, j, content);
					label.setCellFormat(lineFormat);
					sheet.addCell(label);
				}
				adapter.invalidate();
			}

			book.write();
			book.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private int getSupportedStringLen(String content) {
		int len = 0;
		char lo = '\u0391';
		char hi = '\uFFE5';

		for (int i = 0; i < content.length(); i++) {
			char c = content.charAt(i);
			if (c <= hi && c >= lo) {
				len += 2;
			} else {
				len++;
			}
		}
		return len;
	}

	public static void main(String[] args) {
		String str = "abc张三";
		System.out.println(str.length());
		System.out.println(str.getBytes().length);
		System.out.println(str.toCharArray().length);
	}
}
