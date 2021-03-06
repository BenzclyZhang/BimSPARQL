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
import java.util.Stack;


import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryCollection;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class Stitching {
	
	final static double EPS=Geometry.EPS;
	
	public List<TriangulatedSurface> stitches(Geometry geometry) throws GeometryException{
		switch (geometry.geometryTypeId()){
		case TYPE_POINT:
		case TYPE_LINESTRING:
		case TYPE_POLYGON:
		case TYPE_TRIANGLE:
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_GEOMETRYCOLLECTION:
			return stitches((GeometryCollection)geometry);
		case TYPE_TRIANGULATEDSURFACE:
			return stitches((TriangulatedSurface)geometry);
		case TYPE_POLYHEDRALSURFACE:
			return stitches((PolyhedralSurface)geometry);
		}

		throw new GeometryException(
				String.format("stitch(%s) is not implemented", geometry.geometryType()));
		}
	
	
	
	public List<TriangulatedSurface> stitches(Geometry geometry,double exact) throws GeometryException{
		switch (geometry.geometryTypeId()){
		case TYPE_POINT:
		case TYPE_LINESTRING:
		case TYPE_POLYGON:
		case TYPE_TRIANGLE:
		case TYPE_MULTIPOINT:
		case TYPE_MULTILINESTRING:
		case TYPE_MULTIPOLYGON:
		case TYPE_GEOMETRYCOLLECTION:
			return stitches((GeometryCollection)geometry,exact);
		case TYPE_TRIANGULATEDSURFACE:
			return stitches((TriangulatedSurface)geometry,exact);
		case TYPE_POLYHEDRALSURFACE:
			return stitches((PolyhedralSurface)geometry,exact);
		}

		throw new GeometryException(
				String.format("stitch(%s) is not implemented", geometry.geometryType()));
		}

	
	public List<TriangulatedSurface> stitches(GeometryCollection gc){
		return stitches(gc,EPS);
	}
	
	public List<TriangulatedSurface> stitches(GeometryCollection gc,double exact){
		List<TriangulatedSurface> surfaces=new ArrayList<TriangulatedSurface>();
		for(int i=0;i<gc.numGeometries();i++){
			Geometry g=gc.geometryN(i);
			if(g.geometryTypeId()==GeometryType.TYPE_TRIANGULATEDSURFACE){
				surfaces.addAll(stitches((TriangulatedSurface)g,exact));
			}else if(g.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
				surfaces.addAll(stitches((PolyhedralSurface)g,exact));
			}else if(g.geometryTypeId()==GeometryType.TYPE_GEOMETRYCOLLECTION){
				surfaces.addAll(stitches((GeometryCollection)g,exact));
			}else{
				
			}
		}
		return surfaces;
	}
	
	public List<TriangulatedSurface> stitches(TriangulatedSurface ts){ 
		return stitches(ts,EPS);
	}
	

	
	public List<TriangulatedSurface> stitches(TriangulatedSurface ts,double exact){				
		Polyhedron polyhedron=new Polyhedron(ts);
		List<List<Integer>> indices=stitches(polyhedron, exact);
		List<Face> faces=polyhedron.getFaces();
		List<TriangulatedSurface> surfaces=new ArrayList<TriangulatedSurface>();
		for (List<Integer> pointers:indices){
			TriangulatedSurface surface=new TriangulatedSurface();
			for(Integer pointer:pointers){
				surface.addTriangles(faces.get(pointer).polygon.asTriangulatedSurface());
			}
			surfaces.add(surface);
		}
		return surfaces;
	}
	
	public List<TriangulatedSurface> stitches(PolyhedralSurface ps){ 
		return stitches(ps,EPS);
	}
    
	public List<TriangulatedSurface> stitches(PolyhedralSurface ps,double exact){
		Polyhedron polyhedron=new Polyhedron(ps);
		List<List<Integer>> indices=stitches(polyhedron, exact);
		List<Face> faces=polyhedron.getFaces();
		List<TriangulatedSurface> surfaces=new ArrayList<TriangulatedSurface>();
		for (List<Integer> pointers:indices){
			TriangulatedSurface surface=new TriangulatedSurface();
			for(Integer pointer:pointers){
				surface.addTriangles(faces.get(pointer).polygon.asTriangulatedSurface());
			}
			surfaces.add(surface);
		}
		return surfaces;
	}
	
	public List<List<Integer>> stitches (Polyhedron polyhedron, double exact){
		List<List<Integer>> indices=new ArrayList<List<Integer>>();
		List<Face> faces=polyhedron.getFaces();
		boolean[] used=new boolean[faces.size()];
		for(int i=0;i<faces.size();i++){
			if(used[i]==true){
				continue;
			}
			List<Integer> integers=new ArrayList<Integer>();
            Stack<Face> stack=new Stack<Face>();
            Face current;
            stack.push(faces.get(i));
			while(!stack.isEmpty()){
				current=stack.pop();
				integers.add(current.index);
				used[current.index]=true;
				for(Face n:current.getNeighbors()){
					if(!used[n.index]){
					double angle=n.getNormal().dot(current.getNormal());
					if(angle>1-exact)
					stack.push(n);
					}
				}
			}
            indices.add(integers);
		}
		return indices;
	}


}
