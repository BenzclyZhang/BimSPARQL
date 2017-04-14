package nl.tue.ddss.bimsparql.pfunction.spt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseSpatialRelationOnGraph;

public class isLocatedInStoreyPF extends FunctionBaseSpatialRelationOnGraph{

	public isLocatedInStoreyPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected boolean evaluateSpatialRelation(Geometry g1, Geometry g2) throws GeometryException {
		// TODO Auto-generated method stub
		return false;
	}

}
