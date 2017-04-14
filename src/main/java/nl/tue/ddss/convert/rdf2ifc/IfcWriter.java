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


import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;


import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;

import org.apache.commons.io.Charsets;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFLanguages;

import nl.tue.ddss.convert.Header;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.IfcVersionException;
import nl.tue.ddss.convert.Namespace;

public abstract class IfcWriter {

	protected Model schema = ModelFactory.createDefaultModel();
	protected IfcVersion ifcVersion;
	protected OutputStream outputStream;

	protected static final byte[] NEW_LINE = "\n".getBytes(Charsets.UTF_8);
	protected static final String NULL = "NULL";
	protected static final String OPEN_CLOSE_PAREN = "()";
	protected static final String ASTERISK = "*";
	protected static final String PAREN_CLOSE_SEMICOLON = ");";
	protected static final String DOT_0 = ".0";
	protected static final String DASH = "#";
	protected static final String DOT = ".";
	protected static final String COMMA = ",";
	protected static final String OPEN_PAREN = "(";
	protected static final String CLOSE_PAREN = ")";
	protected static final String BOOLEAN_UNKNOWN = ".U.";
	protected static final String SINGLE_QUOTE = "'";
	protected static final String DOUBLE_QUOTE = "\"";
	protected static final String BOOLEAN_FALSE = ".F.";
	protected static final String BOOLEAN_TRUE = ".T.";
	protected static final String DOLLAR = "$";

	protected final Property ISIFCENTITY = schema.getProperty(IfcOWLSupplement.isIfcEntity.getURI());
	protected final Resource OWLLIST = schema.getResource(Namespace.LIST + "OWLList");
	protected final Resource ENUMERATION = schema.getResource(Namespace.EXPRESS + "ENUMERATION");
	protected final Property ISLISTORARRAY = schema.getProperty(IfcOWLSupplement.getURI() + "isListOrArray");
	protected final Property ISSET = schema.getProperty(IfcOWLSupplement.getURI() + "isSet");
	protected final Property ISOPTIONAL = schema.getProperty(IfcOWLSupplement.getURI() + "isOptional");
	protected final Property HASDERIVEATTRIBUTE = schema.getProperty(IfcOWLSupplement.getURI() + "hasDeriveAttribute");
	protected final Resource SELECT = schema.getResource(Namespace.EXPRESS + "SELECT");
	protected final Resource EXPRESS_TRUE = schema.getProperty(Namespace.EXPRESS + "TRUE");
	protected final Resource EXPRESS_FALSE = schema.getProperty(Namespace.EXPRESS + "FALSE");
	protected final Resource EXPRESS_UNKNOWN = schema.getProperty(Namespace.EXPRESS + "UNKNOWN");

	protected final Resource NUMBER = schema.getResource(Namespace.EXPRESS + "NUMBER");
	protected final Resource INTEGER = schema.getResource(Namespace.EXPRESS + "INTEGER");
	protected final Resource REAL = schema.getResource(Namespace.EXPRESS + "REAL");
	protected final Resource BOOLEAN = schema.getResource(Namespace.EXPRESS + "BOOLEAN");
	protected final Resource LOGICAL = schema.getResource(Namespace.EXPRESS + "LOGICAL");
	protected final Resource STRING = schema.getResource(Namespace.EXPRESS + "STRING");
	protected final Resource BINARY = schema.getResource(Namespace.EXPRESS + "BINARY");

	protected final Property HASNEXT = schema.getProperty(Namespace.LIST + "hasNext");
	protected final Property HASCONTENTS = schema.getProperty(Namespace.LIST + "hasContents");
	protected final Property HASBOOLEAN = schema.getProperty(Namespace.EXPRESS + "hasBoolean");
	protected final Property HASSTRING = schema.getProperty(Namespace.EXPRESS + "hasString");
	protected final Property HASLOGICAL = schema.getProperty(Namespace.EXPRESS + "hasLogical");
	protected final Property HASDOUBLE = schema.getProperty(Namespace.EXPRESS + "hasDouble");
	protected final Property HASINTEGER = schema.getProperty(Namespace.EXPRESS + "hasInteger");
	protected final Property HASHEXBINARY = schema.getProperty(Namespace.EXPRESS + "hasHexBinary");

	protected Header header = new Header();

	protected HashMap<Resource, List<Property>> class2Properties;

	public IfcWriter() {
		this(null);
	}

	public IfcWriter(IfcVersion ifcVersion) {
		this(ifcVersion, false);
	}

	public IfcWriter(IfcVersion ifcVersion, boolean updateNS) {
		try {
			if (updateNS == false) {
				IfcVersion.initDefaultIfcNsMap();
				IfcVersion.initDefaultNsIfcMap();
			} else {
				IfcVersion.initIfcNsMap();
				IfcVersion.initNsIfcMap();
			}
		} catch (IfcVersionException e) {
			e.printStackTrace();
		}
		this.ifcVersion = ifcVersion;
	}

	public void convert(String inputUri, String outputFileName, boolean logFile) throws IOException {
		convert(inputUri, outputFileName, RDFLanguages.filenameToLang(inputUri), logFile);
	}

	/**
	 * convert a IFC RDF file to IFC STEP file.
	 * 
	 * @param inputUri
	 *            uri of input including file name
	 * @param outputFileName
	 *            out put file name
	 * @throws IOException
	 */
	public void convert(String inputUri, String outputFileName) throws IOException {
		convert(inputUri, outputFileName, RDFLanguages.filenameToLang(inputUri), false);
	}


	/**
	 * @param inputUri
	 * @param outputFileName
	 * @param lang
	 * @param logFile
	 * @throws IOException
	 */
	public void convert(String inputUri, String outputFileName, Lang lang, boolean logFile) throws IOException {
		if (logFile == false) {
			convert(inputUri, outputFileName, lang, null);
		} else {
			String logFilePath = outputFileName.substring(0, outputFileName.length() - 4) + ".log";
			convert(inputUri, outputFileName, lang, logFilePath);
		}

	}

	public abstract void convert(String inputUri, String outputFileName, Lang lang, String logFilePath)
			throws IOException;

}
