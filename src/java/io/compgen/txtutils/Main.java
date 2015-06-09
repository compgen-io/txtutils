package io.compgen.txtutils;

import io.compgen.cmdline.Help;
import io.compgen.cmdline.License;
import io.compgen.cmdline.MainBuilder;
import io.compgen.txtutils.tab.TabLess;
import io.compgen.txtutils.tab.TabView;
import io.compgen.txtutils.text.Fisher;
import io.compgen.txtutils.text.Missing;
import io.compgen.txtutils.text.Overlap;
import io.compgen.txtutils.text.Union;
import io.compgen.txtutils.text.Venn;


public class Main {
	public static void main(String[] args) {
		try {
			new MainBuilder()
			.setProgName("txtutils")
			.setHelpHeader("txtutils - Text/tab delimited utilities\n---------------------------------------")
			.setDefaultUsage("Usage: txtutils cmd [options]")
			.setHelpFooter("http://compgen.io/txtutils\n"+MainBuilder.readFile("VERSION"))
			.setCategoryOrder(new String[] {"tab", "text", "help"})
			.addCommand(License.class)
			.addCommand(Help.class)
			.addCommand(TabView.class)
			.addCommand(TabLess.class)
			.addCommand(Overlap.class)
			.addCommand(Missing.class)
			.addCommand(Venn.class)
			.addCommand(Union.class)
			.addCommand(Fisher.class)
			.findAndRun(args);
		} catch (Exception e) {
			System.err.println(e.getMessage());
			System.err.println(e.getCause());
			e.printStackTrace();
			System.exit(1);
		}

	}
}
