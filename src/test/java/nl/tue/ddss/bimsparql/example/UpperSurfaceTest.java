package nl.tue.ddss.bimsparql.example;

public class UpperSurfaceTest extends QueryFunctionTest{
	
	public static void main(String[] args){		
        String query="SELECT ?window ?g\n" + "WHERE{\n" +"?window a ifcowl:IfcWindow .\n"+"?window pdt:hasLargestSurface ?g .\n"+ "}";
        executeQuery(query);
	}

}
