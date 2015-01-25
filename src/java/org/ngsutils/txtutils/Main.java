package org.ngsutils.txtutils;

import org.ngsutils.cmdlinej.MainBuilder;


public class Main {
	public static void main(String[] args) throws Exception {
		MainBuilder mb = new MainBuilder()
			.setProgName("txtutils")
			.setUsage("txtutils - Text/tab delimited utilities\n\nUsage: txtutils cmd [options]")
			.addCommand(TabView.class)
			.addCommand(Overlap.class)
			.addCommand(Missing.class)
			.addCommand(Union.class);
		
		if (args.length == 0) {
			mb.showCommands();
		} else {
			mb.findAndRun(args);
		}
	}
}
