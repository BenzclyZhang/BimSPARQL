package nl.tue.ddss.bimsparql.pfunction;

import java.util.HashMap;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;

public abstract class FunctionBaseProductEwktTValue extends FunctionBaseProduct{

	public FunctionBaseProductEwktTValue(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph, Node product, Geometry geometry, Node object,
			ExecutionContext execCxt) {
		String obj;
		try{
		obj=(String)object.getLiteralValue();
		}catch (Exception e){
			return IterLib.noResults(execCxt);
		}
		String b=computeValue(geometry);
		if(b==null){
			return IterLib.noResults(execCxt);
		}
		if(b.equals(obj)){
			return IterLib.result(binding, execCxt);
		}
		return IterLib.noResults(execCxt);
	}

	@Override
	protected QueryIterator getValue(Binding binding, Graph graph, Node product, Geometry geometry, Var alloc,
			ExecutionContext execCxt) {
		String b=computeValue(geometry);
		if(b!=null){
        Node node=NodeFactory.createLiteral(b,null,XSDDatatype.XSDstring);
		return IterLib.oneResult(binding, alloc, node, execCxt);
		}else{
			return IterLib.noResults(execCxt);
		}
	}
	
	protected String writeEwkt(Geometry geometry){
		EwktWriter ew=new EwktWriter("");
		try {
			ew.writeRec(geometry);
			return ew.getString();
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	
	}
	
    protected abstract String computeValue(Geometry geometry);
}