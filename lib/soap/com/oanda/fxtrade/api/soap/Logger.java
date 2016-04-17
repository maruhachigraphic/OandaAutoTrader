/*
 * Copyright © 2010 OANDA Corporation. All Rights Reserved.
 */
package com.oanda.fxtrade.api.soap;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.util.Date;

public class Logger {

	public class FilePrintStream extends PrintStream {

		public FilePrintStream(File file) throws FileNotFoundException {
			super(file);
		}

	}

	public class FileLogStream implements LogStream {

		public FileLogStream(String fileName) {

		}

		public void print(String text) {

		}
	}

	public interface LogStream {
		public void print(String text);
	}

	public class SystemLogStream implements LogStream {

		public void print(String text) {
			System.out.println(text);
		}

	}

	public static boolean isEnabled = false;
	private LogStream logStream = new SystemLogStream();

	private static Logger instance = null;

	public static Logger getInstance() {
		if(instance == null) {
			instance = new Logger();
			instance.setOutputStream(instance.new SystemLogStream());
		}
		return instance;
	}

	public void log(String text) {
		if(isEnabled) {
			Date dt = new Date();
			DateFormat df = new java.text.SimpleDateFormat("yyyy.MM.dd HH:mm:ss.SSS");
			String dateText = df.format(dt);
			String threadId = Thread.currentThread().getName();

			logStream.print("[" + dateText +	"] [" + threadId + "] " + text);
		}
	}

	public void setOutputStream(LogStream logStream) {
		this.logStream = logStream;
	}

	public void setLogfile(String logfile) {
		File file = new File(logfile);
		try {
			System.setOut(
					new FilePrintStream(file));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		//this.logStream = new FileLogStream(logfile);
	}
}
