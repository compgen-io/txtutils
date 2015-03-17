package io.compgen.txtutils.text;

import io.compgen.annotation.Command;
import io.compgen.annotation.UnnamedArg;
import io.compgen.impl.AbstractCommand;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Command(name="missing", desc="Find the lines in file1, but not file2 (etc...)", category="text")
public class Missing extends AbstractCommand {
	private String[] filenames;

	@UnnamedArg(name="FILE1 FILE2...", required=true)
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
				if (i == 0) {
					known.add(s);
				} else {
					known.remove(s);
				}
			}
			reader.close();
		}
		
		for (String l:known) {
			System.out.println(l);
		}
	}
}
