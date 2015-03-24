package io.compgen.txtutils.tab;

import io.compgen.cmdline.annotation.Command;
import io.compgen.cmdline.annotation.Exec;
import io.compgen.cmdline.annotation.Option;
import io.compgen.cmdline.annotation.UnnamedArg;
import io.compgen.cmdline.impl.AbstractCommand;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintStream;

@Command(name="view", desc="View tab-delimited files", category="tab")
public class TabView extends AbstractCommand {
	private PrintStream out = System.out;
	private String filename;
	private int lineCount;
	private int min;
	private int max;
	private int scrollBack = 10000;
	private String delim;

	
	@UnnamedArg(name="FILE", defaultValue="-")
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	@Option(charName="d", desc="Column delimiter", defaultValue="|")
	public void setDelimiter(String delim) {
		this.delim = delim;
	}
	
	@Option(charName="l", desc="Lines to read to estimate column sizes", defaultValue="100")
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
	
	@Option(name="buf", desc="Scroll back buffer size", defaultValue="10000")
	public void setScrollBack(int val) {
		this.scrollBack = val;
	}
	
	@Exec
	public void exec() throws Exception {
		InputStream is = System.in;
		if (!filename.equals("-")) {
			is = new FileInputStream(filename);
		}
		
		StreamingLineSource src = new StreamingLineSource(is, Math.max(scrollBack, lineCount));
		
		int[] colmax = null;
		String[] header= null;

		for (int i=0; i<lineCount; i++) {
			String line = src.getNextLine();
			if (line.startsWith("#")) {
				continue;
			}
			if (colmax == null) {
				colmax = new int[line.split("\t").length];
				for (int j=0; j< colmax.length; j++) {
					colmax[j] = min;
				}
			}
			
			String[] cols = line.split("\t");
			if (header == null) {
				header = cols;
			}
			for (int j=0; j<cols.length; j++) {
				if (cols[j].length() > colmax[j]) {
					if (max > -1) {
						colmax[j] = Math.min(max, cols[j].length());
					} else {
						colmax[j] = cols[j].length();
					}
				}
			}

		}
		
		src.setCurLine(0);
		String line = src.getNextLine();

		while (line != null && !System.out.checkError()) {
			String[] cols = line.split("\t");
			writeCols(cols, colmax);
			line = src.getNextLine();
		}
		
		src.close();
	}
	
	private void writeCols(String[] cols, int[] colmax) {
		for (int i=0; i<cols.length; i++) {
			if (i > 0) {
				out.print(" "+delim+" ");
			}
			String s = cols[i];
			if (s.length() > colmax[i]) {
				s = s.substring(0, colmax[i]-1)+"$";
			} else {
				while (s.length() < colmax[i]) {
					s += " ";
				}
			}
			out.print(s);
		}
		out.println();
	}
}
