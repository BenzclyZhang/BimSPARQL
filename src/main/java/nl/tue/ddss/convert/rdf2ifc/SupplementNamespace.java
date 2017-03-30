package nl.tue.ddss.convert.rdf2ifc;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

public class SupplementNamespace {
	public static final String uri = "http://www.tue.nl/ddss/ifcOWL_supplementary#";
	
	 public static String getURI()
     { return uri; }

 protected static final Resource resource( String local )
     { return ResourceFactory.createResource( uri + local ); }

 protected static final Property property( String local )
     { return ResourceFactory.createProperty( uri, local ); }




 public static final Property isIfcEntity = property( "isIfcEntity" );

}
