package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductNumericalValue;

public class HasOverallLengthPF extends FunctionBaseProductNumericalValue{


	public HasOverallLengthPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}

	@Override
	protected double computeValue(Geometry geometry) {
		double b=0;
        AABBVisitor visitor=new AABBVisitor();
		geometry.accept(visitor);
		AABB aabb=visitor.getAABB();
		if(aabb!=null){
		b= aabb.getXLength();
		}
		return b;
	}

}
