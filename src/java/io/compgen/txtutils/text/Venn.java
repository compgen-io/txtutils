package io.compgen.txtutils.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import org.ngsutils.cmdlinej.annotation.Command;
import org.ngsutils.cmdlinej.annotation.Option;
import org.ngsutils.cmdlinej.annotation.UnnamedArg;
import org.ngsutils.cmdlinej.exceptions.CommandArgumentException;
import org.ngsutils.cmdlinej.impl.AbstractOutputCommand;

@Command(name="venn", desc="Create Venn diagrams and counts", category="text")
public class Venn extends AbstractOutputCommand {
	private String[] filenames;
	private boolean svg = false;
	private String title = null;
	private String[] names = null;
	
	@UnnamedArg(name="FILE1 FILE2 {FILE3} {FILE4}", required=true)
	public void setFilename(String[] filenames) throws CommandArgumentException {
		if (filenames.length > 4 || filenames.length < 2) {
			throw new CommandArgumentException("You must specify between 2 and 4 files.");

		}
		this.filenames = filenames;
	}

	@Option(name="svg", desc="Output an SVG graphic")
	public void setSVG(boolean svg) {
		this.svg = svg;
	}
	
	@Option(name="title", desc="Title (SVG)", defaultValue="Venn diagram")
	public void setTitle(String title) {
		this.title = title;
	}
	
	@Option(name="names", desc="Sample names, comma-delimited (SVG) (defaults to filenames)")
	public void setNames(String names) {
		this.names = names.split(",");
	}
	
	@Override
	public void exec() throws Exception {
		Map<String, Integer> vals = new HashMap<String, Integer>();
		if (names != null && names.length != filenames.length) {
			throw new CommandArgumentException("The 'names' option must be the same length as the number of files");
		}
		
		int bitval = 0x1;
		for (int i=0; i<filenames.length; i++) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filenames[i])));
			String line;
			while ((line = reader.readLine()) != null) {
				String s = line.replaceAll("\n$", "");
				if (!vals.containsKey(line)) {
					vals.put(s, bitval);
				} else {
					vals.put(s, vals.get(s) | bitval);
				}
			}
			reader.close();
			
			bitval = bitval << 1;
		}
		
		int[] counts = new int[bitval];
		for (String k: vals.keySet()) {
			counts[vals.get(k)]++;
		}
		
		if (!svg) {
			outputCounts(counts);
		} else {
			outputSVG(counts);
		}
	}

	private void outputSVG(int[] counts) throws IOException {
		String codes = "ABCD";
		Map<String, String> strings = new HashMap<String, String>();

		for (int i=1; i<counts.length; i++) {
			int bitval = 0x1;
			String key = "";
			for (int j=0; j<filenames.length; j++) {
				if ((i & bitval) > 0) {
					key += codes.charAt(j);
				}
				bitval = bitval << 1;
			}
			strings.put(key, ""+counts[i]);
		}
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(getClass().getClassLoader().getResourceAsStream("org/ngsutils/txtutils/text/Venn"+filenames.length+".svg")));
		String line;
		while ((line = reader.readLine()) != null) {
			line = line.replaceAll("\\$\\$TITLE\\$\\$", title);
			line = line.replaceAll("\\$\\$LABELA\\$\\$", names != null ? names[0]: filenames[0]);
			line = line.replaceAll("\\$\\$LABELB\\$\\$", names != null ? names[1]: filenames[1]);
			if (filenames.length > 2) {
				line = line.replaceAll("\\$\\$LABELC\\$\\$", names != null ? names[2]: filenames[2]);
			}
			if (filenames.length > 3) {
				line = line.replaceAll("\\$\\$LABELD\\$\\$", names != null ? names[3]: filenames[3]);
			}
			for (String k: strings.keySet()) {
				line = line.replaceAll("\\$\\$"+k+"\\$\\$", strings.get(k));
			}
			out.write((line+"\n").getBytes());
		}
		reader.close();
	}

	private void outputCounts(int[] counts) {
		for (int i=1; i<counts.length; i++) {
			boolean first = true;
			int bitval = 0x1;
			for (int j=0; j<filenames.length; j++) {
				if ((i & bitval) > 0) {
					if (!first) {
						System.out.print(", ");
					}
					first = false;
					System.out.print(filenames[j]);
				}
				bitval = bitval << 1;
			}
			System.out.println(": "+counts[i]);
		}
	}
}
