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


import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryType;
import nl.tue.ddss.bimsparql.geometry.LineString;
import nl.tue.ddss.bimsparql.geometry.Segment;

public class Length {
	
	public static double length(Segment s){
		return s.getLength();
	}
	
	public static double length(LineString ls){
		double result=0;
       for (int i=0;i<ls.numSegments();i++){
    	   result+=ls.segmentN(i).getLength();
       }
       return result;
	}

	public static double length(Geometry geometry) {
		double length=0;
		if(geometry.geometryTypeId()==GeometryType.TYPE_LINESTRING){
			for(int i=0;i<((LineString)geometry).numSegments();i++){
				length=length+length(((LineString)geometry).segmentN(i));
			}
		}
		return length;
	}
}
