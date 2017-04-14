package nl.tue.ddss.bimsparql.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.topbraid.spin.arq.ARQ2SPIN;
import org.topbraid.spin.arq.ARQFactory;
import org.topbraid.spin.model.Function;
import org.topbraid.spin.model.Query;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.vocabulary.ARG;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.topbraid.spin.vocabulary.SPL;
import org.xml.sax.SAXException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDFS;

import nl.tue.ddss.bimsparql.BimSPARQL;
import nl.tue.ddss.bimsparql.BimSPARQLNS;
import nl.tue.ddss.convert.Namespace;

public class FireSeparationDistance {
	
	final static String IBC="http://www.iccsafe.org/codes-tech-support/codes/2015-i-codes/ibc/";

	final static String prefixes = "PREFIX ifc: <"+Namespace.IFC2X3_TC1+">\n"
			+ "PREFIX list: <"+Namespace.LIST+">\n"
			+ "PREFIX expr: <"+Namespace.EXPRESS+">\n" + "PREFIX schm:<"+BimSPARQLNS.SCHM+">\n"
			+ "PREFIX pset:<"+BimSPARQLNS.PSET+">\n" + "PREFIX spt:<"+BimSPARQLNS.SPT+">\n"
			+ "PREFIX pdt:<"+BimSPARQLNS.PDT+">\n"+"PREFIX ibc:<"+IBC+">\n";

	final static String mainQuery = "SELECT ?wall ?Ap ?Au ?ap ?au ?r\n" + "WHERE{\n"
			+ "?wall a ifc:IfcWall .\n" + "?wall pset:isExternal true .\n" + "?wall ibc:hasFireSeparationDistance ?d .\n"
			+ "?wall schm:isContainedIn ?storey .\n" + "?storey pset:sprinklerProtection ?bool .\n"
			+ "?wall ibc:hasAp ?Ap .\n" + "?wall ibc:hasAu ?Au .\n"
			+ "BIND (ibc:allowableArea_T705-8(?d,true,?bool) AS ?ap) .\n"
			+ "BIND (ibc:allowableArea_T705-8(?d,false,?bool) AS ?au) .\n" + " BIND ((?Ap/?ap+?Au/?au) AS ?r) .\n" + "}";
	
	final static String test1="SELECT ?wall\n" + "WHERE{\n"
			+ "?wall a ifc:IfcWall .\n" + "?wall pset:isExternal true .\n}";
	
	final static String test2="SELECT ?wall ?d\n" + "WHERE{\n"
			+ "?wall a ifc:IfcWall .\n" + "?wall pset:isExternal true .\n?wall ibc:hasFireSeparationDistance ?d .\n}";
	
	final static String test3 = "SELECT ?wall ?Ap\n" + "WHERE{\n"
			+ "?wall a ifc:IfcWall .\n"
			+ "?wall schm:isContainedIn ?storey .\n" + "?storey pset:sprinklerProtection ?bool .\n"
			+ "?wall ibc:hasAu ?Ap .\n}";
	
	final static String test4 = "SELECT ?wall ?Ap\n" + "WHERE{\n"
			+ "?wall a ifc:IfcWall .\n"
			+ "?wall ibc:hasA ?storey .\n" + "?storey pset:sprinklerProtection ?bool .\n"
			+ "?wall ibc:hasAu ?Ap .\n}";

	final static String hasFireSeparationDistance = "SELECT (MIN(?d) AS ?distance)\n" + "WHERE{ ?line a ifc:IfcAnnotation .\n"
			+ "?line ifc:name_IfcRoot ?name .\n" + "?name expr:hasString \"Lot Line\" .\n"
			+ "(?arg1 ?line) spt:distanceXY ?d .}GROUP By ?arg1";

	final static String hasAp = "SELECT (?windowArea/?wallArea AS ?Ap)\n" + "WHERE{"
			+ "?arg1 pdt:hasGrossWallArea ?wallArea .\n" + "?arg1 ibc:hasProtectedOpeningArea ?windowArea .\n"+"}";
	
	final static String hasAu = "SELECT (?windowArea/?wallArea AS ?area)\n" + "WHERE{"
			+ "?arg1 pdt:hasGrossWallArea ?wallArea .\n" + "?arg1 ibc:hasUnprotectedOpeningArea ?windowArea .\n"+"}";
	
	final static String hasProtectedOpeningArea = "SELECT (SUM(?windowArea) AS ?area)\n" + "WHERE{"
			+"?window schm:isPlacedIn ?arg1 ."
			+ "    ?window pset:fireRating \"OH-45\" ." + "?window pdt:hasWindowArea ?windowArea ." + "} GROUP BY ?arg1";
    
	final static String hasUnprotectedOpeningArea = "SELECT (SUM(?windowArea) AS ?area)\n" + "WHERE{"
			+"?window schm:isPlacedIn ?arg1 ."
			+ "    ?window pset:fireRating \"OH-20\" ." + "?window pdt:hasWindowArea ?windowArea ." + "} GROUP BY ?arg1";

	public static void main(String[] args)
			throws ClassNotFoundException, IOException, ParserConfigurationException, SAXException, URISyntaxException {
		Model model = ModelFactory.createDefaultModel();
		InputStream in = FireSeparationDistance.class.getClassLoader().getResourceAsStream("lifeline_final.ttl");
		model.read(in, null, "TTL");
		System.out.println(model.size());
		Model geometryModel = ModelFactory.createDefaultModel();
		InputStream ing = FireSeparationDistance.class.getClassLoader().getResourceAsStream("lifeline_final_geometry.ttl");
		geometryModel.read(ing, null, "TTL");
		System.out.println(geometryModel.size());
		Model schema=loadModel("IFC2X3_TC1.ttl","TTL");
		BimSPARQL.init(model, geometryModel);
		Model ibcspin = ModelFactory.createDefaultModel();
		addMagicProperty(ibcspin, IBC+"hasFireSeparationDistance", prefixes+hasFireSeparationDistance, 1);
		addMagicProperty(ibcspin, IBC+"hasProtectedOpeningArea", prefixes+hasProtectedOpeningArea, 1);
		addMagicProperty(ibcspin, IBC+"hasUnprotectedOpeningArea", prefixes+hasUnprotectedOpeningArea, 1);
		addMagicProperty(ibcspin, IBC+"hasAp", prefixes+hasAp, 1);
		addMagicProperty(ibcspin, IBC+"hasAu", prefixes+hasAu, 1);
		Model ibc=loadIbcData();
		SPINModuleRegistry.get().registerAll(ibc, null);
		for (Function f:SPINModuleRegistry.get().getFunctions())
		{
			System.out.println(f.getURI());
		}
		com.hp.hpl.jena.query.Query query = QueryFactory.create(prefixes + mainQuery);
		
		OntModel union = ModelFactory.createOntologyModel();
		union.add(schema);
		union.add(model);
		union.add(geometryModel);
		union.add(ibc);
		System.out.println(union.size());
		QueryExecution qe = QueryExecutionFactory.create(query, union);
		com.hp.hpl.jena.query.ResultSet result = qe.execSelect();
        ResultSetFormatter.out(result);

        }

	
	public static Model loadIbcData(){
		Model table=loadModel("Table_705.8.ttl","TTL");
		Model tableFunction=loadModel("Table_705.8_Function.ttl","TTL");
		Model qudtExpr=loadModel("QudtExpr.ttl","TTL");
		Model qudtSpin=loadModel("http://www.qudt.org/qudt/owl/1.0.0/qudt-spin.owl",null);
		Model unit=loadModel("http://www.qudt.org/qudt/owl/1.0.0/unit.owl",null);
		Model union=ModelFactory.createDefaultModel();
		Model spModel = SP.getModel();
		Model spinModel = SPIN.getModel();
		union.add(table);
		union.add(tableFunction);
		union.add(qudtExpr);
		union.add(qudtSpin);
		union.add(unit);
		union.add(spModel);
		union.add(spinModel);
		return union;
	}
	
	public static Model loadModel(String url,String lang){
		Model model=ModelFactory.createDefaultModel();
		if(lang==null){
			model.read(url);
		}else{
			InputStream in=FireSeparationDistance.class.getClassLoader().getResourceAsStream(url);
			model.read(in,null,lang);
		}
		return model;
	}

	public static void addMagicProperty(Model model, String uri, String query, int argNum) {
		com.hp.hpl.jena.query.Query arqQuery = ARQFactory.get().createQuery(model, query);
		Query spinQuery = new ARQ2SPIN(model).createQuery(arqQuery, null);
		Function magicProperty = model.createResource(uri, SPIN.MagicProperty).as(Function.class);
		magicProperty.addProperty(SPIN.body, spinQuery);
		for (int i = 1; i <= argNum; i++) {
			Resource argument = model.createResource(SPL.Argument);
			argument.addProperty(SPL.predicate, model.getProperty(ARG.NS + "arg" + i));
			argument.addProperty(SPL.valueType, RDFS.Resource);
			magicProperty.addProperty(SPIN.constraint, argument);
			SPINModuleRegistry.get().register(magicProperty, null, true);

		}

	}

}
