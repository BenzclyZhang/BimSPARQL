package nl.tue.ddss.bimsparql.function.geom;

import java.io.IOException;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;

import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Area;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class area extends FunctionBase1{

	@Override
	public NodeValue exec(NodeValue v) {
		// TODO Auto-generated method stub
		try {
			return NodeValue.makeDouble(Area.area(new EwktReader(v.getString()).readGeometry()));
		} catch (GeometryException | WktParseException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvZERO;
		}
	}

}
