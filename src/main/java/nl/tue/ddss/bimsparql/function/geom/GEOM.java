package nl.tue.ddss.bimsparql.function.geom;

import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;

import nl.tue.ddss.bimsparql.BimSPARQLNS;

public class GEOM {
	
	/**
	 * The namespace of the vocabulary as a string
	 */
	public static final String uri =BimSPARQLNS.GEOM;

    /** returns the URI for this schema
        @return the URI for this schema
    */
    public static String getURI()
        { return uri; }

    protected static final Resource resource( String local )
        { return ResourceFactory.createResource( uri + local ); }

    protected static final Property property( String local )
        { return ResourceFactory.createProperty( uri, local ); }

 


    public static final Property hasGeometry = property( "hasGeometry" );
    public static final Property asBody = property( "asBody" );
    public static final Property asAABB =property("asAABB");
    public static final Property asMVBB=property("asMVBB");


}
