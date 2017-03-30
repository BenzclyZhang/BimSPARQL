package nl.tue.ddss.convert.rdf2ifc;

import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.ResourceFactory;

/**
 * vocabulary used in ifcOWL supplyment files.
 * @author Chi
 *
 */
public class IfcOWLSupplement {
	public static final String uri = "http://www.tue.nl/ddss/ifcOWL_supplementary#";
	
	 public static String getURI()
     { return uri; }

 protected static final Resource resource( String local )
     { return ResourceFactory.createResource( uri + local ); }

 protected static final Property property( String local )
     { return ResourceFactory.createProperty( uri, local ); }




 public static final Property isIfcEntity = property("isIfcEntity");
 public static final Property isTopLevelIfcEntity=property("isTopLevelIfcEntity");
 public static final Property index=property("index");
 public static final Property isListOrArray=property("isListOrArray");
 public static final Property isSet=property("isSet");
 public static final Property isOptional=property("isOptional");
 public static final Property hasDeriveAttribute=property("hasDeriveAttribute");
 

}
