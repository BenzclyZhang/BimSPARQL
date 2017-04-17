/*******************************************************************************
 * Copyright (C) 2017 Chi Zhang
 * 
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * 
 * You should have received a copy of the GNU Affero General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 ******************************************************************************/
package nl.tue.ddss.bimsparql.pfunction;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

import nl.tue.ddss.convert.Namespace;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.QueryBuildException;
import com.hp.hpl.jena.sparql.ARQInternalErrorException;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

public abstract class FunctionBaseGroupProduct2 extends PFuncListAndSimple{
	
	private static final double EPS = Geometry.EPS;
	protected HashMap<Node, Geometry> hashmap;


	public FunctionBaseGroupProduct2(HashMap<Node, Geometry> hashmap) {
		super();
		this.hashmap = hashmap;
	}


	@Override
	public QueryIterator execEvaluated(Binding binding, List<Node> subject,
			Node predicate, Node object, ExecutionContext execCxt) {
		// TODO Auto-generated method stub
		if(subject.size()!=2){
			throw new QueryBuildException("Not 2 arguments (subject) to "+predicate.getURI());
		}
		return execEvaluated(binding, subject.get(0),subject.get(1),
				predicate, object, execCxt);
}
	protected QueryIterator execEvaluated(Binding binding, Node product1,
			Node product2, Node predicate, Node distance,
			ExecutionContext execCxt) {
		Graph graph = execCxt.getActiveGraph();
		if (Var.isVar(product1) || Var.isVar(product2))
			throw new ARQInternalErrorException(
					"distance: Subject are variables without binding");
		if (Var.isVar(distance))
			return getValue(binding, graph, product1, product2,
					Var.alloc(distance), execCxt);
		else
			return verifyValue(binding, graph, product1, product2, distance,
					execCxt);
	}
	
	protected Geometry getGeometry(Node product, Graph graph) {
		Geometry geometry=hashmap.get(product);
		if(geometry!=null){
			return geometry;
		}
		ExtendedIterator<Triple> iterator=graph.find(product, GEOM.hasGeometry.asNode(), null);
		if(iterator.hasNext()){
			Triple t=iterator.next();
			Node object=t.getObject();
			ExtendedIterator<Triple> iter=graph.find(object, GEOM.asBody.asNode(), null);
			if(iter.hasNext()){
				String s=iter.next().getObject().getLiteralValue().toString();
				try {
					geometry=new EwktReader(s).readGeometry();
					return geometry;
				} catch (WktParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} else{
			geometry=new GeometryCollection();
			ExtendedIterator<Triple> iterator2=graph.find(product,NodeFactory.createURI(Namespace.IFC2X3_TC1+"isDecomposedBy_IfcObjectDefinition") , null);
			while (iterator2.hasNext()){
				Node rel=iterator2.next().getObject();
				ExtendedIterator<Triple> iter=graph.find(rel,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatedObjects_IfcRelDecomposes"),null);
				while(iter.hasNext()){
					Node object=iter.next().getObject();
					Geometry child=getGeometry(object,graph);
					((GeometryCollection)geometry).addGeometry(child);
				}
			}
			if(geometry.numGeometries()>0){
				return geometry;
			}
		}
		return null;
	}


	protected QueryIterator verifyValue(Binding binding, Graph graph,
			Node product1, Node product2, Node node,
			ExecutionContext execCxt) {
		Geometry g1=getGeometry(product1,graph);
		Geometry g2=getGeometry(product2,graph);
		Object obj=computeValue(g1,g2);
		if(obj==null){
			return IterLib.noResults(execCxt);
		}else{
			if(obj instanceof Double){
				try{
					double value=(Double)node.getLiteralValue();
					
				
					if((Double)obj>value-EPS&&(Double)obj<value+EPS){
						return IterLib.result(binding, execCxt);
					}return IterLib.noResults(execCxt);
			}catch (Exception e){
				return IterLib.noResults(execCxt);
			}
			}
             else if(obj instanceof String){
     				try{
     					String value=(String)node.getLiteralValue()    		;		
     					if(((String)obj).equals(value)){
     						return IterLib.result(binding, execCxt);
     					}return IterLib.noResults(execCxt);
     			}catch (Exception e){
     				return IterLib.noResults(execCxt);
     			}
				
			}
		}
		return IterLib.noResults(execCxt);
	}
	


	protected QueryIterator getValue(Binding binding, Graph graph, Node product1,
			Node product2, Var alloc, ExecutionContext execCxt) {
		Geometry g1=getGeometry(product1,graph);
		Geometry g2=getGeometry(product2,graph);
		Object obj=computeValue(g1,g2);
		if(obj==null){
			return IterLib.noResults(execCxt);
		}else{
			if(obj instanceof Double){
				Node node=NodeFactory.createLiteral(Double.toString((Double)obj),null,XSDDatatype.XSDdouble);
				return IterLib.oneResult(binding, alloc, node, execCxt);
			}else if(obj instanceof String){
				Node node=NodeFactory.createLiteral((String)obj,null,XSDDatatype.XSDstring);
				return IterLib.oneResult(binding, alloc, node, execCxt);
			}
		}
		return IterLib.noResults(execCxt);
	}


	protected abstract Object computeValue(Geometry g1, Geometry g2);
}



