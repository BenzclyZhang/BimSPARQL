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
import java.util.List;

public class Edge {
	
	List<Face> faces=new ArrayList<Face>();
	List<HalfEdge> halfEdges=new ArrayList<HalfEdge>();
	

	
	

	public List<Face> getNFaces() {
		// TODO Auto-generated method stub
		return faces;
	}
	
	public boolean hasVertex(Vertex vertex){
		return halfEdges.get(0).start==vertex||halfEdges.get(0).end==vertex;
	}



	public Vertex getVertex(int i) {
		// TODO Auto-generated method stub
		if(i==0){
			return halfEdges.get(0).start;
		}else if(i==1){
			return halfEdges.get(0).end;
		}
		return null;
	}
	
	
	public Vertex anotherVertex(Vertex vertex){
		if (halfEdges.get(0).start==vertex) return halfEdges.get(0).end;
		else if(halfEdges.get(0).end==vertex) return halfEdges.get(0).start;
		else return null;
	}



	
	

}
