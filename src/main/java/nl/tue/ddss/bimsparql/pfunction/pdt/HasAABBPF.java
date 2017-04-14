package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;
import com.hp.hpl.jena.graph.Node;


import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktTValue;

public class HasAABBPF extends FunctionBaseProductEwktTValue{

	public HasAABBPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
        AABBVisitor visitor=new AABBVisitor();
		geometry.accept(visitor);
		AABB aabb=visitor.getAABB();
		EwktWriter ew=new EwktWriter("");
		try {
			ew.writeRec(aabb.toPolyhedralSurface());
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		return ew.getString();
	}

}
