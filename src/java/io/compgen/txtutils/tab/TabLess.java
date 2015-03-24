package io.compgen.txtutils.tab;

import io.compgen.cmdline.annotation.Command;
import io.compgen.cmdline.annotation.Exec;
import io.compgen.cmdline.annotation.Option;
import io.compgen.cmdline.annotation.UnnamedArg;
import io.compgen.cmdline.impl.AbstractCommand;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;

import com.googlecode.lanterna.TerminalFacade;
import com.googlecode.lanterna.input.Key;
import com.googlecode.lanterna.input.Key.Kind;
import com.googlecode.lanterna.terminal.Terminal;

@Command(name="less", desc="Scrolling view of tab-delimited files", category="tab")
public class TabLess extends AbstractCommand {
	private String filename;
	private int lineCount;
	private int min;
	private int max;
	private String delim;
	private boolean hasHeader;
	
	
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
	
	@Option(name="header", desc="Show a fixed header")
	public void setHeader(boolean val) {
		this.hasHeader=val;
	}
	
	@Exec
	public void exec() throws Exception {
		String istty = System.getProperty("org.ngsutils.support.tty.fd1");
		if (istty != null && istty.equals("F")) {
			TabView view = new TabView();
			view.setDelimiter(delim);
			view.setFilename(filename);
			view.setLineCount(lineCount);
			view.setMax(max);
			view.setMin(min);
			view.setVerbose(verbose);
			view.exec();
			return;
		}
		
		InputStream is = System.in;
		if (!filename.equals("-")) {
			is = new FileInputStream(filename);
		}
		
		StreamingLineSource src = new StreamingLineSource(is, lineCount);
		
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
			if (header == null && hasHeader) {
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
		Terminal terminal = TerminalFacade.createTerminal(Charset.forName("UTF8"));
	    terminal.setCursorVisible(false);
	    terminal.enterPrivateMode();
//			int termcols = terminal.getTerminalSize().getColumns();

	    
	    int termrows = terminal.getTerminalSize().getRows();
	    int oldrows = termrows;
	    int currow = 0;
	    drawTUI(terminal, colmax, header, src, currow);

	    while (true) {
	    	try {
	    		termrows = terminal.getTerminalSize().getRows();
				if (termrows != oldrows) {
				    drawTUI(terminal, colmax, header, src, currow);
				    continue;
				}
	    	} catch (IOException e) {
	    		
	    	}

	    	Key key = terminal.readInput();
	    	if (key == null) {
	    		continue;
	    	}
	    	
	    	if (key.getKind().equals(Kind.NormalKey) && key.getCharacter()=='q') {
	    		break;
	    	} else if (key.getKind() == Kind.NormalKey && key.getCharacter()==' ') {
	    		currow+=termrows;
			    drawTUI(terminal, colmax, header, src, currow);
	    	} else if (key.getKind() == Kind.NormalKey && key.getCharacter()=='b') {
	    		currow-=termrows;
	    		if (currow < 0) {
	    			currow = 0;
	    		}
			    drawTUI(terminal, colmax, header, src, currow);
	    	} else if (key.getKind() == Kind.ArrowDown) {
	    		currow++;
			    drawTUI(terminal, colmax, header, src, currow);
	    	} else if (key.getKind() == Kind.ArrowUp) {
	    		currow--;
	    		if (currow < 0) {
	    			currow = 0;
	    		}
			    drawTUI(terminal, colmax, header, src, currow);
	    	}
	    }
	    terminal.exitPrivateMode();
	}

	private void drawTUI(Terminal terminal, int[] colmax, String[] header, StreamingLineSource src, int curLine) throws IOException {
		int termcols = terminal.getTerminalSize().getColumns();
		int termrows = terminal.getTerminalSize().getRows();

		int currow = 0;
		int startcol = 0;

		terminal.moveCursor(0,0);
		if (header != null) {
			terminal.applySGR(Terminal.SGR.ENTER_BOLD);
			writeTerminalCols(terminal, header, colmax, startcol, termcols);
			terminal.applySGR(Terminal.SGR.EXIT_BOLD);
			currow = 1;
		}

		while (currow <= termrows) {
			terminal.moveCursor(0, currow);
			String[] cols = src.getLine(curLine + currow).split("\t");
			writeTerminalCols(terminal, cols, colmax, startcol, termcols);
			currow ++;
		}
		
		terminal.moveCursor(0, 0);
		terminal.flush();
	}
	
	private void writeTerminalCols(Terminal terminal, String[] cols, int[] colmax, int startcol, int termcols) {
		String out = "";
		for (int i=startcol; i<cols.length; i++) {
			if (i > startcol) {
				out+=" "+delim+" ";
			}
			String s = cols[i];
			if (s.length() > colmax[i]) {
				s = s.substring(0, colmax[i]-1)+"$";
			} else {
				while (s.length() < colmax[i]) {
					s += " ";
				}
			}
			out += s;
		}
		for (int i=0; i<out.length() && i < termcols; i++) {
			terminal.putCharacter(out.charAt(i));
		}
	}
}
