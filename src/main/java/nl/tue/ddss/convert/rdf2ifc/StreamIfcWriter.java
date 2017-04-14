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

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;

import org.apache.commons.io.Charsets;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.OWL;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import org.topbraid.spin.util.JenaUtil;

import nl.tue.ddss.convert.IfcHeader;
import nl.tue.ddss.convert.IfcObject;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.IfcVersionException;

/* The class used to read IFC RDF files and write IFC SPF files.
 * This class uses PipedRDFStream in Jena Riot to read IFC RDF files and 
 * orders elements in IFC SPF files according to triple orders in IFC RDF files.
 */
/**
 * @author Chi
 *
 */
public class StreamIfcWriter extends IfcWriter {

	private final static Logger LOGGER = Logger.getLogger(StreamIfcWriter.class.getName());
	private String ontologyURI;
   
	private HashMap<String, Resource> entityMap = new HashMap<String, Resource>();
	private HashMap<String, Resource> enumMap = new HashMap<String, Resource>();
	private HashMap<String, Resource> listMap = new HashMap<String, Resource>();
	private HashMap<String, Object> valueMap = new HashMap<String, Object>();
	private HashMap<String, String> listNextMap = new HashMap<String, String>();
	private HashMap<String, String> typeMap = new HashMap<String, String>();
	private LinkedHashMap<String, IfcObject> ifcMap = new LinkedHashMap<String, IfcObject>();

	private Integer maxLineNum = 0;

	public StreamIfcWriter(IfcVersion version) {
		super(version);
	}

	public StreamIfcWriter() {
		super();
	}
	
	
    
	public StreamIfcWriter(IfcVersion version, boolean updns) {
		super(version,updns);
	}

	/**
	 * convert a IFC RDF input stream to IFC STEP output stream.
	 * 
	 * @param inputUri
	 * @param outputStream
	 * @param lang
	 *            used RDF language.
	 * @throws IOException
	 * @throws IfcVersionException
	 */
	public void convert(String inputUri, String outputFile, Lang lang, String logFilePath) throws IOException {
	       for(Handler handler: LOGGER.getHandlers()){
	    	   LOGGER.removeHandler(handler);
	       }
	        LOGGER.setLevel(Level.ALL);
//	       LOGGER.setUseParentHandlers(false);
		if (logFilePath!=null){
		        FileHandler fh = new FileHandler(logFilePath);

		       fh.setLevel(Level.ALL);
		        LOGGER.addHandler(fh);

		        SimpleFormatter formatter = new SimpleFormatter();
		        fh.setFormatter(formatter);
        }
		this.outputStream = new FileOutputStream(outputFile);
		try {
			write("Start to convert...");
			write("Loading schema...");
			InputStream listSchema = getClass().getClassLoader().getResourceAsStream("list.ttl");
			schema.read(listSchema, null, "TTL");
			InputStream expSchema = getClass().getClassLoader().getResourceAsStream("express.ttl");
			schema.read(expSchema, null, "TTL");
			if (ifcVersion == null) {
				ifcVersion = selectSchema(inputUri, lang);		
			}else {
				if(ifcVersion!=selectSchema(inputUri, lang)){
					LOGGER.log(Level.WARNING, "used ifcOWL ontology might not be consistent with the data");
				}
			}
			InputStream in = getClass().getClassLoader().getResourceAsStream(ifcVersion.getLabel() + ".ttl");
			schema.read(in, null, "TTL");
			InputStream in2 = getClass().getClassLoader()
					.getResourceAsStream(ifcVersion.getLabel() + "_Schema_supplement.ttl");
			schema.read(in2, null, "TTL");	
			
			this.ontologyURI = IfcVersion.IfcNSMap.get(ifcVersion);
			processSchema();
			write("Loading model...");
			try{
			read(inputUri, lang);
			} catch (NoSuchElementException e){
				throw new IfcVersionException ("used ifcOWL ontology does not match the data");
			}
			write("Loading finished!");
			// reArrangeListValues();
			write("Start to write...");
			writeHeader();
			writeData();
			write("Finished!");
			outputStream.close();
			ifcMap.clear();
			valueMap.clear();
			listNextMap.clear();
			listMap.clear();
			entityMap.clear();
			enumMap.clear();
		} catch (IfcVersionException e) {
			LOGGER.log(Level.SEVERE,"Caught IfcVersionException"+e.getMessage());
			write("Converter quitted!!");
			e.printStackTrace();
		}
	}

	/**
	 * Determine IFC schema version (ifcOWL ontology version) based on input
	 * content. It is dertermined by the ontology imported (owl:imports) by the
	 * base uri.
	 * 
	 * @param input
	 * @param lang
	 * @return schema version
	 * @throws IfcOWLVersionException
	 * 
	 */
	public IfcVersion selectSchema(String input, Lang lang) throws IfcVersionException {
		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> stream = new PipedTriplesStream(iter);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Node ontology = null;
		Runnable parser = new Runnable() {
			public void run() {
				RDFDataMgr.parse(stream, input);
			}
		};
		executor.submit(parser);

		while (iter.hasNext()) {
			Triple t = iter.next();
			Node predicate = t.getPredicate();

			if (predicate.equals(OWL.imports.asNode())) {
				ontology = t.getObject();
				// break;
			}
		}
		executor.shutdown();
		if (ontology == null) {
			throw new IfcVersionException("The ifcOWL model did not import used ifcOWL ontology");
		}
		InputStream listSchema = getClass().getClassLoader().getResourceAsStream("list.ttl");
		schema.read(listSchema, null, "TTL");
		InputStream expSchema = getClass().getClassLoader().getResourceAsStream("express.ttl");
		schema.read(expSchema, null, "TTL");
		IfcVersion ifcVersion=IfcVersion.NSIfcMap.get(ontology.getURI());
		if(ifcVersion==null) throw new IfcVersionException("Cannot determine required IFC version");
		return IfcVersion.NSIfcMap.get(ontology.getURI());
	}

	/**
	 * Preprocess the schema, map entities, properties, enumerations and lists.
	 */
	private void processSchema() {
		class2Properties = IfcOWLUtils.getProperties4Class(schema);
		ExtendedIterator<Statement> stats = schema.listLiteralStatements(null, ISIFCENTITY, true);
		while (stats.hasNext()) {
			Statement s = stats.next();
			entityMap.put(s.getSubject().getLocalName(), s.getSubject());
		}
		stats = schema.listStatements(null, RDF.type, (RDFNode) null);
		while (stats.hasNext()) {
			Statement stat = stats.next();
			if (stat.getObject().equals(ENUMERATION)
					|| JenaUtil.hasSuperClass(stat.getObject().asResource(), ENUMERATION)) {
				enumMap.put(stat.getSubject().getLocalName(), stat.getSubject());
			}
		}

		listMap.put(OWLLIST.getLocalName(), OWLLIST);
		Set<Resource> lists = JenaUtil.getAllSubClasses(OWLLIST);
		for (Resource s : lists) {
			listMap.put(s.getLocalName(), s);
		}
	}

	/**
	 * Read an IFC RDF model by triples and map entries.
	 * 
	 * @param input
	 * @param lang
	 * 
	 * 
	 */
	public void read(String input, Lang lang) {
		PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
		final PipedRDFStream<Triple> stream = new PipedTriplesStream(iter);
		ExecutorService executor = Executors.newSingleThreadExecutor();
		Runnable parser = new Runnable() {
			public void run() {
				RDFDataMgr.parse(stream, input);
			}
		};
		executor.submit(parser);
		while (iter.hasNext()) {
			Triple t = iter.next();
			Node subject = t.getSubject();
			Node predicate = t.getPredicate();
			Node object = t.getObject();
			String sn = subject.getLocalName();
			String pn = predicate.getLocalName();
			if (predicate.equals(RDF.type.asNode())) {
				String on = object.getLocalName();
				typeMap.put(sn, on);
				if (isIfcInstance(sn)) {
					IfcObject ifcvo = ifcMap.get(subject.getLocalName());
					if (ifcvo == null) {
						ifcvo = new IfcObject();
						ifcMap.put(sn, ifcvo);
						ifcvo.setList(new HashMap<Integer, Object>());
					}
					ifcvo.setName(on);
					try {
						Integer i = Integer.parseInt(sn.substring(sn.lastIndexOf("_") + 1));
						ifcvo.setLineNum(i);
						maxLineNum = Math.max(maxLineNum, i);
					} catch (NumberFormatException e) {
						continue;
					}
				}
			} else if (pn.equals("hasExpressID")) {
				IfcObject ifcvo = ifcMap.get(subject.getLocalName());
				if (ifcvo == null) {
					ifcvo = new IfcObject();
					ifcMap.put(sn, ifcvo);
					ifcMap.put(sn, ifcvo);
					ifcvo.setList(new HashMap<Integer, Object>());
					ifcvo.setList(new HashMap<Integer, Object>());
				}
				try{
				Integer lineNum = (Integer) object.getLiteralValue();
				ifcvo.setLineNum(lineNum);
				maxLineNum = Math.max(lineNum, maxLineNum);
				}catch (Exception e){
							LOGGER.log(Level.WARNING, "Found an invalid express id with resource: "+subject.getURI());
							continue;
						}
			} else if (pn.equals("hasNext")) {
				listNextMap.put(subject.getLocalName(), object.getLocalName());
			} else if (pn.equals("hasContents")) {
				LinkedList<String> list = new LinkedList<String>();
				list.add(object.getLocalName());
				valueMap.put(subject.getLocalName(), list);
			} else if (pn.equalsIgnoreCase("hasDouble")) {
				Double valDouble=null;
				try{
				 valDouble= (Double) object.getLiteralValue();
				}catch (Exception e){
					LOGGER.log(Level.WARNING, "Found an invalid double value: "+subject.getURI());
					continue;
				}
				String str = "0.0";
				if (valDouble.isInfinite() || (valDouble.isNaN())) {
					LOGGER.log(Level.INFO, "Serialize infinite number as 0.0");
				} else {
					str = object.getLiteral().toString();
					str = str.substring(0, str.indexOf('^'));
				}
				valueMap.put(subject.getLocalName(), str);
			} else if (pn.equalsIgnoreCase("hasString")) {
				String valString=null;
				try{
					 valString= (String) object.getLiteralValue();
					}catch (Exception e){
						LOGGER.log(Level.WARNING, "Found an invalid string value: "+subject.getURI());
						continue;
					}
				valueMap.put(subject.getLocalName(), SINGLE_QUOTE + valString + SINGLE_QUOTE);
			} else if (pn.equals("hasInteger")) {
				Integer valInteger = null;
				try{
					 valInteger= (Integer) object.getLiteralValue();
					}catch (Exception e){
						LOGGER.log(Level.WARNING, "Found an invalid integer value: "+subject.getURI());
						continue;
					}
				String str = valInteger.toString();
				if (str.endsWith(DOT_0)) {
					str = str.substring(0, str.length() - 2);
				}
				valueMap.put(subject.getLocalName(), str);
			} else if (pn.equals("hasHexBinary")) {
				String str;
				try{
					str=object.getLiteralValue().toString();
				}catch (Exception e){
					LOGGER.log(Level.WARNING, "Found an invalid hexbinary value:"+subject.getURI()+", try to convert it as string");
					try {
						str = object.getLiteral().toString();
						str = str.substring(0, str.indexOf('^'));
					}catch (Exception ie){
						LOGGER.log(Level.WARNING, "Did not convert the binary value: "+subject.getURI());
						continue;
					}
				}
				valueMap.put(subject.getLocalName(), DOUBLE_QUOTE + str + DOUBLE_QUOTE);
			} else if (pn.equals("hasBoolean")) {
				if (object.equals(NodeFactory.createLiteral(Boolean.toString(true), null, XSDDatatype.XSDboolean))) {
					valueMap.put(subject.getLocalName(), BOOLEAN_TRUE);
				} else if (object
						.equals(NodeFactory.createLiteral(Boolean.toString(false), null, XSDDatatype.XSDboolean))) {
					valueMap.put(subject.getLocalName(), BOOLEAN_FALSE);
				} else {

					LOGGER.log(Level.WARNING, "Found an invalid boolean value: "+subject.getURI());
					continue;
				}
			} else if (pn.equals("hasLogical")) {
				if (object.equals(EXPRESS_TRUE.asNode())) {
					valueMap.put(subject.getLocalName(), BOOLEAN_TRUE);
				} else if (object.equals(EXPRESS_FALSE.asNode())) {
					valueMap.put(subject.getLocalName(), BOOLEAN_FALSE);
				} else if (object.equals(EXPRESS_UNKNOWN.asNode())) {
					valueMap.put(subject.getLocalName(), BOOLEAN_UNKNOWN);
				}else {
					LOGGER.log(Level.WARNING, "Found an invalid logical value: "+subject.getURI());
					continue;
				}
			} else if (sn.equals("")) {
				if (predicate.equals(IfcHeader.description.asNode())) {
					header.getDescription().add(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.author.asNode())) {
					header.getAuthor().add(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.organization.asNode())) {
					header.getOrganization().add(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.schema_identifiers.asNode())) {
					header.getSchema_identifiers().add(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.implementation_level.asNode())) {
					header.setImplementation_level(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.name.asNode())) {
					header.setName(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.time_stamp.asNode())) {
					header.setTime_stamp(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.preprocessor_version.asNode())) {
					header.setPreprocessor_version(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.originating_system.asNode())) {
					header.setOriginating_system(object.getLiteralValue().toString());
				} else if (predicate.equals(IfcHeader.authorization.asNode())) {
					header.setAuthorization(object.getLiteralValue().toString());
				}
			} else {
				IfcObject ifcvo = ifcMap.get(subject.getLocalName());
				String on = object.getLocalName();
				if (ifcvo == null) {
					ifcvo = new IfcObject();
					ifcvo.setList(new HashMap<Integer, Object>());
					ifcMap.put(subject.getLocalName(), ifcvo);
					//
					setValue(ifcvo, sn, pn, on);
				} else {
					setValue(ifcvo, sn, pn, on);
				}
			}
		}
		executor.shutdown();
	}

	/**
	 * Set value for an attribute of an IFC object based on a triple (local name
	 * of subject, predicate and object).
	 * 
	 * @param ifcobject
	 * @param sn
	 * @param pn
	 * @param on
	 */
	@SuppressWarnings("unchecked")
	private void setValue(IfcObject ifcobject, String sn, String pn, String on) {
		Property p = schema.getProperty(ontologyURI + pn);
		int i = IfcOWLUtils.getPropertyPosition(p, schema);
		Object o = ifcobject.getObjectList().get(i);
		if (o == null) {
			if (isSet(p)) {
				LinkedList<String> list = new LinkedList<String>();
				list.add(on);
				ifcobject.getObjectList().put(i, list);
			} else {
				ifcobject.getObjectList().put(i, on);
			}
		} else {
			if (o instanceof LinkedList<?>) {
				((LinkedList<String>) o).add(on);
			} else {
				LinkedList<String> list = new LinkedList<String>();
				list.add((String) o);
				list.add(on);
				ifcobject.getObjectList().put(i, list);
			}
		}
	}

	private String writeStringList(List<String> list) {
		String result = OPEN_PAREN;
		if(list==null||list.size()==0){
			return result+writeString("")+CLOSE_PAREN;
		}
		for (int i = 0; i < list.size(); i++) {
			if (i < list.size() - 1) {
				result = result + writeString(list.get(i)) + COMMA;
			} else {
				result = result + writeString(list.get(i));
			}
		}
		result = result + CLOSE_PAREN;

		return result;
	}

	private String writeString(String result) {
		if(result==null){
			return SINGLE_QUOTE + SINGLE_QUOTE;
		}
		return SINGLE_QUOTE + result + SINGLE_QUOTE;
	}

	/**
	 * write IFC HEADER.
	 * 
	 * @throws IOException
	 */
	private void writeHeader() throws IOException {
		println("ISO-10303-21;");
		println("HEADER;");

		println("FILE_DESCRIPTION" + OPEN_PAREN + writeStringList(header.getDescription()) + COMMA
				+ writeString(header.getImplementation_level()) + PAREN_CLOSE_SEMICOLON);
		println("FILE_NAME" + OPEN_PAREN + writeString(header.getName()) + COMMA + writeString(header.getTime_stamp())
				+ COMMA + writeStringList(header.getAuthor()) + COMMA + writeStringList(header.getOrganization())
				+ COMMA + writeString(header.getPreprocessor_version()) + COMMA
				+ writeString(header.getOriginating_system()) + COMMA + writeString(header.getAuthorization())
				+ PAREN_CLOSE_SEMICOLON);
		println("FILE_SCHEMA" + OPEN_PAREN + writeStringList(header.getSchema_identifiers()) + PAREN_CLOSE_SEMICOLON);
		println("ENDSEC;");
	}

	/**
	 * write IFC DATA.
	 * 
	 * @throws IOException
	 */
	public void writeData() throws IOException {
		println("DATA;");
		for (String s : ifcMap.keySet()) {
			IfcObject object = ifcMap.get(s);
			if (object.getLineNum() == null) {
				try {
					maxLineNum++;
					object.setLineNum(maxLineNum);
					LOGGER.log(Level.INFO, "The IFC object #"+maxLineNum+" does not have express id. Assigned one for it.");
				} catch (NumberFormatException e) {

				}
			}
		}
		for (String s : ifcMap.keySet()) {
			writeLine(ifcMap.get(s));
		}
		println("ENDSEC;");
		println("END-ISO-10303-21;");
	}

	/**
	 * write a line (an IFC object) in IFC DATA.
	 * @ ifcobject the ifcobject used.
	 * @throws IOException
	 */
	@SuppressWarnings("unchecked")
	public void writeLine(IfcObject ifcobject) throws IOException {
			LOGGER.fine("Write IFC object: #"+ifcobject.getLineNum()+ifcobject.getName());
		print(DASH);
		print(String.valueOf(ifcobject.getLineNum()));
		print("= ");
		String upperCase = ifcobject.getName().toUpperCase();
		print(upperCase);
		print(OPEN_PAREN);
		boolean isFirst = true;
		List<Property> properties = class2Properties.get(entityMap.get(ifcobject.getName()));
		HashMap<Integer, Object> values = ifcobject.getObjectList();
		for (int i = 0; i < properties.size(); i++) {
			Resource range = properties.get(i).getProperty(RDFS.range).getObject().asResource();
			if (values.get(i) == null) {
				if (!isFirst) {
					print(COMMA);
				}
				writeNull(ifcobject, properties.get(i));
			} else {
                Object value=values.get(i);
				if (value instanceof LinkedList<?>) {
					if (!isFirst) {
						print(COMMA);
					}
					writeSet((LinkedList<String>)value, properties.get(i), range);
				} else {
					String str = (String) value;
					if (!isFirst) {
						print(COMMA);
					}

					if (isOwlList(str)) {
						writeList(str, properties.get(i), range, false);
					}

					else if (isIfcInstance(str)) {
						writeIfcInstance(str);
					} else if (isEnumeration(str)) {
						writeEnum(str);
					} else {
						Resource clazz = schema.getResource(ontologyURI + typeMap.get(str));
						if (clazz.equals(range)) {
							writePrimitive(str);
						} else {
							if (IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema))
								writeEmbedded(str);
							else
								writePrimitive(str);
						}
					}
				}

			}
			isFirst = false;
		}
		println(PAREN_CLOSE_SEMICOLON);
	}


	private void println(String line) throws IOException {
		byte[] bytes = line.getBytes(Charsets.UTF_8);
		outputStream.write(bytes, 0, bytes.length);
		outputStream.write(NEW_LINE, 0, NEW_LINE.length);
	}

	private void writePrimitive(String str) throws IOException {
		if (valueMap.get(str) != null) {
			print((String) valueMap.get(str));
		} else
			print(DOLLAR);
	}

	private void writeNull(IfcObject ifcvo, Property property) throws IOException {
		if ((isSet(property) || isListOrArray(property)) && !isOptional(property)) {
			print(OPEN_CLOSE_PAREN);
		} else if (isDerived(ifcvo, property)) {
			print(ASTERISK);
		} else
			print(DOLLAR);
	}

	private void writeSet(LinkedList<String> list, Property property, Resource range) throws IOException {
		boolean isFirst = true;
		print(OPEN_PAREN);
		for (String str : list) {
			if (!isFirst) {
				print(COMMA);
			}
			if (isIfcInstance(str)) {
				writeIfcInstance(str);
			} else if (isOwlList(str)) {
				writeList(str, property, range, false);
			} else if (isEnumeration(str)) {
				writeEnum(str);
			} else {
				Resource clazz = schema.getResource(ontologyURI + typeMap.get(str));
				if (clazz.equals(range)) {
					writePrimitive(str);
				} else {
					// if (JenaUtil.hasSuperClass(clazz, range) &&
					if (IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema))
						writeEmbedded(str);
					else
						writePrimitive(str);
				}
			}

			isFirst = false;
		}
		print(CLOSE_PAREN);
	}

	private boolean isDerived(IfcObject ifcvo, Property property) {
		if (schema.getResource(ontologyURI + ifcvo.getName()).hasProperty(HASDERIVEATTRIBUTE, property)) {
			return true;
		}
		return false;
	}

	private void writeEmbedded(String str) throws IOException {
		// if(clazz.getURI().startsWith("http://purl.org/voc/express#")){
		// print("IFC"+clazz.getLocalName().toUpperCase());
		// }
		// else{
		print(typeMap.get(str).toUpperCase());
		// }
		print(OPEN_PAREN);
		writePrimitive(str);
		print(CLOSE_PAREN);
	}

	@SuppressWarnings("unchecked")
	private void writeList(String s, Property property, Resource range, boolean embededTypeList) throws IOException {
		Object list = getListValue(s);
		if (list instanceof LinkedList<?>) {
			if (((LinkedList<String>) list).size() == 0) {
				print(OPEN_CLOSE_PAREN);
			} else {
				boolean isFirst = true;
				if (JenaUtil.hasSuperClass(schema.getResource(ontologyURI + typeMap.get(s)), range)
						&& IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema)) {
					print(typeMap.get(s).toUpperCase());
					print(OPEN_PAREN);
					embededTypeList = true;
				}
				print(OPEN_PAREN);
				Resource range1 = range;
				if (range.getURI().endsWith("_List")) {
					range1 = schema.getResource(range.getURI().substring(0, range.getURI().length() - 5));
				}
				for (String str : (LinkedList<String>) list) {
					if (!isFirst) {
						print(COMMA);
					}
					if(str==null){
						print(DOLLAR);
						LOGGER.log(Level.WARNING, "A list has a null value, it is worth check:"+s);
					}
					if (isIfcInstance(str)) {
						writeIfcInstance(str);
					} else if (isOwlList(str)) {
						writeList(str, property, range1, embededTypeList);
					} else if (isEnumeration(str)) {
						writeEnum(str);
					} else {
						Resource clazz = schema.getResource(ontologyURI + typeMap.get(str));

						if (clazz.equals(range1)) {
							writePrimitive(str);
						} else {
							if (IfcOWLUtils.isDirectSubClassOf(range1, SELECT, schema) && !embededTypeList
									&& !JenaUtil.hasSuperClass(schema.getResource(ontologyURI + typeMap.get(s)),
											schema.getResource(clazz.getURI() + "_List")))
								writeEmbedded(str);
							else
								writePrimitive(str);
						}

					}

					isFirst = false;
				}
				if (JenaUtil.hasSuperClass(schema.getResource(ontologyURI + typeMap.get(s)), range)
						&& IfcOWLUtils.isDirectSubClassOf(range, SELECT, schema)) {
					print(CLOSE_PAREN);
				}
				print(CLOSE_PAREN);
			}
		}else {
			LOGGER.log(Level.WARNING,"Found a list value which is not a list: "+s);
		}
	}

	@SuppressWarnings({ "unchecked" })
	private LinkedList<String> getListValue(String s) {
		LinkedList<String> list = new LinkedList<String>();
		if (valueMap.get(s) != null){
			list.addAll((LinkedList<String>) valueMap.get(s));
		}
		else {
			list.add(null);
		    LOGGER.log(Level.WARNING,"Found a list without contents "+s);
		}
		while (listNextMap.get(s) != null) {
			LinkedList<String> next = (LinkedList<String>) valueMap.get(listNextMap.get(s));
			if (next != null)
				list.addAll(next);
			s = listNextMap.get(s);
		}
		return list;
	}

	/**
	 * if a resource is a Enumeration.
	 * 
	 * @param str
	 *            local name of the resource
	 * @return
	 */
	private boolean isEnumeration(String str) {
		if (enumMap.get(str) != null) {
			return true;
		}
		return false;
	}

	/**
	 * if a resource is a IFC object.
	 * 
	 * @param str
	 *            local name of the resource
	 * @return
	 */
	private boolean isIfcInstance(String str) {
		if (entityMap.get(typeMap.get(str)) != null) {
			return true;
		} else
			return false;
	}

	/**
	 * if a resource is a List object.
	 * 
	 * @param str
	 *            local name of the resource
	 * @return
	 */
	private boolean isOwlList(String str) {
		if (listMap.get(typeMap.get(str)) != null || (typeMap.get(str) != null && typeMap.get(str).endsWith("_List"))) {
			return true;
		} else
			return false;
	}

	/**
	 * if a property is SET in IFC schema
	 * 
	 * @param property
	 * @return
	 */
	private boolean isSet(Property property) {
		if (property.hasLiteral(ISSET, true)) {
			return true;
		} else
			return false;
	}

	/**
	 * if a property is LIST or ARRAY in IFC schema
	 * 
	 * @param property
	 * @return
	 */
	private boolean isListOrArray(Property property) {
		if (property.hasLiteral(ISLISTORARRAY, true)) {
			return true;
		} else
			return false;
	}

	/**
	 * if a property is optional in IFC schema
	 * 
	 * @param property
	 * @return
	 */
	private boolean isOptional(Property property) {
		if (property.hasLiteral(ISOPTIONAL, true)) {
			return true;
		} else
			return false;
	}

	private void print(String text) throws IOException {
		byte[] bytes = text.getBytes(Charsets.UTF_8);
		outputStream.write(bytes, 0, bytes.length);
	}

	/**
	 * Write ifc instance to the IFC SPF file.
	 * 
	 * @param str
	 * @throws IOException
	 */
	private void writeIfcInstance(String str) throws IOException {
		print(DASH + ifcMap.get(str).getLineNum());
	}

	/**
	 * Write enum value to the IFC SPF file.
	 * 
	 * @param str
	 * @throws IOException
	 */
	private void writeEnum(String str) throws IOException {
		if (str.equals(NULL)) {
			print(DOLLAR);
		} else {
			print(DOT);
			print(str);
			print(DOT);
		}
	}

	private void write(String s) {
		System.out.println(s);
	}

}
