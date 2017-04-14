package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;
import java.util.List;


import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.algorithm.Stitching;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktTValue;

public class HasLargestSurfacePF extends FunctionBaseProductEwktTValue{

	public HasLargestSurfacePF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
		List<TriangulatedSurface> surfaces=null;
		try {
			surfaces = new Stitching().stitches(geometry);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
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
		return writeEwkt(largest);
	}

}