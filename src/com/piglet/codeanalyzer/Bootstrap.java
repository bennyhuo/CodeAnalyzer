package com.piglet.codeanalyzer;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

import com.piglet.codeanalyzer.bean.CodeReport;
import com.piglet.codeanalyzer.bean.Result;
import com.piglet.codeanalyzer.parser.FileType;
import com.piglet.codeanalyzer.parser.NoteSpeci;
import com.piglet.codeanalyzer.reporter.ReportPrinter;
import com.piglet.codeanalyzer.reporter.impl.PdfPrinter;

public class Bootstrap {

	public static BlockingQueue<Result> messageQueue;
	public static ExecutorService worker;
	public static CountDownLatch countDown;
	public static int fileCount = 0;
	// public static SortedSet<Result> resutSet = new TreeSet<>();
	public static String CURRENT_DIR;
	public static int curdirLen;
	public static File dir;

	public static void main(String[] args) throws IOException {
		// if(args.length == 0){
		// printUsage();
		// System.exit(0);
		// }

		messageQueue = new LinkedBlockingQueue<>();
		worker = Executors.newCachedThreadPool();

		dir = new File(".").getCanonicalFile();
		CURRENT_DIR = dir.getAbsolutePath();
		curdirLen = CURRENT_DIR.length() + 1;
		listFiles(dir);

		CodeReport report = genReport();
//		ReportPrinter printer = new ExcelPrinter();
//		printer.print(report);
		ReportPrinter printer = new PdfPrinter();
		printer.print(report);
	}

	public static void printUsage() {
		System.out.println("Usage: codeanalyzer [-cmd] [filepath]");
		System.out.println("cmd:");
		System.out.println("\t -R recursively");
	}

	public static void listFiles(File dir) {
		// System.out.println(dir.getName());
		if (dir.isDirectory()) {
			File[] files = dir.listFiles();
			for (File f : files) {
				listFiles(f);
			}
		} else {
			// analyze
			fileCount++;
			worker.submit(new AnalyzeTask(dir));
		}
	}

	public static CodeReport genReport() {
		HashMap<String, Result> total = new HashMap<String, Result>();
		HashMap<String, TreeSet<Result>> resultMap = new HashMap<String, TreeSet<Result>>();
		while (fileCount-- > 0) {
			Result r;
			try {
				r = messageQueue.take();
				if (r.type != FileType.UNKNOWN) {
					TreeSet<Result> set = resultMap.get(r.type.name());
					if (set == null) {
						set = new TreeSet<Result>();
						set.add(r);
						resultMap.put(r.type.name(), set);
					} else {
						set.add(r);
					}

					Result result = total.get(r.type.name());
					if (result == null) {
						result = new Result();
						result.filepath = "Total";
						total.put(r.type.name(), result);
					}
					result.add(r);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		worker.shutdown();
		return new CodeReport().filePath(CURRENT_DIR + File.separator + dir.getName()).resultMap(resultMap).total(total);
	}

	static class AnalyzeTask implements Runnable {
		private File file;

		public AnalyzeTask(File file) {
			this.file = file;
		}

		@Override
		public void run() {
			Result r = new Result();
			r.filepath = file.getAbsolutePath();
			r.filepath = r.filepath.substring(curdirLen);
			r.type = FileType.getFileType(file.getName());
			if (r.type == FileType.UNKNOWN) {
				// ....
				r.lines = 0;

			} else {
				r.lines = 0;
				BufferedReader reader = null;
				try {
					reader = new BufferedReader(new FileReader(file));
					String content = null;
					boolean isNote = false;
					boolean isRef = false;
					while ((content = reader.readLine()) != null) {
						if (content.matches("\\s*")) {
							r.wsLines++;
						} else if (isNote) {
							if (NoteSpeci.isMultiEnd(content)) {
								isNote = false;
							}
							r.noteLines++;

						} else if (isRef) {
							if (NoteSpeci.isRefEnd(content)) {
								isRef = false;
							}
							r.refLines++;
						} else {
							if (NoteSpeci.isSingleLine(content)) {
								r.noteLines++;
							} else if (NoteSpeci.isMultiBegin(content)) {
								r.noteLines++;
								isNote = true;
							} else if (NoteSpeci.isRefBegin(content)) {
								r.refLines++;
								isRef = true;
							} else {
								r.lines++;
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} finally {
					try {
						reader.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}

			}
			messageQueue.offer(r);
		}
	}
}
