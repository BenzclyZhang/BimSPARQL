package nl.tue.ddss.bimsparql.pfunction.pdt;

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
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.BoundingBoxVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProduct;

public class HasOverallLengthPF extends FunctionBaseProduct{


	public HasOverallLengthPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph,
			Node product, Geometry geometry,Node object, ExecutionContext execCxt) {
		double obj=0;
		try{
		obj=(Double)object.getLiteralValue();
		}catch (Exception e){
			return IterLib.noResults(execCxt);
		}
		double b=0;
        BoundingBoxVisitor visitor=new BoundingBoxVisitor();
		geometry.accept(visitor);
		AABB aabb=visitor.getAABB();
		if(aabb!=null){
		b= aabb.getXLength();
		}
		if(b>obj-EPS&&b<obj+EPS){
			return IterLib.result(binding, execCxt);
		}
		return IterLib.noResults(execCxt);
	}

	@Override
	protected QueryIterator getValue(Binding binding, Graph graph,
			Node product, Geometry geometry,Var alloc, ExecutionContext execCxt) {
		double b=0;
        BoundingBoxVisitor visitor=new BoundingBoxVisitor();
		geometry.accept(visitor);
		AABB aabb=visitor.getAABB();
		if(aabb!=null){
		b= aabb.getXLength();
		}
        Node node=NodeFactory.createLiteral(Double.toString(b),null,XSDDatatype.XSDdouble);
		return IterLib.oneResult(binding, alloc, node, execCxt);
	}

}
