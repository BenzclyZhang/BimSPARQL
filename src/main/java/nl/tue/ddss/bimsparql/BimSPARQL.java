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
package nl.tue.ddss.bimsparql;


import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;

import com.hp.hpl.jena.sparql.pfunction.PropertyFunction;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionFactory;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionRegistry;
import org.topbraid.spin.system.SPINModuleRegistry;
import org.topbraid.spin.vocabulary.SP;
import org.topbraid.spin.vocabulary.SPIN;
import org.xml.sax.SAXException;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.function.Function;
import com.hp.hpl.jena.sparql.function.FunctionFactory;
import com.hp.hpl.jena.sparql.function.FunctionRegistry;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;




public class BimSPARQL {
	
	
	static HashMap<Node,Geometry> hashmap=new HashMap<Node,Geometry>();
	public static Model schema;
    final static Model rules=loadDefaultRules();
	
	public static void init(Model model,Model geometryModel) throws IOException, ClassNotFoundException, ParserConfigurationException, SAXException, URISyntaxException{
		schema=loadDefaultSchema();
//		getSimpleGeometry(geometryModel);      
        registerAll();       
	}
	

	
	private static void getSimpleGeometry(Model geometryModel) {
		Graph graph=geometryModel.getGraph();
		ExtendedIterator<Triple> iterator=graph.find(null, GEOM.hasGeometry.asNode(), null);
		while(iterator.hasNext()){
			Triple t=iterator.next();
			Node subject=t.getSubject();
			Node object=t.getObject();
			ExtendedIterator<Triple> iter=graph.find(object, GEOM.asBody.asNode(), null);
			if(iter.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					Geometry geometry=new EwktReader(s).readGeometry();
					hashmap.put(subject, geometry);
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} 
	}
	

	
	public static void registerAll(){		
	    registerAll(rules);		
	}
	
	public static void registerAll(Model rules){
		SPINModuleRegistry.get().init();
		SPINModuleRegistry.get().registerAll(rules, null);
		Resource function=rules.getResource(BimSPARQLNS.IMPL+"Function");
		Resource propertyfunction=rules.getResource(BimSPARQLNS.IMPL+"PropertyFunction");
		Property body=rules.getProperty(BimSPARQLNS.IMPL+"body");
		StmtIterator functionstmts=rules.listStatements(null,RDF.type,function);
		StmtIterator pfstmts=rules.listStatements(null,RDF.type,propertyfunction);
		while (functionstmts.hasNext()){
			Resource r=functionstmts.next().getSubject();
			String clazzName=r.getProperty(body).getObject().asLiteral().getString();
			FunctionRegistry.get().put(r.getURI(),new FunctionFactory(){
	            public Function create(String uri) {
	            	Class<?> clazz;
					try {
						clazz = Class.forName(clazzName);
		            	Constructor<?> constructor = clazz.getConstructor();
		                return (Function)constructor.newInstance();
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                       return null;
	            }
	        });
		}
		
		while (pfstmts.hasNext()){
			Resource r=pfstmts.next().getSubject();
			String clazzName=r.getProperty(body).getObject().asLiteral().getString();
			PropertyFunctionRegistry.get().put(r.getURI(),new PropertyFunctionFactory(){
	            public PropertyFunction create(String uri) {
	            	Class<?> clazz;
					try {
						clazz = Class.forName(clazzName);
		            	Constructor<?> constructor = clazz.getConstructor(HashMap.class);
		                return (PropertyFunction)constructor.newInstance(hashmap);
					} catch (ClassNotFoundException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (SecurityException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InstantiationException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                       return null;
	            }
	        });
		}
		
	}
	
	public static void unRegisterAll(){
	    unRegisterAll(rules);		
	}
	
	private static void unRegisterAll(Model rules){
		SPINModuleRegistry.get().reset();
		Resource function=rules.getResource(BimSPARQLNS.IMPL+"Function");
		Resource propertyfunction=rules.getResource(BimSPARQLNS.IMPL+"PropertyFunction");
		StmtIterator functionstmts=rules.listStatements(null,RDF.type,function);
		StmtIterator pfstmts=rules.listStatements(null,RDF.type,propertyfunction);
		StmtIterator mgstmts=rules.listStatements(null,RDF.type,SPIN.MagicProperty);
		while (functionstmts.hasNext()){
			Resource r=functionstmts.next().getSubject();
			FunctionRegistry.get().remove(r.getURI());
			
		}
		while (pfstmts.hasNext()){
			Resource r=pfstmts.next().getSubject();
			PropertyFunctionRegistry.get().remove(r.getURI());
		}
		while (mgstmts.hasNext()){
			Resource r=mgstmts.next().getSubject();
			PropertyFunctionRegistry.get().remove(r.getURI());
		}
	}


	private static Model loadDefaultSchema() throws IOException{
		Model schema=ModelFactory.createDefaultModel();
		InputStream in=BimSPARQL.class.getClassLoader().getResourceAsStream("IFC2X3_TC1.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		return schema.read(in, null,"TTL");
	}
	
	private static Model loadDefaultRules(){
		Model rules=ModelFactory.createDefaultModel();
		rules.add(SPIN.getModel());
		rules.add(SP.getModel());
		InputStream in=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/schm.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in, null,"TTL");
		InputStream in2=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/pset.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in2, null,"TTL");
		InputStream in3=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/pdt.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in3, null,"TTL");
		InputStream in4=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/qto.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in4, null,"TTL");
		InputStream in5=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/geom.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in5, null,"TTL");
		InputStream in6=BimSPARQL.class.getClassLoader().getResourceAsStream("bimsparql/spt.ttl"); //new FileInputStream("IFC2X3_Schema.rdf");
		rules.read(in6, null,"TTL");
		return rules;
	}
	
	public static String getOSVersion(){
		String os=System.getProperty("os.name");
		return os;
	}
	
	

}
