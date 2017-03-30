package nl.tue.ddss.bimsparql;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.util.Iterator;

import javax.xml.parsers.ParserConfigurationException;

import org.topbraid.spin.system.SPINModuleRegistry;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.graph.compose.MultiUnion;
import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.ontology.OntModelSpec;
import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ReadWrite;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.tdb.TDBFactory;

import nl.tue.ddss.bimsparql.BimSPARQL;


public class Example2 {

	static final String spt = "";
	static final String ifcowl = "";

	public static void main(String[] args) throws ClassNotFoundException,
			IOException, ParserConfigurationException,
			SAXException, URISyntaxException {
		System.out.println("Load model...");
		// Model model=ModelFactory.createDefaultModel();
		// InputStream in=new
		// FileInputStream("D:/Demo/Example TTL and IFC files/Duplex_A_20110505.ttl");
		// InputStream in=new
		// FileInputStream("D:/Demo/Example TTL and IFC files/Public_TTL_Files/20151023_http.openifcmodel.cs.auckland.ac.nz/091210Med_Dent_Clinic_Arch.ttl");
		// InputStream in=new
		// FileInputStream("D:/Demo/Example TTL and IFC files/D'Amato-Lucia.ttl");
		// model.read(in,null,"TTL");
		Dataset dataset = TDBFactory.createDataset("E:/TDB");
		dataset.begin(ReadWrite.READ);
		Model schema = dataset.getNamedModel("IFC2X3_TC1.owl");
		Iterator<String> names = dataset.listNames();

		//Model model = dataset.getNamedModel("Duplex_A_20110505.ttl");
		// Model model=dataset.getNamedModel("Office_A_20110811_optimized.ttl");
		 Model model=dataset.getNamedModel("091210Med_Dent_Clinic_Arch.ttl");
		 System.out.println(model.size());
         model.add(schema);
		System.out
				.println("Initialize BimSPARQL and parse geometry from the model...");
	//	BimSPARQL.init(model);
	//	InputStream in=new FileInputStream("BimSPARQL_example.ttl");
	//	OntModel spinModel = ModelFactory.createOntologyModel(
	//			OntModelSpec.OWL_MEM);
    //    spinModel.read(in,null,"TTL");		
	//	SPINModuleRegistry.get().init();
	//	SPINModuleRegistry.get().registerAll(spinModel, null);
		System.out.println("Start query...");
		System.out.println(model.size());
		String q1 = "PREFIX ifcowl: <http://www.buildingsmart-tech.org/ifcOWL/IFC2X3_TC1#>\n"
				+ "PREFIX qrw:<http://bimsparql.org/query-rewriting#>\n"
				+"PREFIX pset:<http://bimsparql.org/pset#>\n"
				+"SELECT ?product \n" + "WHERE{\n"
				+ "?wall a ifcowl:IfcWallStandardCase .\n"
				+ " ?wall qrw:isContainedIn ?storey .\n"
				+ "  ?wall pset:isExternal true .\n"
				+ " ?storey a ifcowl:IfcBuildingStorey .\n"
				+ "} GROUP BY ?storey";


		long start = System.currentTimeMillis();
		Query query1 = QueryFactory.create(q1);
		QueryExecution qe1 = QueryExecutionFactory.create(query1, model);
		ResultSet qresults1 = qe1.execSelect();
		int i = 0;
		ResultSetFormatter.out(qresults1);
		long end = System.currentTimeMillis();
		System.out.println("Query time: " + (end - start) + " ms");
		System.out.println("Result size: " + i);

	}
}

