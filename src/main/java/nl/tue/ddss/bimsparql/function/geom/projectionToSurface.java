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
package nl.tue.ddss.bimsparql.function.geom;


import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Polygon;
import nl.tue.ddss.bimsparql.geometry.Triangle;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.Plane;
import nl.tue.ddss.bimsparql.geometry.algorithm.Projection;

public class projectionToSurface extends FunctionBase2{

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {
		try {
		Geometry g1=GFUtils.read(v1);
		Geometry g2=GFUtils.read(v2);
	    Geometry projection=null;
		if(g2.geometryTypeId()==GeometryType.TYPE_POLYGON){
			Plane p=((Polygon)g2).getPlane();
		
				projection=Projection.projectToPlane(g1, p);
			
		}else if(g2.geometryTypeId()==GeometryType.TYPE_TRIANGLE){
			Plane p=((Triangle)g2).getPlane();
			projection=Projection.projectToPlane(g1, p);
		}
		if(projection!=null){
			return GFUtils.write(projection);
		}else{
			return NodeValue.nvEmptyString;
		} 
		}catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvEmptyString;
		}
}
}
