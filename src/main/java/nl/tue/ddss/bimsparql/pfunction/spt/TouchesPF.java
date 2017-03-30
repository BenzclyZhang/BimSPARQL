package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Intersects;
import nl.tue.ddss.bimsparql.pfunction.GeometryFunctionBase;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;

public class TouchesPF extends GeometryFunctionBase{
	
	public TouchesPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}
    protected List<Node> getRelatedNodes(Node node,ExecutionContext execCxt){
    	Geometry geometry=getGeometry(node, execCxt.getActiveGraph());
		List<Node> nodes=new ArrayList<Node>();
		for (Node n:hashmap.keySet()){
			try {
				if(Intersects.intersects3D(geometry, getGeometry(n,execCxt.getActiveGraph()))==2){
					nodes.add(n);
				}
			} catch (GeometryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		nodes.remove(node);
		return nodes;   	 	
    }
	@Override
	protected QueryIterator verify(Binding binding, Graph queryGraph,
			Node matchSubject, Node predicate, Node matchObject,
			ExecutionContext execCxt) {
		Geometry s=getGeometry(matchSubject, execCxt.getActiveGraph());
		Geometry o=getGeometry(matchObject, execCxt.getActiveGraph());
		try {
			if(Intersects.intersects3D(s, o)==2){
				return IterLib.result(binding, execCxt);
			}
			else{
				return IterLib.noResults(execCxt);
			}
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return IterLib.noResults(execCxt);
	}

}
