package de.cimt.talendcomp.dev;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;

public class JETFileChecker {
	
	private File componentFolder = null;
	private int countTags = 0; // for test evaluation
	
	public JETFileChecker() {
		// for test cases
	}
	
	public JETFileChecker(String componentFolder) {
		if (componentFolder == null || componentFolder.trim().isEmpty()) {
			throw new IllegalArgumentException("filePath cannot be null or empty");
		} else {
			this.componentFolder = new File(componentFolder);
			if (this.componentFolder.exists() == false) {
				throw new IllegalArgumentException("componentFolder: " + this.componentFolder.getAbsolutePath() + " does not exist");
			} else if (this.componentFolder.isDirectory() == false) {
				throw new IllegalArgumentException("componentFolder: " + this.componentFolder.getAbsolutePath() + " is not a folder (component root folder is expected)");
			}
		}
	}

	public File[] listJetFiles() {
		File[] jetfiles = this.componentFolder.listFiles(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				return (name != null && name.endsWith(".javajet"));
			}
			
		});
		return jetfiles;
	}
	
	public String checkJetFile(File jetfile) throws Exception {
		String content = readContentfromFile(jetfile, null);
		return checkContent(content);
	}
	
	public String checkContent(String content) throws IOException {
		countTags = 0;
		if (content == null) {
			throw new IllegalArgumentException("file content cannot be null");
		}
		String message = null;
		boolean open = false;
		BufferedReader sr = new BufferedReader(new StringReader(content));
		String line = null;
		int lineNumber = 0;
		int lastLineNumberWithOpenTag = 0;
		int lastLineNumberWithCloseTag = 0;
		while ((line = sr.readLine()) != null) {
			lineNumber++;
			int length = line.length();
			for (int i = 0; i < length; i++) {
				char c0 = line.charAt(i);
				char c1 = ' ';
				if (i < length - 1) {
					c1 = line.charAt(i + 1);
				}
				if (c0 == '<' && c1 == '%') {
					countTags++;
					// open found
					if (open) {
						// found an open but already in JET code!
						message = "Line: " + lineNumber + " column: " + (i + 1) + ": found open tag but former tag not closed. Last line-number with open tag: " + lastLineNumberWithOpenTag;
					}
					open = true;
					lastLineNumberWithOpenTag = lineNumber;
				} else if (c0 == '%' && c1 == '>') {
					countTags++;
					if (open == false) {
						// found an close but already outside JET code!
						message = "Line: " + lineNumber + " column: " + (i + 1) + ": found close tag but former tag was not open. Last line number with close tag: " + lastLineNumberWithCloseTag;
					}
					open = false;
					lastLineNumberWithCloseTag = lineNumber;
				}
				if (message != null) {
					break;
				}
			}
			if (message != null) {
				break;
			}
		}
		if (open && message == null) {
			message = "File content has an (last) open tags at line: " + lastLineNumberWithOpenTag + " but no close tag"; 
		}
		// we are at the end of the content and still in open state
		return message;
	}

	public int getCountTags() {
		return countTags;
	}
	
	public static String readContentfromFile(File file, String charset) throws Exception {
		if (file == null) {
			throw new IllegalArgumentException("file cannot be null");
		}
		if (file.exists() == false) {
			throw new Exception("File: " + file.getAbsolutePath() + " does not exist.");
		}
		if (charset == null || charset.trim().isEmpty()) {
			charset = "UTF-8";
		}
		try {
			Path p = java.nio.file.Paths.get(file.getAbsolutePath());
			byte[] bytes = Files.readAllBytes(p);
			if (bytes != null && bytes.length > 0) {
				return new String(bytes, charset);
			} else {
				return null;
			}
		} catch (IOException e) {
			throw new Exception("Read file content from file: " + file.getAbsolutePath() + " failed: " + e.getMessage(), e);
		}
	}

}
