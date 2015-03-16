package io.compgen.txtutils;

import io.compgen.txtutils.tab.TabLess;
import io.compgen.txtutils.tab.TabView;
import io.compgen.txtutils.text.Missing;
import io.compgen.txtutils.text.Overlap;
import io.compgen.txtutils.text.Union;
import io.compgen.txtutils.text.Venn;

import org.ngsutils.cmdlinej.MainBuilder;


public class Main {
	public static void main(String[] args) throws Exception {
		MainBuilder mb = new MainBuilder()
			.setProgName("txtutils")
			.setUsage("txtutils - Text/tab delimited utilities\n\nUsage: txtutils cmd [options]")
			.addCommand(TabView.class)
			.addCommand(TabLess.class)
			.addCommand(Overlap.class)
			.addCommand(Missing.class)
			.addCommand(Venn.class)
			.addCommand(Union.class);
		
		if (args.length == 0) {
			mb.showCommands();
		} else {
			mb.findAndRun(args);
		}
	}
}
