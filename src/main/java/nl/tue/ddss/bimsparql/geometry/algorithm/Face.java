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
package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Polygon;

public class Face {
	
	List<HalfEdge> halfEdges=new LinkedList<HalfEdge>();
	List<Vertex> vertices=new LinkedList<Vertex>();
	Polygon polygon;
	int index;
	
	public Face(Polygon polygon, int index) {
		this.polygon=polygon;
		this.index=index;		
	}

	public Vector3d getNormal() {
		return polygon.getNormal();
	}


	public Edge getEdge(int i) {
		// TODO Auto-generated method stub
		return halfEdges.get(i).getEdge();
	}
	
	public List<Face> getNeighbors(){
		List<Face> faces=new ArrayList<Face>();
		for(HalfEdge he:halfEdges){
			Edge e=he.getEdge();
			List<Face> fs=e.getNFaces();
			for (Face f:fs){
				if(f!=this){
					faces.add(f);
				}
			}
		}
		return faces;
	}

	public void addHalfEdge(HalfEdge hf) {
		halfEdges.add(hf);
		
	}

	public void addVertex(Vertex v) {
		vertices.add(v);
		
	}

}
