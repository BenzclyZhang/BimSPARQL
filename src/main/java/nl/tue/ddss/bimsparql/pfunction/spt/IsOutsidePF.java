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
package nl.tue.ddss.bimsparql.pfunction.spt;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.algorithm.ExternalQuadTreeImpl;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductOnGraph;
import nl.tue.ddss.convert.Namespace;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.core.Var;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.sparql.engine.QueryIterator;
import com.hp.hpl.jena.sparql.engine.binding.Binding;
import com.hp.hpl.jena.sparql.util.IterLib;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class IsOutsidePF extends FunctionBaseProductOnGraph {
	
	private static final Node IfcWallStandardCase=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcWallStandardCase");
	private static final Node IfcWall=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcWall");
	private static final Node IfcWindow=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcWindow");
	private static final Node IfcDoor=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcDoor");
	private static final Node IfcRoof=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcRoof");
	private static final Node IfcCurtainWall=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcCurtainWall");
	private static final Node IfcRamp=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcRamp");
	private static final Node IfcColumn=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcColumn");
	private static final Node IfcPlate=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcPlate");
	private static final Node IfcMember=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcMember");
	private static final Node IfcRailing=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcRailing");
	private static final Node IfcBeam=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcBeam");
	private static final Node IfcSlab=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcSlab");
	private static final Node IfcStair=NodeFactory.createURI(Namespace.IFC2X3_TC1+"IfcStair");
	private static final Node[] ProcessableTypes={IfcWallStandardCase,IfcWall, IfcWindow, IfcDoor, IfcRoof, IfcCurtainWall, IfcRamp, IfcColumn,IfcPlate,IfcMember,IfcRailing,IfcBeam,IfcSlab,IfcStair};
	
	public IsOutsidePF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
	}
    

	
	@Override
	protected QueryIterator verifyValue(Binding binding, Graph graph, Node product, Geometry geometry, Node object,
			ExecutionContext execCxt) {
		boolean b = false;
		try {
			if (product.getLocalName().equals("IfcWallStandardCase_5397")){
				System.out.println("");
			}
			HashSet<Geometry> allGeometries=getProcessableElements(graph);
			b = new ExternalQuadTreeImpl(geometry, allGeometries).getIsExternal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node = NodeFactory.createLiteral(Boolean.toString(b), null, XSDDatatype.XSDboolean);
		if (node.equals(object)) {
			return IterLib.result(binding, execCxt);
		} else {
			return IterLib.noResults(execCxt);
		}
	}
	


	@Override
	protected QueryIterator getValue(Binding binding, Graph graph, Node product, Geometry geometry, Var alloc,
			ExecutionContext execCxt) {
		boolean b = false;
		try {
			HashSet<Geometry> allGeometries=getProcessableElements(graph);
			b = new ExternalQuadTreeImpl(geometry, allGeometries).getIsExternal();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Node node = NodeFactory.createLiteral(Boolean.toString(b), null, XSDDatatype.XSDboolean);
		return IterLib.oneResult(binding, alloc, node, execCxt);
	}
	
	
	protected HashSet<Geometry> getAllGeometries(Node type,HashMap<Node,Geometry> hashmap,Graph graph){
		HashSet<Geometry> allGeometries = new HashSet<Geometry>();
		for(Node node:hashmap.keySet()){
			if(instanceOf(node,type,graph)){
				allGeometries.add(hashmap.get(node));
			}
		}
		return allGeometries;
	}
	
	
	private HashSet<Geometry> getProcessableElements(Graph graph){
		HashSet<Geometry> all=new HashSet<Geometry>();
		for(Node cls:ProcessableTypes){
			ExtendedIterator<Triple> it=graph.find(null, RDF.type.asNode(), cls);
			while(it.hasNext()){
				Geometry geometry=getGeometry(it.next().getSubject(),graph);
				if(geometry!=null){
					addGeometry(all,geometry);
		}
			}

		}
		return all;
	}
	
	private void addGeometry(HashSet<Geometry> all, Geometry geometry){
		if(geometry.geometryTypeId()==GeometryType.TYPE_TRIANGULATEDSURFACE){
			all.add(geometry);
		}else if(geometry.geometryTypeId()==GeometryType.TYPE_GEOMETRYCOLLECTION){
			for(int i=0;i<geometry.numGeometries();i++){
				addGeometry(all,geometry.geometryN(i));
			}
		}
	}
	
	protected HashSet<Node> getInstanceOf(Node cls,Graph graph){
		HashSet<Node> nodes=new HashSet<Node>();
		ExtendedIterator<Triple> it=graph.find(null, RDF.type.asNode(), cls);
		while(it.hasNext()){
			nodes.add(it.next().getSubject());
		}
		return nodes;
	}
	protected HashSet<Node> getAllFatherElements(Node node, Graph graph,HashSet<Node> allElements) {
		ExtendedIterator<Triple> iterator2=graph.find(null,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatedObjects_IfcRelDecomposes") , node);
		while (iterator2.hasNext()){
			Node rel=iterator2.next().getSubject();
			ExtendedIterator<Triple> iter=graph.find(rel,NodeFactory.createURI(Namespace.IFC2X3_TC1+"relatingObject_IfcRelDecomposes"),null);
			while(iter.hasNext()){
				Node object=iter.next().getObject();
				allElements.add(object);
				allElements=getAllFatherElements(object,graph,allElements);
			}
		}
		return allElements;
	}

	protected boolean instanceOf(Node node, Node clazz,Graph graph){
		ExtendedIterator<Triple> it=graph.find(node, RDF.type.asNode(), null);
		while(it.hasNext()) {
			Triple s = it.next();
				Node actualType = s.getObject();
				if(actualType.equals(clazz) || hasSuperClass(actualType, clazz,new HashSet<Node>(),graph)) {
					it.close();
					return true;
				}
		}
		return false;
	}
	

	
	
	private static boolean hasSuperClass(Node subClass, Node superClass, Set<Node> reached,Graph graph) {
		for(Triple s : graph.find(subClass,RDFS.subClassOf.asNode(),null).toList()) {
			if(superClass.equals(s.getObject())) {
				return true;
			}
			else if(!reached.contains(s.getObject())) {
				reached.add(s.getObject());
				if(hasSuperClass(s.getObject(), superClass, reached,graph)) {
					return true;
				}
			}
		}
		return false;
	}

}
