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
package nl.tue.ddss.bimsparql.pfunction.pdt;

import java.util.HashMap;

import com.hp.hpl.jena.graph.Node;

import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktWriter;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktWriteException;
import nl.tue.ddss.bimsparql.geometry.visitor.MVBBVisitor;
import nl.tue.ddss.bimsparql.pfunction.FunctionBaseProductEwktValue;

public class HasMVBBPF extends FunctionBaseProductEwktValue{

	public HasMVBBPF(HashMap<Node, Geometry> hashmap) {
		super(hashmap);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected String computeValue(Geometry geometry) {
		MVBBVisitor mv=new MVBBVisitor();
		geometry.accept(mv);
		Box box=mv.getMVBB();
		EwktWriter ew=new EwktWriter("");
		try {
			ew.writeRec(box.toPolyhedralSurface());
			return ew.getString();
		} catch (WktWriteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

}
