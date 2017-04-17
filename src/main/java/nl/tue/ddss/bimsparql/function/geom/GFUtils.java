package nl.tue.ddss.bimsparql.function.geom;

import java.io.IOException;

import com.hp.hpl.jena.sparql.expr.NodeValue;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;

public class GFUtils {
	
	public static Geometry read(NodeValue s){
		String string=s.getString();
		EwktReader er=new EwktReader(string);
		Geometry geometry;
		try {
			geometry = er.readGeometry();
			return geometry;
		} catch (WktParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}

	}
	
	public static NodeValue write(Geometry g){
		EwktWriter ew=new EwktWriter("");
		try {
			ew.writeRec(g);			
			return NodeValue.makeString(ew.getString());
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvEmptyString;
		}

	}

}
