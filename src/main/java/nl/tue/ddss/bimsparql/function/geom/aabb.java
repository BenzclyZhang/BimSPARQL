package nl.tue.ddss.bimsparql.function.geom;

import java.io.IOException;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;

public class aabb extends FunctionBase1{

	@Override
	public NodeValue exec(NodeValue v) {
		EwktReader er=new EwktReader(v.getString());
		try {
			Geometry g=er.readGeometry();
			AABBVisitor visitor=new AABBVisitor();
			g.accept(visitor);
			EwktWriter ew=new EwktWriter("");
			ew.writeRec(visitor.getAABB().toPolyhedralSurface());
			
			return NodeValue.makeString(ew.getString());
		} catch (WktParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvNothing;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvNothing;
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvNothing;
		}
	}

}
