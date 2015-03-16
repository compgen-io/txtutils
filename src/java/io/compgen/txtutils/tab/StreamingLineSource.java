package io.compgen.txtutils.tab;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class StreamingLineSource {
	private List<String> lines = new ArrayList<String>();
	private int offset = 0;
	private int maxsize = 10000;
	private int curLine = 0;
	
	private BufferedReader reader;
	
	public StreamingLineSource(InputStream is, int maxsize) {
		this.reader = new BufferedReader(new InputStreamReader(is));
		this.maxsize = maxsize;
	}
	
	public void close() throws IOException {
		this.reader.close();
	}
	
	public void setCurLine(int num) {
		this.curLine = num;
	}
	
	public String getLine(int num) throws IOException {
		while (lines.size() <= num - offset) {
			if (readLine()==null) {
				return null;
			}
		}
		curLine = num+1;
		return lines.get(num - offset);
	}

	public String getNextLine() throws IOException {
		return getLine(curLine++);
	}

	private String readLine() throws IOException {
		String s = reader.readLine();
		if (s != null) {
			lines.add(s);
			if (lines.size() > maxsize) {
				lines.remove(0);
				offset++;
			}
		}
		return s;
	}
}
