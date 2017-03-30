package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.Distance;
import nl.tue.ddss.bimsparql.pfunction.FunctionBase2Product;

public class DistanceXY extends FunctionBase2Product{

	public DistanceXY(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph, Node product1, Node product2, Node distance,
			ExecutionContext execCxt) {
		return null;
	}

	@Override
	protected QueryIterator getValue(Binding binding, Graph graph, Node product1, Node product2, Var alloc,
			ExecutionContext execCxt) {
		Geometry g1=getGeometry(product1,execCxt.getActiveGraph());
		Geometry g2=getGeometry(product2,execCxt.getActiveGraph());
		double d=Distance.distance2D(g1,g2);
		Node node=NodeFactory.createLiteral(Double.toString(d),null,XSDDatatype.XSDdouble);
	    return IterLib.oneResult(binding, alloc, node, execCxt);
	}

}
