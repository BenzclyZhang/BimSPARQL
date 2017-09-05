package nl.tue.ddss.bimsparql.example;

public class BottomSurfaceTest extends QueryFunctionTest{
	
	public static void main(String[] args){		
        String query="SELECT ?wall ?g\n" + "WHERE{\n" +"?wall a ifcowl:IfcWall .\n"+"?wall schm:isContainedIn ?storey .\n"+"?wall a ifcowl:IfcWall .\n"+"?wall pdt:hasBottomSurface ?g .\n"+ "}";
        executeQuery(query);
	}

}
