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

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.sparql.engine.ExecutionContext;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDF;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import nl.tue.ddss.bimsparql.geometry.visitor.AABBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseSpatialRelationOnGraph;
import nl.tue.ddss.convert.Namespace;

public class HasUpperFloorPF extends FunctionBaseSpatialRelationOnGraph{

	public HasUpperFloorPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected HashSet<Node> getRelatedObjects(Node node, ExecutionContext execCxt) {
		HashSet<Node> results = new HashSet<Node>();

		Graph graph = execCxt.getActiveGraph();

		Node clazz = NodeFactory.createURI(Namespace.IFC2X3_TC1 + "IfcBuildingStorey");
		LinkedList<Storey> storeys = new LinkedList<Storey>();
		if (graph.contains(node, RDF.type.asNode(), clazz)) {
			Storey storey = new Storey(node, elevation(node, graph));
			ExtendedIterator<Triple> triples = graph.find(null, RDF.type.asNode(), clazz);
			while (triples.hasNext()) {
				Node subject = triples.next().getSubject();
				Storey s = new Storey(subject, elevation(subject, graph));
				if (s.elevation > storey.elevation) {
					addStorey(storeys, s, graph);
				}
			}
			if (storeys.size() > 0) {
				results.add(storeys.get(0).storey);
			}
		}
		return results;
	}

	@Override
	protected HashSet<Node> getRelatedSubjects(Node node, ExecutionContext execCxt) {
		HashSet<Node> results = new HashSet<Node>();

		Graph graph = execCxt.getActiveGraph();

		Node clazz = NodeFactory.createURI(Namespace.IFC2X3_TC1 + "IfcBuildingStorey");
		LinkedList<Storey> storeys = new LinkedList<Storey>();
		if (graph.contains(node, RDF.type.asNode(), clazz)) {
			Storey storey = new Storey(node, elevation(node, graph));
			ExtendedIterator<Triple> triples = graph.find(null, RDF.type.asNode(), clazz);
			while (triples.hasNext()) {
				Node subject = triples.next().getSubject();
				Storey s = new Storey(subject, elevation(subject, graph));
				if (s.elevation < storey.elevation) {
					addStorey(storeys, s, graph);
				}
			}
			if (storeys.size() > 0) {
				results.add(storeys.get(storeys.size() - 1).storey);
			}
		}
		return results;

	}

	protected void addStorey(LinkedList<Storey> storeys, Storey s, Graph graph) {

		int i = 0;
		for (i = 0; i < storeys.size(); i++) {
			if (i < storeys.size() - 1) {
				if (storeys.get(i).elevation < s.elevation && storeys.get(i + 1).elevation > s.elevation) {
					break;
				}
			}

		}
		storeys.add(i + 1, s);
	}

	private double elevation(Node storey, Graph graph) {
		double elevation;
		AABB aabb = new AABB();
		ExtendedIterator<Triple> iterator2 = graph.find(null,
				NodeFactory.createURI(Namespace.IFC2X3_TC1 + "relatingStructure_fcRelContainedInSpatialStructure"),
				storey);
		while (iterator2.hasNext()) {
			Node rel = iterator2.next().getSubject();
			ExtendedIterator<Triple> iter = graph.find(rel,
					NodeFactory.createURI(Namespace.IFC2X3_TC1 + "relatedElements_fcRelContainedInSpatialStructure"),
					null);
			while (iter.hasNext()) {
				Node element = iter.next().getObject();

				if (graph.contains(element, RDF.type.asNode(), NodeFactory.createURI(Namespace.IFC2X3_TC1 + "IfcSlab"))
						|| graph.contains(element, RDF.type.asNode(),
								NodeFactory.createURI(Namespace.IFC2X3_TC1 + "IfcRoof"))) {
					Geometry geometry = getGeometry(element, graph);
					AABBVisitor visitor = new AABBVisitor();
					geometry.accept(visitor);
					aabb.addBoundingBox(visitor.getAABB());
				}
			}
		}
		elevation = (aabb.getMin().z + aabb.getMax().z) / 2;
		return elevation;
	}

	private class Storey {
		Node storey;
		double elevation;

		Storey(Node storey, double elevation) {
			this.storey = storey;
			this.elevation = elevation;
		}
	}


}
