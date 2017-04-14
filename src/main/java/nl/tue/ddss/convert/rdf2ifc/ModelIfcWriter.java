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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;


import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

import org.apache.commons.io.Charsets;
import org.apache.jena.riot.Lang;
import org.topbraid.spin.util.JenaUtil;

import nl.tue.ddss.convert.IfcHeader;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.IfcVersionException;
import nl.tue.ddss.convert.Namespace;

public class ModelIfcWriter extends IfcWriter{




	private Model model;
    

	public ModelIfcWriter(Model model) throws FileNotFoundException, IfcVersionException {
		if (ifcVersion!=null){
			
		}
//		writeCounter = 0;
		this.model = model;
		this.schema = ModelFactory.createDefaultModel();
		
		InputStream listSchema = getClass().getResourceAsStream("list.ttl");
  		schema.read(listSchema, null, "TTL");
  		InputStream expSchema = getClass().getResourceAsStream("express.ttl");
  		schema.read(expSchema, null, "TTL");


		String ontoUri=model.getNsPrefixURI("ifcowl");
		if (ontoUri==null) ontoUri=model.getNsPrefixURI("ifc");
		if(ontoUri==null) {
		try{
		ontoUri=model.listObjectsOfProperty(OWL.imports).next().asResource().getURI();
		}catch(NullPointerException e){
			throw new IfcVersionException("Cannot determine IFC version");
		}
		}
		
       if(ontoUri.equals(Namespace.IFC2X3_TC1)){
   		InputStream in = getClass().getResourceAsStream("resource/IFC2X3_TC1.ttl");
   		schema.read(in, null, "TTL");
   		InputStream in2 = getClass().getResourceAsStream("resource/IFC2X3_TC1_Schema_supplement.ttl");
   		schema.read(in2, null, "TTL");
       }
       else if(ontoUri.equals(Namespace.IFC4_ADD1)){
      		InputStream in = getClass().getResourceAsStream("resource/IFC4_ADD1.ttl");
       		schema.read(in, null, "TTL");
       		InputStream in2 = getClass().getResourceAsStream("resource/IFC4_ADD1_Schema_supplement.ttl");
       		schema.read(in2, null, "TTL");
       }else if(ontoUri.equals(Namespace.IFC4_ADD2)){
      		InputStream in = getClass().getResourceAsStream("resource/IFC4_ADD2.ttl");
       		schema.read(in, null, "TTL");
       		InputStream in2 = getClass().getResourceAsStream("resource/IFC4_ADD2_Schema_supplement.ttl");
       		schema.read(in2, null, "TTL");
       }else if(ontoUri.equals(Namespace.IFC4)){
      		InputStream in = getClass().getResourceAsStream("resource/IFC4.ttl");
       		schema.read(in, null, "TTL");
       		InputStream in2 = getClass().getResourceAsStream("resource/IF4_Schema_supplement.ttl");
       		schema.read(in2, null, "TTL");
       }else if(ontoUri.equals(Namespace.IFC2X3_FINAL)){
      		InputStream in = getClass().getResourceAsStream("resource/IFC2X3_FINAL.ttl");
       		schema.read(in, null, "TTL");
       		InputStream in2 = getClass().getResourceAsStream("resource/IFC2X3_FINAL_Schema_supplement.ttl");
       		schema.read(in2, null, "TTL");
       }else{
    	   throw new IfcVersionException("Unrecognized ifc ontology version");
       }
		model.add(schema);
		class2Properties = IfcOWLUtils.getProperties4Class(schema);

	}



	public ModelIfcWriter(IfcVersion version) {
		super(version);
	}



	public void convert(String inputFile, String outputFile,Lang lang,String logFilePath) throws IOException{

				try {
					Model model = ModelFactory.createDefaultModel();
					System.out.println("Start to convert... " + inputFile);
					long start = System.currentTimeMillis();
					InputStream in = new FileInputStream(inputFile);
					model.read(in, null, lang.getLabel());
					ModelIfcWriter iss = new ModelIfcWriter(model);
					OutputStream out = new FileOutputStream(outputFile);
					iss.write(out);
					long end = System.currentTimeMillis();
					System.out.println("Conversion time: " + ((end - start)) / 1000 + " s");
					System.out.println("Done!");
					out.close();
				} catch (StackOverflowError e) {
					System.out.println("Fail");
					System.out.println(e.getCause());
				}  catch (IfcVersionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

	}

	public boolean write(OutputStream outputStream) throws IOException {

			this.outputStream = outputStream;
			writeHeader();
			String query = "prefix xsd: <http://www.w3.org/2001/XMLSchema#>\n" + "prefix supply:<"
					+ IfcOWLSupplement.getURI() + ">\n" + "select distinct ?instance where {\n"
					+ "   ?instance a  ?class .\n" + "?class supply:isIfcEntity \"true\"^^xsd:boolean .\n" + "}";
			ResultSet results = QueryExecutionFactory.create(query, model).execSelect();
            
			
			while (results.hasNext()) {
				write(results.next().getResource("instance"));
//				writeCounter++;
			}
			writeFooter();
			return true;
		
	}

	private void writeFooter() throws IOException {
		println("ENDSEC;");
		println("END-ISO-10303-21;");
	}

	private void writeHeader() throws IOException {
		println("ISO-10303-21;");
		println("HEADER;");
		Resource base=model.getResource(model.getNsPrefixURI("inst"));
		
		String description=parseListStringValueForHeader(base, IfcHeader.description);
		String implementation_level=parseStringValueForHeader(base, IfcHeader.implementation_level);
		String name=parseStringValueForHeader(base, IfcHeader.name);
		String time_stamp=parseStringValueForHeader(base, IfcHeader.time_stamp);
		String author=parseListStringValueForHeader(base, IfcHeader.author);
		String organization=parseListStringValueForHeader(base, IfcHeader.organization);
		String preprocessor_version=parseStringValueForHeader(base, IfcHeader.preprocessor_version);
		String originating_system=parseStringValueForHeader(base, IfcHeader.originating_system);
		String authorization=parseStringValueForHeader(base, IfcHeader.authorization);
		String schema_identifiers=parseListStringValueForHeader(base, IfcHeader.schema_identifiers);
		
		println("FILE_DESCRIPTION ("+description+", "+implementation_level+");");
		println("FILE_NAME ("+name+", "+time_stamp+", " + author+", "+organization+", "+preprocessor_version+", "+originating_system+", "+authorization+");");
		println("FILE_SCHEMA ("+schema_identifiers+");");
		println("ENDSEC;");
		println("DATA;");
		// println("//This program comes with ABSOLUTELY NO WARRANTY.");
		// println("//This is free software, and you are welcome to redistribute
		// it under certain conditions. See www.bimserver.org
		// <http://www.bimserver.org>");
	}
	
	private String parseListStringValueForHeader(Resource base,Property p){
		String result;
		Statement stat=base.getProperty(p);
		if(stat==null){
			result="$";
		}else{
			
			String[] sl=stat.getObject().asLiteral().getString().split("\\s*,\\s*");
			result="(";
			for(int i=0;i<sl.length;i++){
				if(i<sl.length-1){
				result=result+"'"+sl[i]+"', ";
				}
				else{
					result=result+"'"+sl[i]+"'";
				}
			}
			result=result+")";
		}
		return result;
	}
	
	private String parseStringValueForHeader(Resource base, Property p){
		String result;
		Statement stat=base.getProperty(p);
		if(stat==null){
			result="$";
		}else{
			result="'"+stat.getObject().asLiteral().getString()+"'";
		}
		return result;
	}

	private void println(String line) throws IOException {
		byte[] bytes = line.getBytes(Charsets.UTF_8);
		outputStream.write(bytes, 0, bytes.length);
		outputStream.write(NEW_LINE, 0, NEW_LINE.length);
	}

	private void print(String text) throws IOException {
		byte[] bytes = text.getBytes(Charsets.UTF_8);
		outputStream.write(bytes, 0, bytes.length);
	}

	private void write(Resource object) throws IOException {
		Resource ontClass = IfcOWLUtils.getClass(object, model);
		print(DASH);
		int convertedKey = getExpressId(object);
		print(String.valueOf(convertedKey));
		print("= ");
		String upperCase = ontClass.getLocalName().toUpperCase();
		print(upperCase);
		print(OPEN_PAREN);
		boolean isFirst = true;
		for (Property property : class2Properties.get(ontClass)) {
			List<Statement> stmts = model.listStatements(object, property, (RDFNode) null).toList();
			Resource range = property.getPropertyResourceValue(RDFS.range);
			if (stmts.size() == 0) {
				if (!isFirst) {
					print(COMMA);
				}
				writeNull(object, property);
			} else if (stmts.size() > 1) {
				if (!isFirst) {
					print(COMMA);
				}
				writeSet(stmts, property, range);
			} else {
				if (!isFirst) {
					print(COMMA);
				}
				Resource o = stmts.get(0).getObject().asResource();
				if (isOwlList(o)) {
					writeList(o, property, range);
				} else if (isMany(property)) {
					writeSet(stmts, property, range);
				} else if (isIfcInstance(o)) {
					writeIfcInstance(o);
				} else if (isEnumeration(o)) {
					writeEnum(o);
				} else {
					Resource clazz = o.getPropertyResourceValue(RDF.type);

					if (clazz.equals(range)) {
						writePrimitive(o);
					} else {

						// if (JenaUtil.hasSuperClass(clazz, range)&&
						if (IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema))
							writeEmbedded(clazz, o);
						// else
						// if(clazz.getURI().startsWith("http://purl.org/voc/express#")&&!IfcRDFUtils.isSubClassOf(range,
						// clazz, schema)){
						// writeEmbedded(clazz,o);
						// }
						else
							writePrimitive(o);
					}
				}
			}
			isFirst = false;
		}
		println(PAREN_CLOSE_SEMICOLON);
	}

	private void writePrimitive(Resource val) throws IOException{
		if (isLogical(val)) {
			if (val.hasProperty(HASLOGICAL, EXPRESS_TRUE)) {
				print(BOOLEAN_TRUE);
			} else if (val.hasProperty(HASLOGICAL, EXPRESS_FALSE)) {
				print(BOOLEAN_FALSE);
			} else if (val.hasProperty(HASLOGICAL, EXPRESS_UNKNOWN)) {
				print(BOOLEAN_UNKNOWN);
			}
		} else if (isReal(val) || isNumber(val)) {
			Double valDouble;
			if (val.getProperty(HASDOUBLE) != null) {
				valDouble = val.getProperty(HASDOUBLE).getObject().asLiteral().getDouble();
			} else {
				valDouble = val.getProperty(model.getProperty(Namespace.EXPRESS + "hasDOUBLE")).getObject().asLiteral()
						.getDouble();
			}
			if ((valDouble).isInfinite() || ((valDouble).isNaN())) {
				print("0.0");
			} else {
				String string = valDouble.toString();
				if (string.endsWith(DOT_0)) {
					print(string.substring(0, string.length() - 1));
				} else {
					print(string);
				}
			}
		} else if (isInteger(val)) {
			Integer valInteger = val.getProperty(HASINTEGER).getObject().asLiteral().getInt();
			String string = valInteger.toString();
			if (string.endsWith(DOT_0)) {
				print(string.substring(0, string.length() - 2));
			} else {
				print(string);
			}
		} else if (isBinary(val)) {
			String binary = val.getProperty(HASHEXBINARY).getObject().asLiteral().getString();
			print(binary);
		} else if (isBoolean(val)) {
			if (val.hasLiteral(HASBOOLEAN, true)) {
				print(BOOLEAN_TRUE);
			} else if (val.hasLiteral(HASBOOLEAN, false)) {
				print(BOOLEAN_FALSE);
			}
		} else if (isString(val)) {
			print(SINGLE_QUOTE);
			String stringVal = val.getProperty(HASSTRING).getObject().asLiteral().getString();
			print(stringVal);			
			print(SINGLE_QUOTE);
		} else if (isEnumeration(val)) {
			String enumVal = val.getLocalName();
			print("." + enumVal + ".");
		} else {
			print(val == null ? "$" : val.toString());
		}
	}

	private void writeNull(Resource object, Property property) throws IOException {
		if (isMany(property)&&!isOptional(property)) {
			print(OPEN_CLOSE_PAREN);
		} else if (isDerived(object, property)) {
			print(ASTERISK);
		} else
			print(DOLLAR);
	}

	private void writeSet(List<Statement> stmts, Property property, Resource range)
			throws IOException{
		boolean isFirst = true;
		print(OPEN_PAREN);
		for (Statement stmt : stmts) {
			Resource resource = stmt.getObject().asResource();
			if (!isFirst) {
				print(COMMA);
			}
			if (isIfcInstance(resource)) {
				writeIfcInstance(resource);
			} else if (isEnumeration(resource)) {
				writeEnum(resource);
			} else if (isOwlList(resource)) {
				writeList(resource, property, range);
			} else {
				Resource clazz = resource.getPropertyResourceValue(RDF.type);
				if (clazz.equals(range)) {
					writePrimitive(resource);
				} else {
					// if (JenaUtil.hasSuperClass(clazz, range) &&
					if (IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema))
						writeEmbedded(clazz, resource);
					// else
					// if(clazz.getURI().startsWith("http://purl.org/voc/express#")&&!IfcRDFUtils.isSubClassOf(range,
					// clazz, schema)){
					// writeEmbedded(clazz,resource);
					// }
					else
						writePrimitive(resource);
				}
			}
			isFirst = false;
		}
		print(CLOSE_PAREN);
	}

	private boolean isDerived(Resource object, Property property) {
		if (object.getPropertyResourceValue(RDF.type).hasProperty(HASDERIVEATTRIBUTE, property)) {
			return true;
		}
		return false;
	}

	private void writeEmbedded(Resource clazz, Resource object) throws IOException {
		// if(clazz.getURI().startsWith("http://purl.org/voc/express#")){
		// print("IFC"+clazz.getLocalName().toUpperCase());
		// }
		// else{
		print(clazz.getLocalName().toUpperCase());
		// }
		print(OPEN_PAREN);
		writePrimitive(object);
		print(CLOSE_PAREN);
	}

	private void writeList(Resource object, Property property, Resource range) throws IOException {
		LinkedList<Resource> list = new LinkedList<Resource>();

		list = parseList(list, object);
		if (list.size() == 0) {
			print(OPEN_CLOSE_PAREN);
		} else {
			boolean isFirst = true;
			if (JenaUtil.hasSuperClass(object.getPropertyResourceValue(RDF.type), range)
					&& IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema)) {
				print(object.getPropertyResourceValue(RDF.type).getLocalName().toUpperCase());
			}
			print(OPEN_PAREN);
			if (range.getURI().endsWith("_List")) {
				range = schema.getResource(range.getURI().substring(0, range.getURI().length() - 5));
			}
			for (Resource resource : list) {
				if (!isFirst) {
					print(COMMA);
				}
				if (isIfcInstance(resource)) {
					writeIfcInstance(resource);
				} else if (isEnumeration(resource)) {
					writeEnum(resource);
				} else if (isOwlList(resource)) {
					writeList(resource, property, range);
				} else {
					Resource clazz = resource.getPropertyResourceValue(RDF.type);

					if (clazz.equals(range)) {
						writePrimitive(resource);
					} else {
						// if (JenaUtil.hasSuperClass(clazz, range)
						if (IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema))
							writeEmbedded(clazz, resource);
						// else
						// if(clazz.getURI().startsWith("http://purl.org/voc/express#")&&!IfcRDFUtils.isSubClassOf(range,
						// clazz, schema)){
						// writeEmbedded(clazz,resource);
						// }
						else
							writePrimitive(resource);
					}

				}
				isFirst = false;
			}
			print(CLOSE_PAREN);
		}
	}

	private int getExpressId(Resource ifcResource) {
		return Integer.parseInt(ifcResource.getLocalName().substring(ifcResource.getLocalName().indexOf("_") + 1));
	}

	private LinkedList<Resource> parseList(LinkedList<Resource> list, Resource root) {
		while (root.hasProperty(HASNEXT)) {
			list.add(root.getPropertyResourceValue(HASCONTENTS));
			root = root.getPropertyResourceValue(HASNEXT);
		}
		list.add(root.getPropertyResourceValue(HASCONTENTS));
		return list;
	}

	private boolean isEnumeration(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(ENUMERATION) || JenaUtil.hasSuperClass(clazz, ENUMERATION)) {
			return true;
		} else
			return false;
	}

	private boolean isIfcInstance(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.hasLiteral(ISIFCENTITY, true)) {
			return true;
		} else
			return false;
	}

	private boolean isReal(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(REAL) || JenaUtil.hasSuperClass(clazz, REAL)) {
			return true;
		} else
			return false;
	}

	private boolean isInteger(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(INTEGER) || JenaUtil.hasSuperClass(clazz, INTEGER)) {
			return true;
		} else
			return false;
	}

	private boolean isNumber(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(NUMBER) || JenaUtil.hasSuperClass(clazz, NUMBER)) {
			return true;
		} else
			return false;
	}

	private boolean isBoolean(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(BOOLEAN) || JenaUtil.hasSuperClass(clazz, BOOLEAN)) {
			return true;
		} else
			return false;
	}

	private boolean isBinary(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(BINARY) || JenaUtil.hasSuperClass(clazz, BINARY)) {
			return true;
		} else
			return false;
	}

	private boolean isLogical(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(LOGICAL) || JenaUtil.hasSuperClass(clazz, LOGICAL)) {
			return true;
		} else
			return false;
	}

	private boolean isString(Resource resource) {
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (clazz.equals(STRING) || JenaUtil.hasSuperClass(clazz, STRING)) {
			return true;
		} else
			return false;
	}

	private boolean isOwlList(Resource resource) {
		boolean result = false;
		Resource clazz = resource.getPropertyResourceValue(RDF.type);
		if (resource.hasProperty(HASCONTENTS)) {
			result = true;
			if (!(clazz.equals(OWLLIST) || JenaUtil.hasSuperClass(clazz, OWLLIST))) {
				System.out.println(resource.getLocalName() + ": " + "The class " + clazz.getLocalName()
						+ " has been cast to a list");
			}
		}

		return result;
	}

	private boolean isMany(Property property) {
		if (property.hasLiteral(ISLISTORARRAY, true)||property.hasLiteral(ISSET, true)) {
			return true;
		} else
			return false;
	}
	
	private boolean isOptional(Property property) {
		if (property.hasLiteral(ISOPTIONAL, true)) {
			return true;
		} else
			return false;
	}

	private void writeIfcInstance(Resource object) throws IOException {
		print(DASH);
		print(Integer.toString(getExpressId(object)));
	}

	private void writeEnum(Resource val) throws IOException {
		if (val.getLocalName().equals(NULL)) {
			print(DOLLAR);
		} else {
			print(DOT);
			print(val.getLocalName());
			print(DOT);
		}
	}




}
