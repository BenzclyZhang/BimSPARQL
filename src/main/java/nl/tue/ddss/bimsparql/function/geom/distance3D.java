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

import java.io.IOException;

import com.hp.hpl.jena.sparql.expr.NodeValue;
import com.hp.hpl.jena.sparql.function.FunctionBase2;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryException;
import nl.tue.ddss.bimsparql.geometry.algorithm.Distance;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class distance3D extends FunctionBase2{

	@Override
	public NodeValue exec(NodeValue v1, NodeValue v2) {		
		try {
				Geometry g1 = new EwktReader(v1.getString()).readGeometry();
				Geometry g2=new EwktReader(v2.getString()).readGeometry();			
			    return NodeValue.makeDouble(Distance.distance3D(g1, g2));
			} catch (WktParseException | IOException |GeometryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return NodeValue.nvNaN;
		}
	}
	

}
