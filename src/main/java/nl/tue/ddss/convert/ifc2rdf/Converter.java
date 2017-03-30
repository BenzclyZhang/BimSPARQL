package nl.tue.ddss.convert.ifc2rdf;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;
import org.openbimstandards.vo.EntityVO;
import org.openbimstandards.vo.TypeVO;

import be.ugent.RDFWriter;
import nl.tue.ddss.convert.Header;
import nl.tue.ddss.convert.HeaderParser;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.IfcVersionException;

public class Converter {

	private String timeLog = new SimpleDateFormat("yyyyMMdd_HHmmss").format(Calendar.getInstance().getTime());

	public final String DEFAULT_PATH = "http://linkedbuildingdata.net/ifc/resources" + timeLog + "/";
	


	public static void main(String[] args) {

		Options options = new Options();

		Option baseuri = Option.builder("b").longOpt("baseuri").argName("uri").hasArg(true).required(false)
				.desc("set base uri for converted RDF instances").build();
		Option version = Option.builder("v").longOpt("version").argName("schema_version").hasArg(true).required(false)
				.desc("manually set used schema version (available values are \"IFC2X3_TC1\",\"IFC2X3_FINAL\",\"IFC4\",\"IFC4X1_RC3\",\"IFC4_ADD1\",\"IFC4_ADD2\")").build();
		Option log = new Option("l", "log", false, "generate log file");
		Option expid=new Option("e","expid",false,"use express id as a separate property for IFC object resources (by default express id is only baked into URI names of IFC object resources)");
		Option merge=new Option("m","merge",false,"merge duplicated objects (it might make roundtrip IFC file have less objects)");
		Option updns = new Option("u", "updns", false, "update ifcOWL namespaces using namespaces in referenced TTL ifcOWL files (only make sense if namespaces in TTL file changed)");
		
		options.addOption(baseuri);
		options.addOption(version);
		options.addOption(log);
		options.addOption(expid);
		options.addOption(merge);
		options.addOption(updns);

		CommandLineParser parser = new DefaultParser();
		CommandLine cmd;
		try {
			cmd = parser.parse(options, args);
			if (cmd.getArgs() == null || cmd.getArgs().length != 2) {

				HelpFormatter formatter = new HelpFormatter();
				formatter.printHelp("java -jar IfcSTEP2IfcOWL.jar <input.ifc> <output.xxx> [options]", options);
			} else {
				String baseURI = cmd.getOptionValue("baseuri");
				String ifcVersion=cmd.getOptionValue("version");
				Converter converter = new Converter();
				if (baseURI != null) {
					converter.convert(args[0], args[1], ifcVersion,baseURI, cmd.hasOption("log"),cmd.hasOption("expid"),cmd.hasOption("merge"),cmd.hasOption("updns"));
				} else {
					long start = System.currentTimeMillis();

					converter.convert(args[0], args[1], ifcVersion,converter.DEFAULT_PATH, 
							cmd.hasOption("log"),cmd.hasOption("expid"),cmd.hasOption("merge"),cmd.hasOption("updns"));
					long end = System.currentTimeMillis();
				//	System.out.println("Finished!");
					System.out.println("Total conversion time: " + ((float) (end - start)) / 1000 + " s");
				}
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			System.err.println("Parsing command line failed.  Reason: " + e.getMessage());
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp("java -jar IfcSTEP2IfcOWL.jar <input.ifc> <output.xxx> [options]", options);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	
	
	public void convert(String inputFile,String outputFile) throws IOException{
		convert(inputFile,outputFile,null);
	}
	
	public void convert(String inputFile,String outputFile,String ifcVersion) throws IOException{
		convert(inputFile,outputFile,ifcVersion,null);
	}
	
	public void convert(String inputFile,String outputFile,String ifcVersion,String baseURI) throws IOException{
		convert(inputFile,outputFile,ifcVersion,baseURI,false,false,false,false);
	}
	

	@SuppressWarnings("unchecked")
	public void convert(String inputFile, String outputFile, String ifcVersion,String baseURI, boolean logToFile,boolean expid,boolean merge,boolean updateNS)
			throws IOException {
		OntModel schema = null;
		InputStream in = null;
		
		String logFile=null;
		if (logToFile) {
			logFile = outputFile.substring(0, outputFile.length() - 4) + ".log";
		}

		try{
			if(updateNS){
		IfcVersion.initIfcNsMap();
		}else{
			IfcVersion.initDefaultIfcNsMap();
		}
		IfcVersion version=null;
		Header header = HeaderParser.parseHeader(inputFile);
		if(ifcVersion!=null){
			version=IfcVersion.getIfcVersion(ifcVersion);
		}else{
		    version=IfcVersion.getIfcVersion(header);
		}
		
		String ontNS=IfcVersion.IfcNSMap.get(version);

		// CONVERSION
			schema = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
			in = getClass().getClassLoader().getResourceAsStream(version.getLabel() + ".ttl");
			Reader reader=new InputStreamReader(in);
			schema.read(reader, null, "TTL");

			String expressTtl = "express.ttl";
			InputStream expressTtlStream = getClass().getClassLoader().getResourceAsStream(expressTtl);
			OntModel expressModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
			expressModel.read(expressTtlStream, null, "TTL");

			String rdfList = "list.ttl";
			InputStream rdfListStream = getClass().getClassLoader().getResourceAsStream(rdfList);
			OntModel listModel = ModelFactory.createOntologyModel(OntModelSpec.OWL_DL_MEM_TRANS_INF);
			listModel.read(rdfListStream, null, "TTL");

			schema.add(expressModel);
			schema.add(listModel);
			// Model im = om.getDeductionsModel();

			InputStream fis = getClass().getClassLoader().getResourceAsStream("ent" + version.getLabel() + ".ser");
			ObjectInputStream ois = new ObjectInputStream(fis);
			Map<String, EntityVO> ent = null;
			try {
				ent = (Map<String, EntityVO>) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				ois.close();
			}

			fis = getClass().getClassLoader().getResourceAsStream("typ" + version.getLabel() + ".ser");
			ois = new ObjectInputStream(fis);
			Map<String, TypeVO> typ = null;
			try {
				typ = (Map<String, TypeVO>) ois.readObject();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} finally {
				ois.close();
			}
			InputStream input = new FileInputStream(inputFile);
			RDFWriter conv = new RDFWriter(schema, expressModel, listModel, input, baseURI, ent, typ, ontNS);
			conv.setRemoveDuplicates(merge);
			conv.setExpIdAsProperty(expid);


			conv.setlogFile(logFile);
			FileOutputStream out = new FileOutputStream(outputFile);
			Lang lang=RDFLanguages.filenameToLang(outputFile);
			String s = "# baseURI: " + baseURI;
			s += "\r\n# imports: " + ontNS.substring(0, ontNS.length()-1) + "\r\n\r\n";
			out.write(s.getBytes());
			out.flush();
			System.out.println("Start to convert...");
			conv.parseModel2Stream(out, header,lang);
		} catch (IOException e1) {
			e1.printStackTrace();
		}  catch (IfcVersionException e) {
//			LOGGER.log(Level.SEVERE,"Caught IfcVersionException: "+e.getMessage());
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			try {
				in.close();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
			try {
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	}








}
