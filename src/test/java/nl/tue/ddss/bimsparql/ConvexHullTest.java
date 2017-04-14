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
package nl.tue.ddss.bimsparql;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.PolyhedralSurface;
import nl.tue.ddss.bimsparql.geometry.algorithm.ConvexHull;
import nl.tue.ddss.bimsparql.geometry.ewkt.EwktReader;
import nl.tue.ddss.bimsparql.geometry.ewkt.WktParseException;

public class ConvexHullTest {
	
	public static void main(String[] args) throws IOException, WktParseException{
		InputStream in=ConvexHullTest.class.getClassLoader().getResourceAsStream("lifeline_geometry.ttl");
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		String line ;
		while ((line= br.readLine()) != null) {
		if (line.contains("geom:asWKT")){
			String ewkt=line.substring(line.indexOf("\"")+1,line.lastIndexOf("\""));
			EwktReader reader=new EwktReader(ewkt);
			Geometry geometry=reader.readGeometry();
			if (geometry.geometryTypeId()==GeometryType.TYPE_POLYHEDRALSURFACE){
			@SuppressWarnings("unused")
			PolyhedralSurface ps=new ConvexHull().buildConvexHull((PolyhedralSurface)geometry);
		}
		
	}
	}
}
}
