package nl.tue.ddss.bimsparql.geometry.algorithm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.TriangulatedSurface;

public class Stitching {
	
	final static double EPS=Geometry.EPS;
	
	
	public List<Polygon> stitches(TriangulatedSurface ts){
		HashMap<Vector3d,List<Triangle>> normMap=new HashMap<Vector3d,List<Triangle>>();
		List<Triangle>	triangles=ts.getTriangles();
		for (Triangle triangle:triangles){
			Vector3d normal=Normal.normal(triangle);
			normMap.get(normal);
		}
		return null;
	}
	
	public List<List<Integer>> stitches(PolyhedralSurface ps){ 
		return stitches(ps,EPS);
	}
    
	public List<List<Integer>> stitches(PolyhedralSurface ps,double exact){
		List<List<Integer>> indices=new ArrayList<List<Integer>>();
		Polyhedron polyhedron=new Polyhedron(ps);
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
			Face f=faces.get(i);
			used[f.index]=true;
			while(!stack.isEmpty()){
				current=stack.pop();
				integers.add(current.index);
				used[current.index]=true;
				for(Face n:current.getNeighbors()){
					double angle=n.getNormal().dot(current.getNormal());
					if(!used[n.index]&&angle>1-exact)
					stack.push(n);
				}
			}
            indices.add(integers);
		}
		return indices;
	}


}
