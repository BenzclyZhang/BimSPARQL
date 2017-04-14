/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package nl.tue.ddss.convert.rdf2ifc;


import java.io.IOException;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.IfcVersionException;


public class Converter {
	
	
public static void main(String[] args) throws IOException, IfcVersionException{

 	
	Options options=new Options();
	Option logfile = new Option("l", "logfile", false, "generate log file");
	Option version = Option.builder("v").longOpt("version").argName("version").hasArg(true).required(false)
			.desc("specify IFC schema version, if not specified, use default one parsed from model.").build();
	Option updns = new Option("u", "updns", false, "whether using update namespaces in ifcOWL files in resources folder"); 
//	Option converter = Option.builder("c").longOpt("converter").argName("type").hasArg(true).required(false)
//			.desc("set which converter to use").build();
    options.addOption(logfile).addOption(version).addOption(updns);
	
		try { 	
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;

				cmd = parser.parse(options, args);
		if(cmd.getArgs()==null||cmd.getArgs().length!=2){
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar IfcOWL2IfcSTEP.jar <input.xxx> <output.ifc> [options]", options);
			
		}else{	
			Converter convert=new Converter();
			long start=System.currentTimeMillis();
			if(cmd.hasOption("version")){
				String v=cmd.getOptionValue("version");
				IfcVersion ifcVersion=IfcVersion.getIfcVersion(v);
				convert.convert(args[0], args[1],cmd.hasOption("logfile"),ifcVersion,cmd.hasOption("updns"));
			}else{
				convert.convert(args[0], args[1],cmd.hasOption("logfile"),null,cmd.hasOption("updns"));
			}
				
				long end=System.currentTimeMillis();
		    	System.out.println("Total conversion time: "+((float)(end-start))/1000+" s");
		}
	} catch (ParseException e) {
		// TODO Auto-generated catch block
		System.err.println( "Parsing command line failed.  Reason: " + e.getMessage() );
		HelpFormatter formatter = new HelpFormatter();
		formatter.printHelp("java -jar IfcOWL2IfcSTEP.jar <input.xxx> <output.ifc> [options]", options);
	}
}


public void convert(String inputFile, String outputFile,boolean logFile,IfcVersion version,boolean updns) throws IOException {
	IfcWriter writer=new StreamIfcWriter(version,updns);
	writer.convert(inputFile,outputFile,logFile);	
}
	
}
