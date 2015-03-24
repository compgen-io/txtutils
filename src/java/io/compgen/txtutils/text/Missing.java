package io.compgen.txtutils.text;

import io.compgen.cmdline.annotation.Command;
import io.compgen.cmdline.annotation.Exec;
import io.compgen.cmdline.annotation.UnnamedArg;
import io.compgen.cmdline.exceptions.CommandArgumentException;
import io.compgen.cmdline.impl.AbstractCommand;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Command(name="missing", desc="Find the lines in file1, but not file2 (etc...)", category="text")
public class Missing extends AbstractCommand {
	private String[] filenames;

	@UnnamedArg(name="FILE1 FILE2...")
	public void setFilename(String[] filenames) throws CommandArgumentException {
		if (filenames.length < 2) {
			throw new CommandArgumentException("You must specify at least 2 files.");
		}
		this.filenames = filenames;
	}
	
	@Exec
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
