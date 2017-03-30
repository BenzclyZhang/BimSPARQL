package nl.tue.ddss.bimsparql.geometry;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;



import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class IfcSemanticExtractor {

	private final String[] geometryEntities = {
			"ifc:IfcRepresentationItem", "ifc:IfcConnectionGeometry",
			"ifc:IfcGridAxis", "ifc:IfcObjectPlacement",
			"ifc:IfcProductRepresentation", "ifc:IfcProfileDef",
			"ifc:IfcProfileProperties", "ifc:IfcRepresentationContext",
			"ifc:IfcRepresentationMap", "ifc:IfcShapeAspect",
			"ifc:IfcRepresentation" };
	
	private Model model;
	private Model schema;
	
	public IfcSemanticExtractor(Model model,Model schema){
		this.model=model;
		this.schema=schema;
	}


/*	private Model removeResource(Resource r) {
		model.removeAll(r, null, null);
		model.removeAll(null, null, r);
		return model;
	}*/

	private List<Statement> listRelatedStatements(Resource r) {
		List<Statement> statements = new ArrayList<Statement>();
		StmtIterator st1 = model.listStatements(r, null, (RDFNode) null);
		StmtIterator st2 = model.listStatements(r, null, (String) null);
		StmtIterator st3 = model.listStatements(null, null, r);
		StmtIterator stmt = model.listStatements(null, RDF.first, r);
		while (stmt.hasNext()) {
			Statement s = stmt.next();
			Resource list = s.getSubject();
			if (list.getProperty(RDF.rest)!=null && list.getProperty(RDF.rest).getObject().equals(RDF.nil)) {
				statements.add(list.getProperty(RDF.rest));
				StmtIterator st = model.listStatements(null, RDF.rest, list);
				while (st.hasNext()) {
					statements.addAll(retrieveList(new ArrayList<Statement>(),
							st.next()));
				}
			}

		}

		while (st1.hasNext()) {
			Statement statement=st1.next();
			statements.add(statement);
            if(statement.getObject() instanceof Resource){ 
            	if (((Resource)statement.getObject()).getProperty(RDF.value)!=null){
            	statements.add(statement.getObject().asResource().getProperty(RDF.value));
            	statements.add(statement.getObject().asResource().getProperty(RDF.type));

            }            	if (((Resource)statement.getObject()).getProperty(RDF.first)!=null){
            	statements.addAll(retrieveList2(new ArrayList<Statement>(),((Resource)statement.getObject()).getProperty(RDF.first)));
            	}
            }
		}
		while (st2.hasNext()) {
			statements.add(st2.next());
		}
		while (st3.hasNext()) {
			statements.add(st3.next());
		}
		return statements;
	}

	private List<Statement> retrieveList(List<Statement> statements,
			 Statement s) {
		statements.add(s);
		Resource list = s.getSubject();
		StmtIterator st = model.listStatements(null, RDF.rest, list);
		if (st.hasNext()) {
			statements = retrieveList(statements,st.next());
		}
		return statements;
	}
	
	private List<Statement> retrieveList2(List<Statement> statements,Statement s){
		statements.add(s);
		Resource list=s.getSubject();
		Statement st = list.getProperty(RDF.rest);
		statements.add(st);
		if (st.getObject() instanceof Resource && ((Resource)st.getObject()).getProperty(RDF.first)!=null){
			statements=retrieveList2(statements,((Resource)st.getObject()).getProperty(RDF.first));
		}
		return statements;
	}

	private List<Resource> getSubClasses(Resource r) {
		List<Resource> resources = new ArrayList<Resource>();
		String query = "\n" + "prefix rdfs: <" + RDFS.getURI() + ">\n" + "\n"
				+ "select distinct ?class where {\n"
				+ "   ?class rdfs:subClassOf/rdfs:subClassOf* <" + r.getURI()
				+ "> \n" + "}";
		ResultSet results = QueryExecutionFactory.create(query, schema)
				.execSelect();
		while (results.hasNext()) {
			resources.add(results.next().getResource("class"));
		}
		return resources;
	}

	private List<Resource> getSubClasses(String classURI) {
		Resource resource = model.getResource(classURI);
		List<Resource> subclasses = getSubClasses(resource);
		return subclasses;
	}

	private List<String> getSubClassShortNames(
			String classShortName) {
		int i = classShortName.indexOf(":");
		String uri = model.getNsPrefixMap().get(classShortName.substring(0, i));
		List<Resource> subclasses = getSubClasses(
				uri + classShortName.substring(i + 1));
		List<String> subclassShortNames = new ArrayList<String>();
		for (Resource r : subclasses) {
			subclassShortNames.add(classShortName.substring(0, i) + ":"
					+ r.getLocalName());
		}
		return subclassShortNames;
	}

/*	private List<Resource> getAllResoucesOfAClass(
			String className) {
		List<Resource> resources = new ArrayList<Resource>();
		String template = "PREFIX ifc: <http://rdfrepo.ddss.nl/schema/IFC2X3#>"
				+ "SELECT DISTINCT ?s\n" 
				+ "WHERE{\n" + "?s a %s .\n" + "}";
		String q = String.format(template, className);
		Query query = QueryFactory.create(q);
		QueryExecution qexec = QueryExecutionFactory.create(query, model);
		ResultSet results = qexec.execSelect();

		while (results.hasNext()) {
			QuerySolution solution = results.next();
			resources.add(solution.getResource("s"));
		}
		return resources;
	}*/

	public List<Resource> getAllGeometricResources()
			throws FileNotFoundException {
		List<Resource> resources = new ArrayList<Resource>();
		String template = "PREFIX ifc: <"+model.getNsPrefixURI("ifc")+">\n"
				+ "SELECT DISTINCT ?s\n" + "WHERE{\n" + "?s a %s .\n" + "}";
		for (String s : geometryEntities) {
			String q = String.format(template, s);
			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				resources.add(solution.getResource("s"));
			}
			List<String> subclasses = getSubClassShortNames(s);
			for (String subclass : subclasses) {
				String qq = String.format(template, subclass);
				Query qquery = QueryFactory.create(qq);
				QueryExecution qqexec = QueryExecutionFactory.create(qquery,
						model);
				ResultSet qresults = qqexec.execSelect();
				while (qresults.hasNext()) {
					QuerySolution qsolution = qresults.next();
					resources.add(qsolution.getResource("s"));
				}
			}
		}
		return resources;
	}

	
	public List<Statement> listAllGeometricStatements() throws FileNotFoundException{
		List<Statement> statements=new ArrayList<Statement>();
		for (Resource r:getAllGeometricResources()){
			List<Statement> sts=listRelatedStatements(r);
			statements.addAll(sts);
		}
		return statements;
	}

}
