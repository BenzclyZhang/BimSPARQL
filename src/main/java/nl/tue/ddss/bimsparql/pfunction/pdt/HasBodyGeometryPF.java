package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;


import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktTValue;

public class HasBodyGeometryPF extends FunctionBaseProductEwktTValue{

	public HasBodyGeometryPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
		if (geometry==null){
			return null;
		}
		EwktWriter writer=new EwktWriter("");
		try {
			writer.writeRec(geometry);
		} catch (WktWriteException e) {
			e.printStackTrace();
			return null;
		}		
	    String s=writer.getString();
		return s;
	}



}
