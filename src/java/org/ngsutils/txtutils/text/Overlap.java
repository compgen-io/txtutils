package org.ngsutils.txtutils.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.ngsutils.cmdlinej.annotation.Command;
import org.ngsutils.cmdlinej.annotation.UnnamedArg;
import org.ngsutils.cmdlinej.impl.AbstractCommand;

@Command(name="overlap", desc="Find the overlap in files", category="text")
public class Overlap extends AbstractCommand {
	private String[] filenames;

	@UnnamedArg(name="FILE1 FILE2...")
	public void setFilename(String[] filenames) {
		this.filenames = filenames;
	}
	
	@Override
	public void exec() throws Exception {
		Set<String> known = new HashSet<String>();
		List<String> working = new ArrayList<String>();
		
		for (int i=0; i<filenames.length; i++) {
			working.clear();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filenames[i])));
			String line;
			while ((line = reader.readLine()) != null) {
				String s = line.replaceAll("\n$", "");
				if (i == 0 || known.contains(s)) {
					working.add(s);
				}
			}
			reader.close();
			known.clear();
			known.addAll(working);
		}
		
		for (String l:working) {
			System.out.println(l);
		}
	}
}
