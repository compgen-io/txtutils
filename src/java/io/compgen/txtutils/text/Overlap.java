package io.compgen.txtutils.text;

import io.compgen.cmdline.annotation.Command;
import io.compgen.cmdline.annotation.Exec;
import io.compgen.cmdline.annotation.Option;
import io.compgen.cmdline.annotation.UnnamedArg;
import io.compgen.cmdline.exceptions.CommandArgumentException;
import io.compgen.cmdline.impl.AbstractCommand;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Command(name="overlap", desc="Find the overlap in files", category="text")
public class Overlap extends AbstractCommand {
	private String[] filenames;
	private boolean ignoreCase = false;


	@UnnamedArg(name="FILE1 FILE2...")
	public void setFilename(String[] filenames) throws CommandArgumentException {
		if (filenames.length < 2) {
			throw new CommandArgumentException("You must specify at least 2 files.");
		}
		this.filenames = filenames;
	}
	@Option(charName="i", name="ignore-case", desc="Ignore case")
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	

	@Exec
	public void exec() throws Exception {
		Set<String> known = new HashSet<String>();
		List<String> working = new ArrayList<String>();
		
		for (int i=0; i<filenames.length; i++) {
			working.clear();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filenames[i])));
			String line;
			while ((line = reader.readLine()) != null) {
				String s = line.replaceAll("\n$", "");
				if (ignoreCase) {
					s = s.toUpperCase();
				}

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
