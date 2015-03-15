package com.piglet.codeanalyzer.reporter.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.TabStop.Alignment;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import com.piglet.codeanalyzer.bean.CodeReport;
import com.piglet.codeanalyzer.bean.Result;
import com.piglet.codeanalyzer.reporter.ReportPrinter;

public class PdfPrinter implements ReportPrinter {
	class CellSizeAdapter {
		private static final int MIN_WIDTH = 5;
		private static final int MIN_HEIGHT = 1;

		private ArrayList<Float> colWidths = new ArrayList<Float>();
		private ArrayList<Float> rowHeights = new ArrayList<Float>();
		private PdfPTable table;

		public CellSizeAdapter(PdfPTable table) {
			this.table = table;
		}

		public void invalidate() {
			try {
				if (colWidths.size() > 0) {
					float[] widths = new float[colWidths.size()];
					for (int i = 0; i < colWidths.size(); i++) {
						widths[i] = colWidths.get(i);
					}
					table.setWidths(widths);
				}
				if (rowHeights.size() > 0) {
					float[] heights = new float[rowHeights.size()];
					for (int i = 0; i < rowHeights.size(); i++) {
						heights[i] = rowHeights.get(i);
					}
					// table.set(heights);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		public void adaptWidth(int col, float width) {
			putToList(colWidths, col, width, MIN_WIDTH);
		}

		public void adaptHeight(int row, float height) {
			putToList(rowHeights, row, height, MIN_HEIGHT);
		}

		private void putToList(List<Float> list, int index, float value, float defaultValue) {
			value *= 1.1f;
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

	@Override
	public void print(CodeReport report) {
		String filePath = report.getFilePath();
		HashMap<String, Result> total = report.getTotal();
		HashMap<String, TreeSet<Result>> resultMap = report.getResultMap();
		try {
			File pdfFile = new File(filePath + ".pdf");
			int i = 1;

			// iText 处理中文
			BaseFont baseFont = BaseFont.createFont("STSongStd-Light", "UniGB-UCS2-H", true);
			// 1.创建 Document 对象
			Rectangle rect = new Rectangle(842,595);
			Document document = new Document(rect);
			FileOutputStream fos = new FileOutputStream(pdfFile);
			PdfWriter.getInstance(document, fos);
			// 3.打开文档
			document.open();
			// 4.创建需要填入文档的元素
			for (Map.Entry<String, Result> entry : total.entrySet()) {
				String type = entry.getKey();
				Result totalResult = entry.getValue();
				TreeSet<Result> detailResultSet = resultMap.get(type);

				document.add(new Paragraph(type, new Font(baseFont)));
				PdfPTable table = new PdfPTable(Result.COL_NUMS);
				CellSizeAdapter adapter = new CellSizeAdapter(table);
				for (i = 0; i < Result.COL_NUMS; i++) {
					String content = Result.getHeader(i);
					adapter.adaptWidth(i, getSupportedStringLen(content));
					PdfPCell cell = new PdfPCell();
//					Paragraph p = new Paragraph(content, new Font(baseFont));
//					cell.addElement(p);
					cell.addElement(new Chunk(content,new Font(baseFont)));
					cell.setHorizontalAlignment(Element.ALIGN_CENTER );
					cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
					cell.setBackgroundColor(BaseColor.GRAY);
					table.addCell(cell);
				}

				Iterator<Result> it = detailResultSet.iterator();
				while (it.hasNext()) {
					Result result = it.next();
					for (i = 0; i < Result.COL_NUMS; i++) {
						String content = result.get(i);
						 adapter.adaptWidth(i, getSupportedStringLen(content));
						PdfPCell cell = new PdfPCell();
						cell.addElement(new Paragraph(content));
						table.addCell(cell);
					}
				}
				for (i = 0; i < Result.COL_NUMS; i++) {
					String content = totalResult.get(i);
					 adapter.adaptWidth(i, getSupportedStringLen(content));
					PdfPCell cell = new PdfPCell();
					cell.addElement(new Paragraph(content));
					table.addCell(cell);
				}
				 adapter.invalidate();
				document.add(table);
			}
			// 6.关闭文档
			document.close();

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
}
