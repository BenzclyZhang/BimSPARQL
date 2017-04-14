package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Intersects;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseSpatialRelation;

import com.hp.hpl.jena.graph.Node;

public class ContainsPF extends FunctionBaseSpatialRelation{

	public ContainsPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean evaluateSpatialRelation(Geometry g1, Geometry g2) throws GeometryException {
		return Intersects.intersects3D(g1, g2)==3;
	}


}
