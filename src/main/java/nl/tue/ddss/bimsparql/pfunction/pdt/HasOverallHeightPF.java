package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductNumericalValue;

import com.hp.hpl.jena.graph.Node;

public class HasOverallHeightPF extends FunctionBaseProductNumericalValue{


	public HasOverallHeightPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected double computeValue(Geometry geometry) {
		double b=0;
        AABBVisitor visitor=new AABBVisitor();
		geometry.accept(visitor);
		AABB aabb=visitor.getAABB();
		if(aabb!=null){
		b= aabb.getHeight();
		}
		return b;
	}






}
