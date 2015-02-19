package org.ngsutils.txtutils;

import org.ngsutils.cmdlinej.MainBuilder;
import org.ngsutils.txtutils.tab.TabLess;
import org.ngsutils.txtutils.tab.TabView;
import org.ngsutils.txtutils.text.Missing;
import org.ngsutils.txtutils.text.Overlap;
import org.ngsutils.txtutils.text.Union;
import org.ngsutils.txtutils.text.Venn;


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
