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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.engine.binding.BindingHashMap;
import com.hp.hpl.jena.sparql.engine.binding.BindingMap;
import com.hp.hpl.jena.sparql.engine.iterator.QueryIterConcat;
import com.hp.hpl.jena.sparql.expr.Expr;
import com.hp.hpl.jena.sparql.expr.ExprList;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArg;
import com.hp.hpl.jena.sparql.pfunction.PropFuncArgType;
import com.hp.hpl.jena.sparql.pfunction.PropertyFunctionEval;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;

import nl.tue.ddss.bimsparql.function.geom.GEOM;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.convert.Namespace;

public abstract class FunctionBaseSpatialRelation extends PropertyFunctionEval {
	
	

	protected HashMap<Node, Geometry> hashmap;


	public FunctionBaseSpatialRelation(HashMap<Node, Geometry> hashmap) {
		super(PropFuncArgType.PF_ARG_EITHER, PropFuncArgType.PF_ARG_EITHER);
		this.hashmap = hashmap;
	}

	public final QueryIterator execEvaluated(Binding binding,
			PropFuncArg argSubject, Node predicate, PropFuncArg argObject,
			ExecutionContext execCxt) {		
		ExprList subjectExprList = argSubject.asExprList(argSubject);
		ExprList objectExprList = argObject.asExprList(argObject);
		QueryIterConcat existingValues = null;

		if (objectExprList.size() == 1 && subjectExprList.size() == 1) {
			
			Expr subject = subjectExprList.get(0);
			Expr object = objectExprList.get(0);
			Node matchSubject = argSubject.getArg();
			Node matchObject = argObject.getArg();

			Graph queryGraph = execCxt.getActiveGraph();
//			queryGraph.add(new Triple(matchSubject,NodeFactory.createURI("www.tue.nl/try#add"),NodeFactory.createAnon()));
			List<Triple> lt = new LinkedList<Triple>();

			if (Var.isVar(matchSubject) || Var.isVar(matchObject)) {
				if (Var.isVar(matchSubject) && Var.isVar(matchObject)) {
					lt = getGeomQuery(binding, queryGraph, Var.alloc(matchSubject),
							predicate, Var.alloc(matchObject), execCxt);
				} else if (!Var.isVar(matchSubject)) {
					lt = getGeomQuery(binding, queryGraph, matchSubject,
							predicate, Var.alloc(matchObject), execCxt);
				} else if (!Var.isVar(matchObject)) {
					lt = getGeomQuery(binding, queryGraph,
							Var.alloc(matchSubject), predicate, matchObject,
							execCxt);

				}
				for (Triple triple:lt) {
					BindingMap map = new BindingHashMap(binding);
					if(subject.isVariable()) {
						map.add(subject.asVar(), triple.getSubject());
					}
					if (object.isVariable()) {
						map.add(object.asVar(), triple.getObject());
					}
					if (existingValues == null) {
						existingValues = new QueryIterConcat(execCxt);
					}
					QueryIterator nested = IterLib.result(map, execCxt);
					existingValues.add(nested);
				}
				return existingValues;
			} else {
                   return verify(binding, queryGraph,
               			matchSubject, predicate, matchObject,
            			execCxt);
			}

		}
		else{
		return IterLib.noResults(execCxt);}
	
	}


	private List<Triple> getGeomQuery(Binding binding, Graph queryGraph,
			Node matchSubject, Node predicate, Node matchObject,
			ExecutionContext execCxt) {
		List<Triple> lt = queryGraph.find(matchSubject, predicate, null).toList();
		if(Var.isVar(matchSubject)&&Var.isVar(matchObject)){
			HashMap<Node,Node> nodeMap=getNodeMap(execCxt);
			for(Node node:nodeMap.keySet()){
				lt.add(new Triple(node,predicate,nodeMap.get(node)));
			}
		}
		if(Var.isVar(matchSubject)){
			List<Node> nodes=getRelatedNodes(matchObject,execCxt);
			for (Node node:nodes){
				lt.add(new Triple(node,predicate,matchObject));
			}
		}
		else if(Var.isVar(matchObject)){
			List<Node> nodes=getRelatedNodes(matchSubject,execCxt);
			for (Node node:nodes){
				lt.add(new Triple(matchSubject,predicate,node));
			}
		    
		}
		return lt;
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

	
	protected QueryIterator verify(Binding binding, Graph queryGraph, Node matchSubject, Node predicate,
			Node matchObject, ExecutionContext execCxt) {
		Geometry s=getGeometry(matchSubject, execCxt.getActiveGraph());
		Geometry o=getGeometry(matchObject, execCxt.getActiveGraph());
		try {
			if(evaluateSpatialRelation(s,o)){
				return IterLib.result(binding, execCxt);
			}
			else{
				return IterLib.noResults(execCxt);
			}
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}return IterLib.noResults(execCxt);
	}


	protected List<Node> getRelatedNodes(Node node, ExecutionContext execCxt) {
		Geometry geometry=getGeometry(node, execCxt.getActiveGraph());
		List<Node> nodes=new ArrayList<Node>();
		for (Node n:hashmap.keySet()){
			try {
				if(evaluateSpatialRelation(geometry, getGeometry(n,execCxt.getActiveGraph()))){
					nodes.add(n);
				}
			} catch (GeometryException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		nodes.remove(node);
		return nodes;   
	}


	protected HashMap<Node, Node> getNodeMap(ExecutionContext execCxt) {
		HashMap<Node,Node> nodeMap=new HashMap<Node,Node>();
		for (Node n:hashmap.keySet()){
			Geometry g1=getGeometry(n,execCxt.getActiveGraph());
			for(Node no:hashmap.keySet()){
				if(no!=n){
				Geometry g2=getGeometry(no,execCxt.getActiveGraph());
				try {
					if(evaluateSpatialRelation(g1, g2)){
						nodeMap.put(n,no);
					}
				} catch (GeometryException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}						
				}
			}
		}
		return nodeMap;   
	}

	protected abstract boolean evaluateSpatialRelation(Geometry g1, Geometry g2) throws GeometryException;
}
