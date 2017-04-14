package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Intersects;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseSpatialRelation;

import com.hp.hpl.jena.graph.Node;

public class IntersectsPF extends FunctionBaseSpatialRelation{

	public IntersectsPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}

	@Override
	protected boolean evaluateSpatialRelation(Geometry g1, Geometry g2) throws GeometryException {
		return Intersects.intersects3D(g1, g2)==1;
	}


}
