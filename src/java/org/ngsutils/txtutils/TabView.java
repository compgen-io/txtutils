package org.ngsutils.txtutils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import org.ngsutils.cmdlinej.annotation.Command;
import org.ngsutils.cmdlinej.annotation.Option;
import org.ngsutils.cmdlinej.annotation.UnnamedArg;
import org.ngsutils.cmdlinej.impl.AbstractCommand;

@Command(name="view", desc="View tab-delimited files", category="tab")
public class TabView extends AbstractCommand {
	private PrintStream out = System.out;
	private String filename;
	private int lineCount;
	private int min;
	private int max;
	private String delim;

	@UnnamedArg(name="FILE", defaultValue="-")
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Option(charName="d", desc="Column delimiter", defaultValue="|")
	public void setDelimiter(String delim) {
		this.delim = delim;
	}
	
	@Option(charName="l", desc="Lines to read to estimate column sizes", defaultValue="1000")
	public void setLineCount(int lines) {
		this.lineCount = lines;
	}
	
	@Option(name="min", desc="Minumum column size", defaultValue="-1")
	public void setMin(int min) {
		this.min = min;
	}
	
	@Option(name="max", desc="Max column size", defaultValue="-1")
	public void setMax(int max) {
		this.max = max;
	}
	
	@Override
	public void exec() throws Exception {
		InputStream is = System.in;
		if (!filename.equals("-")) {
			is = new FileInputStream(filename);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		String line = reader.readLine();
		
		while (line.startsWith("#")) {
			line = reader.readLine();
		}
		
		int[] colmax = new int[line.split("\t").length];
		
		for (int i=0; i< colmax.length; i++) {
			colmax[i] = min;
		}
		
		List<String[]> bufferedCols = new ArrayList<String[]>();
		
		while (line != null && bufferedCols.size() < lineCount) {
			String[] cols = line.split("\t");
			bufferedCols.add(cols);
			
			for (int i=0; i<cols.length; i++) {
				if (cols[i].length() > colmax[i]) {
					if (max > -1) {
						colmax[i] = Math.min(max, cols[i].length());
					} else {
						colmax[i] = cols[i].length();
					}
				}
			}
			line = reader.readLine();
		}
		
		for (String[] cols: bufferedCols) {
			writeCols(cols, colmax);
		}

		while (line != null) {
			String[] cols = line.split("\t");
			writeCols(cols, colmax);
			line = reader.readLine();
		}
		
		if (!is.equals(System.in)) {
			reader.close();
		}
	}

	private void writeCols(String[] cols, int[] colmax) {
		for (int i=0; i<cols.length; i++) {
			if (i > 0) {
				out.print(" "+delim+" ");
			}
			String s = cols[i];
			while (s.length() < colmax[i]) {
				s += " ";
			}
			out.print(s);
		}
		out.println();
	}
}
