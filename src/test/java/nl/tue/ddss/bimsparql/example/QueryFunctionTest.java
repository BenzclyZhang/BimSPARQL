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
package nl.tue.ddss.bimsparql.example;

import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.hp.hpl.jena.ontology.OntModel;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;

import nl.tue.ddss.bimsparql.BimSPARQL;
import nl.tue.ddss.bimsparql.BimSPARQLNS;
import nl.tue.ddss.bimsparql.geometry.GeometryGenerator;
import nl.tue.ddss.convert.Namespace;

public class QueryFunctionTest {
	
	private static final String prefixes="PREFIX ifcowl: <"+Namespace.IFC2X3_TC1+">\n"+"PREFIX list: <"+Namespace.LIST+">\n"+"PREFIX expr: <"+Namespace.EXPRESS+">\n"
			+ "PREFIX schm:<"+BimSPARQLNS.SCHM+">\n" + "PREFIX pset:<"+BimSPARQLNS.PSET+">\n"+ "PREFIX spt:<"+BimSPARQLNS.SPT+">\n"+"PREFIX pdt:<"+BimSPARQLNS.PDT+">\n"+"PREFIX qto:<"+BimSPARQLNS.QTO+">\n";
	
	public static OntModel loadDefaultModel(){
		InputStream in = BodyGeometryTest.class.getClassLoader()
				.getResourceAsStream("Duplex_A_20110505.ttl");
		Model model=ModelFactory.createDefaultModel();
		model.read(in,null,"TTL");
		String baseuri=model.getNsPrefixURI("inst");
		InputStream ins = BodyGeometryTest.class.getClassLoader()
				.getResourceAsStream("IFC2X3_TC1.ttl");
		InputStream input = BodyGeometryTest.class.getClassLoader()
				.getResourceAsStream("Duplex_A_20110505.ifc");
//		ColladaParser parser = new ColladaParser();
		GeometryGenerator gg=new GeometryGenerator();
		gg.generateGeometry(input,baseuri);
		Model geometryModel=gg.getGeometryModel();
		Model schema=ModelFactory.createDefaultModel();
		schema.read(ins,null,"TTL");	

			try {
				BimSPARQL.init(model,geometryModel);
			} catch (ClassNotFoundException | IOException | ParserConfigurationException | SAXException
					| URISyntaxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		OntModel ontology=ModelFactory.createOntologyModel();
		ontology.add(schema);
		ontology.add(model);
		ontology.add(geometryModel);
		return ontology;
	}
	
	public static void executeQuery(String query){
		System.out.println("Load model...");
		OntModel ontModel=loadDefaultModel();
		System.out.println("Start to query...");
		Query q = QueryFactory.create(prefixes+query);
		QueryExecution qe = QueryExecutionFactory.create(q, ontModel);
		ResultSet qresults = qe.execSelect();
		ResultSetFormatter.out(qresults);
	}

}
