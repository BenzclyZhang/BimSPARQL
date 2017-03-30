package nl.tue.ddss.bimsparql.function.geom;

import java.io.IOException;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Distance;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class distance3D extends FunctionBase2{

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {		
		try {
				Geometry g1 = new EwktReader(v1.getString()).readGeometry();
				Geometry g2=new EwktReader(v2.getString()).readGeometry();			
			    return NodeValue.makeDouble(Distance.distance3D(g1, g2));
			} catch (WktParseException | IOException |GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvNaN;
		}
	}
	

}
