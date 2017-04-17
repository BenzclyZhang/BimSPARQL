package nl.tue.ddss.bimsparql.example;

public class MVBBTest extends QueryFunctionTest{
	
	public static void main(String[] args){		
        String query="SELECT ?wall ?g\n" + "WHERE{\n" +"?wall a ifcowl:IfcWindow .\n"+"?wall pdt:hasMVBB ?g .\n"+ "}";
        executeQuery(query);
	}

}
