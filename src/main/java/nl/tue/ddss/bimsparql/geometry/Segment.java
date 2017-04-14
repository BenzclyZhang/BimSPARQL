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
package nl.tue.ddss.bimsparql.geometry;

public class Segment extends LineString{
	
	public Point p0;
	public Point p1;
	
	public Segment(){
		
	}

	public Segment(Point p0, Point p1) {
		this.p0=p0;
		this.p1=p1;
	}
	
	

	public double getLength() {
		Point3d p0=this.p0.asPoint3d();
		Point3d p1=this.p1.asPoint3d();
		return Math.sqrt((p0.x-p1.x)*(p0.x-p1.x)+(p0.y-p1.y)*(p0.y-p1.y)+(p0.z-p1.z)*(p0.z-p1.z));
	}
    
    public boolean equals(Segment s){
    	if (this.p0.equals(s.p0)&&this.p1.equals(s.p1)){
    		return true;
    	}else if(this.p0.equals(s.p1)&&this.p1.equals(s.p0)){
    		return true;
    	}else return false;
    }
    
    
    public boolean is3D() {
    	return p0.is3D();
    }

}
