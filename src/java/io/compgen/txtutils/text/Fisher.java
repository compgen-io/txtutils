package io.compgen.txtutils.text;

import io.compgen.cmdline.annotation.Command;
import io.compgen.cmdline.annotation.Exec;
import io.compgen.cmdline.annotation.Option;
import io.compgen.cmdline.annotation.UnnamedArg;
import io.compgen.cmdline.exceptions.CommandArgumentException;
import io.compgen.cmdline.impl.AbstractCommand;
import io.compgen.ngsutils.support.stats.FisherExact;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

@Command(name="fisher", desc="Calculate Fisher test 2x2 table for overlaps between two sets, correcting for background overlap", category="text")
public class Fisher extends AbstractCommand {
	private String[] filenames;
	private boolean ignoreCase = false;

	@UnnamedArg(name="Set1 Background1 Set2 Background2")
	public void setFilename(String[] filenames) throws CommandArgumentException {
		if (filenames.length != 4) {
			throw new CommandArgumentException("You must specify exactly 4 files.");
		}
		this.filenames = filenames;
	}
	@Option(charName="i", name="ignore-case", desc="Ignore case")
	public void setIgnoreCase(boolean ignoreCase) {
		this.ignoreCase = ignoreCase;
	}
	

	@Exec
	public void exec() throws Exception {
		Set<String> set1 = new HashSet<String>();
		Set<String> set2 = new HashSet<String>();
		Set<String> bg1 = new HashSet<String>();
		Set<String> bg2 = new HashSet<String>();

		for (int i=0; i<filenames.length; i++) {
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(filenames[i])));
			String line;
			while ((line = reader.readLine()) != null) {
				String s = line.replaceAll("\n$", "");
				if (ignoreCase) {
					s = s.toUpperCase();
				}
				switch(i) {
				case 0:
					set1.add(s);
					break;
				case 1:
					bg1.add(s);
					break;
				case 2:
					set2.add(s);
					break;
				case 3:
					bg2.add(s);
					break;
				}
			}
			reader.close();
		}
		
		Set<String> totalBackground = new HashSet<String>();
		for (String foo: bg1) {
			if (bg2.contains(foo)) {
				totalBackground.add(foo);
			}
		}

		Set<String> set1Back = new HashSet<String>();
		for (String foo: set1) {
			if (totalBackground.contains(foo)) {
				set1Back.add(foo);
			}
		}
		
		Set<String> set2Back = new HashSet<String>();
		for (String foo: set2) {
			if (totalBackground.contains(foo)) {
				set2Back.add(foo);
			}
		}

		int A=0;

		for (String foo: set1Back) {
			if (set2Back.contains(foo)) {
				A++;
			}
		}

		int B=set1Back.size() - A;
		int C=set2Back.size() - A;
		int D=totalBackground.size() - A - B - C;

		System.out.println("Set1/Set2 overlap\t"+ A);

		System.out.println("Set1 total\t"+ set1.size());
		System.out.println("Set1 (corrected) total\t"+ set1Back.size());

		System.out.println("Set2 total\t"+ set2.size());
		System.out.println("Set2 (corrected) total\t"+ set2Back.size());
		System.out.println("Combined background\t"+ totalBackground.size());

		System.out.println("");
		
		System.out.println("A\t"+ A);
		System.out.println("B\t"+ B);
		System.out.println("C\t"+ C);
		System.out.println("D\t"+ D);

		System.out.println("");
		System.out.println("Fisher-exact p-value (two-tailed):\t" + new FisherExact().calcTwoTailedPvalue(A,B,C,D));
		
	}
}
