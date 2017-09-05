package nl.tue.ddss.bimsparql.geometry.convert;



import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.media.j3d.Geometry;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;

import com.hp.hpl.jena.rdf.model.Model;

import nl.tue.ddss.bimsparql.example.BodyGeometryTest;
import nl.tue.ddss.bimsparql.geometry.GeometryGenerator;
import nl.tue.ddss.bimsparql.geometry.InstanceGeometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.AABB;
import unbboolean.j3dbool.BooleanModeller;
import unbboolean.j3dbool.Solid;

public class BooleanTest {
     public static void main(String[] args){
    	
  /* 	 Point3d[] points={new Point3d(0,0,0),new Point3d(2,0,0),new Point3d(2,2,0),new Point3d(0,2,0),new Point3d(0,0,2),new Point3d(2,0,2),new Point3d(2,2,2),new Point3d(0,2,2)};
    	 Point3d[] points2={new Point3d(1,1,4),new Point3d(3,1,4),new Point3d(3,3,4),new Point3d(1,3,4),new Point3d(1,1,6),new Point3d(3,1,6),new Point3d(3,3,6),new Point3d(1,3,6)};
    	
    	 int[] indices={0,1,4,4,1,5,5,1,6,1,2,6,2,1,0,2,0,3,7,4,5,7,5,6,7,0,4,7,3,0,2,3,7,2,7,6};
    	 Color3f color=new Color3f(0,0,0);
    	 Color3f[] colors={color,color,color,color,color,color,color,color};
    	 Solid s=new Solid(points,indices,colors);
    	 Solid s2=new Solid(points2,indices,colors);
    	 List<Solid> solids=new ArrayList<Solid>();
    	 solids.add(s);
    	 solids.add(s2);
    	 Solid union=union(solids);
    	 Geometry geometry=union.getGeometry();
         System.out.println("");*/
  	 InputStream input = BooleanTest.class.getClassLoader()
 				.getResourceAsStream("Duplex_A_20110505.ifc");
 		GeometryGenerator gg=new GeometryGenerator();
 		HashMap<Integer,InstanceGeometry> maps=gg.generateGeometry(input, "www.tue.nl/");
 		List<Solid> solids=new ArrayList<Solid>();
 		for(InstanceGeometry g:maps.values()){
 			if(g.getType().equals("IfcWall")||g.getType().equals("IfcWallStandardCase")){
 			Point3d[] points=new Point3d[g.getPoints().length/3];
 			AABB aabb=new AABB();
 			for(int i=0;i<points.length;i++){
 				aabb.addPoint3d(new Point3d(g.getPoints()[i*3],g.getPoints()[i*3+1],g.getPoints()[i*3+2]));
 			}
 			solids.add(aabb.toJ3DSolid());
 			}
 		}
 		Solid union=union(solids);
 		System.out.println(union.getVertices());
 		
     }
     
     public static Solid union(List<Solid> solids){
    	 int i=1;
    	 Solid solid=null;
    	 if(solids!=null&&solids.size()>0){
    		 if(solids.size()==1){
    			 return solids.get(0);
    		 }else{
    			 BooleanModeller bm;
    	  	 while(i<solids.size()){
    	  		 if(solid==null){
    	  		bm=new BooleanModeller(solids.get(i-1),solids.get(i));
    	  		solid=bm.getUnion();
    	  		 }else{
    	  			 bm=new BooleanModeller(solid,solids.get(i));
    	  			 solid=bm.getUnion();
    	  		 }
    	  		 i++;
        	 }
    		 }
    	 }return solid;
  
     }
}
