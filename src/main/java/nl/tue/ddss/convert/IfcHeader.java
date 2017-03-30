package nl.tue.ddss.convert;

import com.hp.hpl.jena.rdf.model.Resource;



import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.ResourceFactory;


/**
 * Defined vocabulary for IFC header
 * @author Chi
 *
 */
public class IfcHeader {
	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri ="http://www.openbim.org/ifcowl/ifcheader#";
	public static final String ns="ifch";

    /** returns the URI for this schema
        @return the URI for this schema
    */
    public static String getURI()
        { return uri; }

    protected static final Resource resource( String local )
        { return ResourceFactory.createResource( uri + local );}

    protected static final Property property( String local )

        { return ResourceFactory.createProperty( uri, local ); }

 


    public static final Property description = property( "description" );
    public static final Property implementation_level = property( "implementation_level" );
    public static final Property name = property( "name" );
    public static final Property time_stamp = property( "time_stamp" );
    public static final Property author = property( "author" );
    public static final Property organization = property( "organization" );
    public static final Property preprocessor_version = property( "preprocessor_version" );
    public static final Property originating_system = property( "originating_system" );
    public static final Property authorization = property( "authorization" );
    public static final Property schema_identifiers = property( "schema_identifiers" );
    

}
