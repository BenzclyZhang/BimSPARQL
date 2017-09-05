package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Topology;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseSpatialRelation;

public class EqualsPF extends FunctionBaseSpatialRelation{
	
	public EqualsPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}
	
	@Override
	protected boolean evaluateSpatialRelation(Geometry g1, Geometry g2) throws GeometryException {
		return Topology.intersects3D(g1, g2)==5;
	}

}
