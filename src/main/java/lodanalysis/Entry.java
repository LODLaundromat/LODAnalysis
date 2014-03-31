package lodanalysis;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import lodanalysis.authority.CalcAuthority;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class Entry {
	List<String> possibleAnalysis;
	CommandLine line;

	public Entry(String[] args)  {
		Arrays.asList(new String[]{"authority"});
		parseArgs(args);
	}

	private void parseArgs(String[] args) {
		Options options = getOptions();
		CommandLineParser parser = new GnuParser();
		HelpFormatter help = new HelpFormatter();
		try {
			line = parser.parse(getOptions(), args);
			validateParameters();
		} catch (ParseException e) {
			String jarName = new java.io.File(Entry.class.getProtectionDomain()
					  .getCodeSource()
					  .getLocation()
					  .getPath())
					.getName();
			String header = "Java entry class to run our LOD cloud experiments. Usage: " + jarName + " [[OPTION]]... " + possibleAnalysis.toString();
			if (e.getMessage().length() > 0) System.err.println("Wrong parameters: " + e.getMessage());
			help.printHelp(header, options);
			System.exit(1);
		}
		run();
	}
	
	private void run() {
		for (Object arg: line.getArgList()) {
			String argString = (String)arg;
			if (possibleAnalysis.contains(argString)) {
				if (argString.equals("authority")) {
					new CalcAuthority(this);
				}
				break;
			} else {
				System.out.println("murml murml, skipping unrecognized argument " + argString);
			}
		}
	}

	private void validateParameters() throws ParseException{
		if (line.hasOption("help")) throw new ParseException("");
		if (line.getArgList().size() == 0) throw new ParseException("You forgot to tell me what task you want me to run!");
		boolean hasValidAnalysisArg = false;
		for (Object arg: line.getArgList()) {
			String argString = (String)arg;
			if (possibleAnalysis.contains(argString)) {
				hasValidAnalysisArg = true;
				break;
			}
		}
		
		if (!hasValidAnalysisArg) throw new ParseException("Could not identify from your arguments what type of analysis to run");
		if (!line.hasOption("path")) throw new ParseException("Please specify the path where we can find the dataset directories");
		
		if (!new File(line.getOptionValue("path")).exists()) throw new ParseException("The datasets path you specified does not exist");
		if (!new File(line.getOptionValue("path")).isDirectory()) throw new ParseException("The datasets path you specified is not a directory");
		
	}

	public boolean isVerbose() {
		return line.hasOption("verbose");
	}

	private Options getOptions() {
		Options options = new Options();
		Option verbose = new Option("verbose", "be extra verbose");
		@SuppressWarnings("static-access")
		 Option path = OptionBuilder
		 .withArgName("path")
		 .hasArg()
		 .withDescription("Path containing all the dataset directories")
		 .create("path");
		Option help = new Option("help", "print this message");

		options.addOption(help);
		options.addOption(verbose);
		 options.addOption(path);
		return options;
	}

	public static void main(String[] args)  {
		new Entry(args);
	}
}
