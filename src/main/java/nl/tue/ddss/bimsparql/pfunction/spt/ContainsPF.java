package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.pfunction.GeometryFunctionBase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;

public class ContainsPF extends GeometryFunctionBase{

	public ContainsPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected QueryIterator verify(Binding binding, Graph queryGraph,
			Node matchSubject, Node predicate, Node matchObject,
			ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected List<Node> getRelatedNodes(Node node,ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		return null;
	}

}
