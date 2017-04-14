package nl.tue.ddss.bimsparql.geometry.partition;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.List;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import nl.tue.ddss.convert.Namespace;

public class GeometryTripleExtractor {
	


	static final String[] geometryEntities = {
			"ifc:IfcRepresentationItem", "ifc:IfcConnectionGeometry",
			"ifc:IfcGridAxis", "ifc:IfcObjectPlacement",
			"ifc:IfcProductRepresentation", "ifc:IfcProfileDef",
			"ifc:IfcProfileProperties", "ifc:IfcRepresentationContext",
			"ifc:IfcRepresentationMap", "ifc:IfcShapeAspect",
			"ifc:IfcRepresentation" };
	
	private Model model=ModelFactory.createDefaultModel();
	private Model schema=ModelFactory.createDefaultModel();
	
	protected final Property HASNEXT = schema.getProperty(Namespace.LIST + "hasNext");
	protected final Property HASCONTENTS = schema.getProperty(Namespace.LIST + "hasContents");
	protected final Property HASBOOLEAN = schema.getProperty(Namespace.EXPRESS + "hasBoolean");
	protected final Property HASSTRING = schema.getProperty(Namespace.EXPRESS + "hasString");
	protected final Property HASLOGICAL = schema.getProperty(Namespace.EXPRESS + "hasLogical");
	protected final Property HASDOUBLE = schema.getProperty(Namespace.EXPRESS + "hasDouble");
	protected final Property HASINTEGER = schema.getProperty(Namespace.EXPRESS + "hasInteger");
	protected final Property HASHEXBINARY = schema.getProperty(Namespace.EXPRESS + "hasHexBinary");
	
	public GeometryTripleExtractor(Model model,Model schema){
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
		StmtIterator st3 = model.listStatements(null, null, r);
		StmtIterator stmt = model.listStatements(null, model.getProperty(Namespace.LIST+"hasContents"), r);
		while (stmt.hasNext()) {
			Statement s = stmt.next();
			Resource list = s.getSubject();
			if (list.getProperty(model.getProperty(Namespace.LIST+"hasNext"))!=null) {
				statements.add(list.getProperty(model.getProperty(Namespace.LIST+"hasNext")));
				StmtIterator st = model.listStatements(null, model.getProperty(Namespace.LIST+"hasNext"), list);
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
            	Resource object=(Resource)statement.getObject();
            	Statement s=object.getProperty(HASBOOLEAN);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	s=object.getProperty(HASSTRING);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	s=object.getProperty(HASDOUBLE);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	s=object.getProperty(HASLOGICAL);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	s=object.getProperty(HASINTEGER);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	s=object.getProperty(HASHEXBINARY);
            	if(s!=null){
            		statements.add(statement);
            		statements.add(object.getProperty(RDF.type));
            	}
            	if (((Resource)statement.getObject()).getProperty(HASCONTENTS)!=null){
            	statements.addAll(retrieveList2(new ArrayList<Statement>(),((Resource)statement.getObject()).getProperty(HASCONTENTS)));
            	}
            }
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
		StmtIterator st = model.listStatements(null, model.getProperty(Namespace.LIST+"hasNext"), list);
		if (st.hasNext()) {
			statements = retrieveList(statements,st.next());
		}
		return statements;
	}
	
	private List<Statement> retrieveList2(List<Statement> statements,Statement s){
		statements.add(s);
		Resource list=s.getSubject();
		Statement st = list.getProperty(model.getProperty(Namespace.LIST+"hasNext"));
		if(st!=null){
		statements.add(st);
		if (st.getObject() instanceof Resource && ((Resource)st.getObject()).getProperty(model.getProperty(Namespace.LIST+"hasContents"))!=null){
			statements=retrieveList2(statements,((Resource)st.getObject()).getProperty(model.getProperty(Namespace.LIST+"hasContents")));
		}
		}
		return statements;
	}

	public List<Resource> getAllGeometricResources()
			throws FileNotFoundException {
		List<Resource> resources = new ArrayList<Resource>();
		String template = "PREFIX ifc: <"+model.getNsPrefixURI("ifcowl")+">\n"
				+ "SELECT DISTINCT ?s\n" + "WHERE{\n" + "?s a %s .\n" + "}";
		OntModel ontology=ModelFactory.createOntologyModel();
		ontology.add(model);
		ontology.add(schema);
		for (String s : geometryEntities) {
			String q = String.format(template, s);
			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.create(query, ontology);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				resources.add(solution.getResource("s"));
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
