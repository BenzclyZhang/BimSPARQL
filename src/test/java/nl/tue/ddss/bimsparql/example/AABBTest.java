package nl.tue.ddss.bimsparql.example;

public class AABBTest extends QueryFunctionTest{
	
	public static void main(String[] args){		
        String query="SELECT ?wall ?g\n" + "WHERE{\n" +"?wall a ifcowl:IfcWall .\n"+"?wall pdt:hasAABB ?g .\n"+ "}";
        executeQuery(query);
	}

}
