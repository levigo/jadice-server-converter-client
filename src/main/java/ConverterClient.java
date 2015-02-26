import java.io.File;
import java.util.List;

import javafx.application.Application;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.levigo.jadice.server.converterclient.CommandLineConversion;
import org.levigo.jadice.server.converterclient.JobCardFactory;
import org.levigo.jadice.server.converterclient.configurations.WorkflowConfiguration;
import org.levigo.jadice.server.converterclient.gui.ConverterClientApplication;


public class ConverterClient {
	
	private static final String DEFAULT_SERVER_LOC = "tcp://localhost:61616";

	public static void main(String[] args) throws Exception {
		Options opt = buildOptions();
		CommandLineParser parser = new GnuParser();
		try {
			CommandLine cl = parser.parse(opt, args);
			evalCommandLine(opt, cl);
		} catch (ParseException e) {
			printHelp(opt);
		}
	}

	private static void evalCommandLine(Options opt, CommandLine cl) throws Exception {
		if (cl.hasOption("help"))
			printHelp(opt);
		else if (cl.hasOption("gui")) {
        Application.launch(ConverterClientApplication.class);
		} else if (cl.hasOption("list")) {
			System.out.println("Available Configurations (<ID>: <description>)");
			String format = "%15s: %s";
			for (WorkflowConfiguration cfg : JobCardFactory.getInstance().getConfigurations()) {
				System.out.println(String.format(format, cfg.getID(), cfg.getDescription()));
			}
		} else if (cl.hasOption("convert") && cl.getOptionValues("convert").length == 3) {
			String[] params = cl.getOptionValues("convert");
			String configID = params[0];
			String inFile = params[1];
			String outFile = params[2];
			String serverLocation = cl.getOptionValue("server", DEFAULT_SERVER_LOC);
			CommandLineConversion conversion = new CommandLineConversion(configID, inFile, outFile, serverLocation);
			System.out.println("Starting conversion");
			List<File> files = conversion.runConversion();
			System.out.println("Saving result(s) as:");
			for (File file : files) {
				System.out.println(file.getAbsolutePath());
			}
			
			
		} else {
			printHelp(opt);
		}
	}
	
	private static Options buildOptions() {
		Options options = new Options();
		OptionGroup grp = new OptionGroup();
		grp.addOption(new Option("gui", false, "Start Converter Client GUI"));
		grp.addOption(new Option("list", false, "List available conversion configurations"));
		grp.addOption(new Option("help", false, "Print this message"));
		
		Option convertOpt = new Option("convert", true, "Run conversion");
		convertOpt.setArgs(3);
		// quirky workaround as Multi-Args are not really supported
		convertOpt.setArgName("configID> <inputFile> <outputFile");
		convertOpt.setType(Integer.class);
		grp.addOption(convertOpt);
		
		options.addOptionGroup(grp);
		options.addOption("server", true, "jadice server's address, default: " + DEFAULT_SERVER_LOC);
		return options;
	}
	
	private static void printHelp(Options options) {
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("converterclient", options, true);
	}

}
