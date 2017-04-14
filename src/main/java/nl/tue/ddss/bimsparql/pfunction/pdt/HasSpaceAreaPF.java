package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;
import java.util.List;

import javax.vecmath.Vector3d;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.algorithm.Normal;
import nl.tue.ddss.bimsparql.geometry.algorithm.Stitching;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductNumericalValue;

public class HasSpaceAreaPF extends FunctionBaseProductNumericalValue{

	public HasSpaceAreaPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected double computeValue(Geometry geometry) {
		List<TriangulatedSurface> surfaces=null;
		try {
			surfaces = new Stitching().stitches(geometry);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
		TriangulatedSurface lowest=null;
		for (TriangulatedSurface surface:surfaces){
			if(lowest!=null){
				if(elevation(surface)<elevation(lowest)){
					lowest=surface;
				}
			}else{
				lowest=surface;
			}
		}
		Vector3d normal=Normal.normal(lowest.triangleN(0));
		double dot=normal.dot(new Vector3d(0,0,-1));
		if ((dot>1-EPS&&dot<1+EPS)||(dot>-1-EPS&&dot<-1+EPS)){
		return Area.area(lowest);
		}else{
			return Double.NaN;
		}
	}
    
	protected double elevation(Geometry geometry){
		if(geometry.getAABB()!=null){
			return (geometry.getAABB().min.z+geometry.getAABB().max.z)/2;
		}
		AABBVisitor visitor=new AABBVisitor();
		geometry.accept(visitor);
		geometry.setAABB(visitor.getAABB());
		return (visitor.getAABB().min.z+visitor.getAABB().max.z)/2;
	}

}
