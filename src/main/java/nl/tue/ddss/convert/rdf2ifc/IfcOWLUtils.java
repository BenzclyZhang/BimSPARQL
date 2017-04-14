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


import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;



public class IfcOWLUtils extends ModelUtils {

	public static Resource getClass(Resource ifcObject, Model schema) {
		Resource clazz = ifcObject.getPropertyResourceValue(RDF.type);
		return clazz;
	}

	public static Model changeNS(Model schema, String oldNS, String newNS) throws IOException {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		schema.write(out);
		ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
		BufferedReader reader = new BufferedReader(new InputStreamReader(in)) {
			@Override
			public String readLine() throws IOException {
				String lBuf = super.readLine();
				lBuf.replace(oldNS, newNS);
				return lBuf;
			}
		};
		Model changedSchema = ModelFactory.createDefaultModel();
		changedSchema.read(reader, null);
		return changedSchema;
	}

	public static boolean isDirectSubClassOf(Resource subclass, Resource superclass, Model schema) {
		return schema.contains(subclass, RDFS.subClassOf, superclass);
	}

	public static int getPropertyPosition(Property p, Model schema) {
		Resource clazz = schema.listStatements(p, RDFS.domain, (RDFNode) null).next().getObject().asResource();
		Property index = schema.getProperty(IfcOWLSupplement.getURI() + "index");
		int i = p.listProperties(index).next().getInt();
		Property isTopLevelIfcEntity = schema.getProperty(IfcOWLSupplement.getURI() + "isTopLevelIfcEntity");
		while (clazz.getProperty(isTopLevelIfcEntity) == null) {
			clazz = getIfcSuperClass(clazz, schema);
			i = i + getPropList(schema, clazz, index).size();
		}
		return i;
	}

	public static HashMap<Resource, List<Property>> getProperties4Class(Model schema) {
		HashMap<Resource, List<Property>> hashMap = new HashMap<Resource, List<Property>>();
		List<Resource> classes = new ArrayList<Resource>();
		Property index = schema.getProperty(IfcOWLSupplement.getURI() + "index");
		Property isIfcEntity = schema.getProperty(IfcOWLSupplement.getURI() + "isIfcEntity");
		Property isTopLevelIfcEntity = schema.getProperty(IfcOWLSupplement.getURI() + "isTopLevelIfcEntity");
		StmtIterator stmt = schema.listLiteralStatements(null, isIfcEntity, true);
		while (stmt.hasNext()) {
			classes.add(stmt.next().getSubject());
		}
		for (Resource clazz : classes) {
			if (clazz.hasLiteral(isTopLevelIfcEntity, true)) {
				List<Property> properties = getPropList(schema, clazz, index);
				hashMap.put(clazz, properties);
				List<Resource> subClasses = getSubClasses(clazz, schema, RDFS.subClassOf);
				if (subClasses.size() > 0) {
					for (Resource subClass : subClasses) {
						recurGetProperties(hashMap, subClass, schema, index, RDFS.subClassOf, properties);
					}
				}
			}

		}
		return hashMap;
	}

	private static void recurGetProperties(HashMap<Resource, List<Property>> hashMap, Resource clazz, Model schema,
			Property index, Property ifcSubClassOf, List<Property> inherited) {
		List<Property> properties = new ArrayList<Property>();
		properties.addAll(inherited);
		properties.addAll(getPropList(schema, clazz, index));
		hashMap.put(clazz, properties);
		List<Resource> subClasses = getSubClasses(clazz, schema, ifcSubClassOf);
		if (subClasses.size() > 0) {
			for (Resource subClass : subClasses) {
				recurGetProperties(hashMap, subClass, schema, index, ifcSubClassOf, properties);
			}
		}
	}

	private static List<Property> getPropList(Model schema, Resource clazz, Property index) {
		List<Property> properties = new ArrayList<Property>();
		StmtIterator propIter = schema.listStatements(null, RDFS.domain, clazz);
		while (propIter.hasNext()) {
			Resource p = propIter.next().getSubject();
			if (schema.contains(p, index)) {
				properties.add(schema.getProperty(p.getURI()));
			}
		}
		properties = reArrangePropList(properties, index);
		return properties;
	}

	private static List<Property> reArrangePropList(List<Property> props, Property index) {
		List<Property> properties = new ArrayList<Property>();
		Property[] array = new Property[props.size()];
		for (Property prop : props) {
			int i = prop.listProperties(index).next().getInt();
			array[i] = prop;
		}
		for (Property p : array) {
			properties.add(p);
		}
		return properties;
	}

	private static List<Resource> getSubClasses(Resource superClass, Model model, Property ifcSubClassOf) {
		List<Resource> classes = new ArrayList<Resource>();
		StmtIterator stmt = model.listStatements(null, ifcSubClassOf, superClass);
		while (stmt.hasNext()) {
			classes.add(stmt.next().getSubject());
		}
		return classes;

	}
/**
 * Get the direct IFC super class resource for a sub class resource (e.g. IfcWall to IfcWallStandardCase).
 * @param subClass Sub class resource
 * @param schema Schema model (including the standard ifcOWL and ifcOWL schema supplyment)
 * @return super class as resource
 */
	private static Resource getIfcSuperClass(Resource subClass, Model schema) {
		Property isIfcEntity = schema.getProperty(IfcOWLSupplement.getURI() + "isIfcEntity");
		StmtIterator stmt = schema.listStatements(subClass, RDFS.subClassOf, (RDFNode) null);
		while (stmt.hasNext()) {
			Resource clazz = stmt.next().getResource();
			if (clazz.getProperty(isIfcEntity) != null) {
				return clazz;
			}
		}
		return null;
	}

}
