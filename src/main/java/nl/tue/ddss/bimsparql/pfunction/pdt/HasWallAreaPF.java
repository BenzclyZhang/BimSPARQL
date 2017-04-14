package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;
import java.util.List;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.algorithm.Stitching;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductNumericalValue;

public class HasWallAreaPF extends FunctionBaseProductNumericalValue{


	public HasWallAreaPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}


	@Override
	protected double computeValue(Geometry geometry) {
		List<TriangulatedSurface> surfaces=null;
		try {
			surfaces = new Stitching().stitches(geometry,0.1);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return Double.NaN;
		}
		TriangulatedSurface largest=null;
		for (TriangulatedSurface surface:surfaces){
			if(largest!=null){
				if(Area.area(surface)>Area.area(largest)){
					largest=surface;
				}
			}else{
				largest=surface;
			}
		}
		return Area.area(largest);
	}



}
