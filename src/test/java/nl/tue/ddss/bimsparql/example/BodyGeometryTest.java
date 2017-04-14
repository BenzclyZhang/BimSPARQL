package nl.tue.ddss.bimsparql.example;


public class BodyGeometryTest extends QueryFunctionTest{
	
	
	
	public static void main(String[] args){		
         String query="SELECT ?wall ?g\n" + "WHERE{\n" +"?wall a ifcowl:IfcProduct .\n"+"?wall pdt:hasBodyGeometry ?g .\n"+ "}";
         executeQuery(query);
	}

}
