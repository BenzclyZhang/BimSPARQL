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


import javax.vecmath.Vector3d;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase1;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.Point3d;
import nl.tue.ddss.bimsparql.geometry.algorithm.Normal;

public class normal extends FunctionBase1{

	@Override
	public NodeValue exec(NodeValue v) {
		Geometry g=GFUtils.read(v);
		Vector3d normal;
		try {
			normal = Normal.normal(g);
			Point3d p=new Point3d(normal.x,normal.y,normal.z);
			return GFUtils.write(p);
		} catch (GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvEmptyString;
		}
		
	}

}
