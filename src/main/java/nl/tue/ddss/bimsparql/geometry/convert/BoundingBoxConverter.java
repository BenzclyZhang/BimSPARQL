package nl.tue.ddss.bimsparql.geometry.convert;

import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;

import fi.ni.rdf.Namespace;

import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.riot.RDFLanguages;
import org.apache.jena.riot.lang.PipedRDFIterator;
import org.apache.jena.riot.lang.PipedRDFStream;
import org.apache.jena.riot.lang.PipedTriplesStream;
import org.apache.jena.riot.system.StreamRDF;
import org.apache.jena.riot.writer.WriterStreamRDFBlocks;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.geometry.visitor.MVBBVisitor;

public class BoundingBoxConverter {
	
	StreamRDF rdfWriter;
	
	
	public void convertToBoundingBox(InputStream in, OutputStream out, String baseUri,boolean mvbb){
		setRdfWriter(new WriterStreamRDFBlocks(out));
		getRdfWriter().base(baseUri);
		getRdfWriter().prefix("inst", baseUri);
		getRdfWriter().prefix("rdf", Namespace.RDF);
		getRdfWriter().prefix("xsd", Namespace.XSD);
		getRdfWriter().prefix("owl", Namespace.OWL);
		getRdfWriter().prefix("geom", GEOM.getURI());
		getRdfWriter().start();

		getRdfWriter().finish();
			PipedRDFIterator<Triple> iter = new PipedRDFIterator<Triple>();
			final PipedRDFStream<Triple> stream = new PipedTriplesStream(iter);
			ExecutorService executor = Executors.newSingleThreadExecutor();
			Runnable parser = new Runnable() {
				public void run() {
					RDFDataMgr.parse(stream, in, RDFLanguages.TTL);
				}
			};
			executor.submit(parser);
			while (iter.hasNext()) {
				Triple t = iter.next();
				Node subject = t.getSubject();
				Node predicate = t.getPredicate();
				Node object = t.getObject();
				if(predicate.equals(GEOM.asBody.asNode())){
					Geometry g=EwktReader.parseGeometry((String)object.getLiteralValue());
					if(!g.isEmpty()){
					AABBVisitor av=new AABBVisitor();					
					g.accept(av);
					AABB aabb=av.getAABB();
					getRdfWriter().triple(new Triple(subject,GEOM.asAABB.asNode(),NodeFactory.createLiteral(EwktWriter.writeGeometry(aabb.toPolyhedralSurface()))));
					if(mvbb){
						MVBBVisitor mv=new MVBBVisitor();
						g.accept(mv);
						Box mvbbb=mv.getMVBB();
						getRdfWriter().triple(new Triple(subject,GEOM.asMVBB.asNode(),NodeFactory.createLiteral(EwktWriter.writeGeometry(mvbbb.toPolyhedralSurface()))));
					}
					}
				}

			}
			executor.shutdown();
			System.out.println("Model parsed!");	
			System.out.println("Finished!");
	}

	public StreamRDF getRdfWriter() {
		// TODO Auto-generated method stub
		return rdfWriter;
	}

	public void setRdfWriter(StreamRDF rdfWriter) {
		this.rdfWriter=rdfWriter;
		
	}

}
