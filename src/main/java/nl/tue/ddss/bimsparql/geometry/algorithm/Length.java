package nl.tue.ddss.bimsparql.geometry.algorithm;


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
}
