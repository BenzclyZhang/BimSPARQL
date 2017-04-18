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
import com.hp.hpl.jena.sparql.function.FunctionBase1;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.algorithm.Length;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class length extends FunctionBase1{

	@Override
	public NodeValue exec(NodeValue v) {
		EwktReader er=new EwktReader(v.getString());
		try {
			Geometry geometry=er.readGeometry();
			
			return NodeValue.makeDouble(Length.length(geometry));
		} catch (WktParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		return null;
	}

}
