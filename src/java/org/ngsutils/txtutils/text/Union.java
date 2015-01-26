package org.ngsutils.txtutils.text;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

import org.ngsutils.cmdlinej.annotation.Command;
import org.ngsutils.cmdlinej.annotation.UnnamedArg;
import org.ngsutils.cmdlinej.impl.AbstractCommand;

@Command(name="union", desc="Merge together files", category="text")
public class Union extends AbstractCommand {
	private String[] filenames;

	@UnnamedArg(name="FILE1 FILE2...")
	public void setFilename(String[] filenames) {
		this.filenames = filenames;
	}
	
	@Override
	public void exec() throws Exception {
		Set<String> known = new HashSet<String>();
		
		for (int i=0; i<filenames.length; i++) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filenames[i])));
			String line;
			while ((line = reader.readLine()) != null) {
				String s = line.replaceAll("\n$", "");
				known.add(s);
			}
			reader.close();
		}
		
		for (String l:known) {
			System.out.println(l);
		}
	}
}
