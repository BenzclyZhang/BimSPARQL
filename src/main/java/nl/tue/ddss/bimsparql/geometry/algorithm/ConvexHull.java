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


import java.io.IOException;
import java.util.List;

import com.github.quickhull3d.QuickHull3D;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.Point;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;
import nl.tue.ddss.bimsparql.geometry.visitor.PointsVisitor;

public class ConvexHull {
	
	public PolyhedralSurface buildConvexHull(String wkt) throws WktParseException, IOException {
		EwktReader reader=new EwktReader(wkt);
		Geometry geometry=reader.readGeometry();
	  	  PointsVisitor pv=new PointsVisitor();  	  
	      geometry.accept(pv);
	      List<Point> points=pv.getPoints();
	      return buildConvexHull(points);		
		}
	
	public PolyhedralSurface buildConvexHull(Geometry geometry) {
  	  PointsVisitor pv=new PointsVisitor();  	  
      geometry.accept(pv);
      List<Point> points=pv.getPoints();
      return buildConvexHull(points);		
	}
	
      public PolyhedralSurface buildConvexHull(PolyhedralSurface s){   	 
    	  PointsVisitor pv=new PointsVisitor();  	  
           s.accept(pv);
          List<Point> points=pv.getPoints();
          return buildConvexHull(points);
      }
      
      public PolyhedralSurface buildConvexHull(List<Point> points){
    	  PolyhedralSurface newps=new PolyhedralSurface();
    	  double[] coords=new double[points.size()*3];
          for (int i=0;i<points.size();i++){
        	  coords[i*3]=points.get(i).x();
        	  coords[i*3+1]=points.get(i).y();
        	  coords[i*3+2]=points.get(i).z();
          }
              QuickHull3D hull = new QuickHull3D();
        	  hull.build(coords);
        	int[][] faceIndices= hull.getFaces();
        	com.github.quickhull3d.Point3d[] vertices=  hull.getVertices();
        	for (int i = 0; i < faceIndices.length; i++) {
        		Polygon p=new Polygon();
                for (int k = 0; k < faceIndices[i].length; k++) {
                	com.github.quickhull3d.Point3d pp=vertices[faceIndices[i][k]];
                    p.exteriorRing().addPoint(new Point3d(pp.x,pp.y,pp.z));
                }
                com.github.quickhull3d.Point3d pp=vertices[faceIndices[i][0]];
                p.exteriorRing().addPoint(new Point3d(pp.x,pp.y,pp.z));
               newps.addPolygon(p);
            }
        	 return newps;
    	  
      }



}
