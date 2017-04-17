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
package nl.tue.ddss.bimsparql.geometry.partition;

import java.io.FileNotFoundException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.topbraid.spin.util.JenaUtil;

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
	private Model geometryModel=ModelFactory.createDefaultModel();
	
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

	private HashSet<Statement> listRelatedStatements(Resource r) {
		HashSet<Statement> statements = new HashSet<Statement>();
		StmtIterator st1 = model.listStatements(r, null, (RDFNode) null);
		StmtIterator st3 = model.listStatements(null, null, r);
		StmtIterator stmt = model.listStatements(null, model.getProperty(Namespace.LIST+"hasContents"), r);
		while (stmt.hasNext()) {
			Statement s = stmt.next();
			Resource list = s.getSubject();
			if (list.getProperty(model.getProperty(Namespace.LIST+"hasNext"))!=null) {
				statements.add(list.getProperty(model.getProperty(Namespace.LIST+"hasNext")));
				statements.add(list.getProperty(RDF.type));
				StmtIterator st = model.listStatements(null, model.getProperty(Namespace.LIST+"hasNext"), list);
				while (st.hasNext()) {
					statements.addAll(retrieveList(new HashSet<Statement>(),
							st.next()));
				}
			}

		}

		while (st1.hasNext()) {
			Statement statement=st1.next();
			statements.add(statement);
            if(statement.getObject() instanceof Resource){ 
                statements=removePrimaryTypes(statements,statement);
            	if (((Resource)statement.getObject()).getProperty(HASCONTENTS)!=null){
            	statements.addAll(retrieveList2(new HashSet<Statement>(),((Resource)statement.getObject()).getProperty(HASCONTENTS)));
            	}
            }
		}
		while (st3.hasNext()) {
			statements.add(st3.next());
		}
		return statements;
	}
	
	private HashSet<Statement> removePrimaryTypes(HashSet<Statement> statements, Statement statement){
    	Resource object=(Resource)statement.getObject();
    	Statement s=object.getProperty(HASBOOLEAN);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
    	s=object.getProperty(HASSTRING);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
    	s=object.getProperty(HASDOUBLE);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
    	s=object.getProperty(HASLOGICAL);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
    	s=object.getProperty(HASINTEGER);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
    	s=object.getProperty(HASHEXBINARY);
    	if(s!=null){
    		statements.add(s);
    		statements.add(object.getProperty(RDF.type));
    	}
		return statements;
	}

	private HashSet<Statement> retrieveList(HashSet<Statement> statements,
			 Statement s) {
		statements=removePrimaryTypes(statements,s);
		statements.add(s);
		statements.add(s.getSubject().getProperty(RDF.type));
		Resource list = s.getSubject();
		StmtIterator st = model.listStatements(null, model.getProperty(Namespace.LIST+"hasNext"), list);
		if (st.hasNext()) {
			statements = retrieveList(statements,st.next());
		}
		return statements;
	}
	
	private HashSet<Statement> retrieveList2(HashSet<Statement> statements,Statement s){
		statements.add(s);
		statements=removePrimaryTypes(statements,s);
		Resource list=s.getSubject();
		statements.add(list.getProperty(RDF.type));
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
		
		
		for (String s : geometryEntities) {
			Resource rr=schema.getResource(schema.getNsPrefixURI("ifc")+s.substring(4));
			String q = String.format(template, "<"+rr.getURI()+">");
			Query query = QueryFactory.create(q);
			QueryExecution qexec = QueryExecutionFactory.create(query, model);
			ResultSet results = qexec.execSelect();
			while (results.hasNext()) {
				QuerySolution solution = results.next();
				resources.add(solution.getResource("s"));
			}
			for (Resource r:JenaUtil.getAllSubClasses(schema.getResource(schema.getNsPrefixURI("ifc")+s.substring(4)))){
				q = String.format(template, "<"+r.getURI()+">");
				query = QueryFactory.create(q);
				qexec = QueryExecutionFactory.create(query, model);
				results = qexec.execSelect();
				while (results.hasNext()) {
					QuerySolution solution = results.next();
					resources.add(solution.getResource("s"));
				}
			};
			
		}
		return resources;
	}
	
/*	public void removeGeometryTriples() throws FileNotFoundException{
//		List<Statement> statements=new ArrayList<Statement>();
		for (Resource r:getAllGeometricResources()){
			HashSet<Statement> sts=listRelatedStatements(r);
			model.remove(sts);
		}
//		model.remove(geometryModel);
	}*/

	
	public Model getModel() {
		return model;
	}


	public void setModel(Model model) {
		this.model = model;
	}


	public Model getSchema() {
		return schema;
	}


	public void setSchema(Model schema) {
		this.schema = schema;
	}


	public Model getGeometryModel() {
		return geometryModel;
	}


	public void setGeometryModel(Model geometryModel) {
		this.geometryModel = geometryModel;
	}


	public long geometryTripleCount() throws FileNotFoundException{
		HashSet<Statement> statements=new HashSet<Statement>();
		for (Resource r:getAllGeometricResources()){
			HashSet<Statement> sts=listRelatedStatements(r);
			statements.addAll(sts);
		}
		return statements.size();
	}

}
