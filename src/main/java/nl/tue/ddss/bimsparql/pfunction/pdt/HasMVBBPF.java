package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.ConvexHull;
import nl.tue.ddss.bimsparql.geometry.algorithm.MVBB;
import nl.tue.ddss.bimsparql.geometry.algorithm.Polyhedron;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktTValue;

public class HasMVBBPF extends FunctionBaseProductEwktTValue{

	public HasMVBBPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
		PolyhedralSurface ps=new ConvexHull().buildConvexHull(geometry);
		Polyhedron ph=new Polyhedron(ps);
		MVBB obb=new MVBB(ph);
		obb.computeMinBB();
		Box box=obb.getBox();
		EwktWriter ew=new EwktWriter("");
		try {
			ew.writeRec(box.toPolyhedralSurface());
			return ew.getString();
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
