package nl.tue.ddss.bimsparql.pfunction;

import java.io.IOException;
import java.util.HashMap;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.convert.IfcVersion;
import nl.tue.ddss.convert.Namespace;

public abstract class FunctionBaseProductOnGraph {
	
	protected final static double EPS=Geometry.EPS;
	
	protected HashMap<Node, Geometry> hashmap;
	protected IfcVersion ifcVersion;


	public FunctionBaseProductOnGraph(HashMap<Node, Geometry> hashmap) {
		super();
    	this.hashmap = hashmap;
	}

	public QueryIterator execEvaluated(Binding binding, Node product,
			 Node predicate, Node object,
			ExecutionContext execCxt) {
		Graph graph = execCxt.getActiveGraph();
		Geometry geometry=getGeometry(product,graph);
		if (Var.isVar(product))
			throw new ARQInternalErrorException(
					this.getClass().getName()+": Subject are variables without binding");
		if (Var.isVar(object))
			return getValue(binding, graph, product,geometry,
					Var.alloc(object), execCxt);
		else
			return verifyValue(binding, graph, product,geometry,object,
					execCxt);

	}


	private Geometry getGeometry(Node product, Graph graph) {
		Geometry geometry=hashmap.get(product);
		if(geometry!=null){
			return geometry;
		}
		ExtendedIterator<Triple> iterator=graph.find(product, GEOM.hasGeometry.asNode(), null);
		if(iterator.hasNext()){
			Triple t=iterator.next();
			Node object=t.getObject();
			ExtendedIterator<Triple> iter=graph.find(object, GEOM.asBody.asNode(), null);
			if(iter.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					geometry=new EwktReader(s).readGeometry();
					return geometry;
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else{
			geometry=new GeometryCollection();
			ExtendedIterator<Triple> iterator2=graph.find(product,NodeFactory.createURI(Namespace.IFC2X3_TC1+"isDecomposedBy_IfcObjectDefinition") , null);
			while (iterator2.hasNext()){
				Node rel=iterator2.next().getObject();
				ExtendedIterator<Triple> iter=graph.find(rel,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatedObjects_IfcRelDecomposes"),null);
				while(iter.hasNext()){
					Node object=iter.next().getObject();
					Geometry child=getGeometry(object,graph);
					((GeometryCollection)geometry).addGeometry(child);
				}
			}
			if(geometry.numGeometries()>0){
				return geometry;
			}
		}
		return null;
	}

	protected abstract QueryIterator verifyValue(Binding binding, Graph graph,
			Node product, Geometry geometry,Node object,
			ExecutionContext execCxt) ;


	protected abstract QueryIterator getValue(Binding binding, Graph graph, Node product,Geometry geometry,
			Var alloc, ExecutionContext execCxt) ;

}
