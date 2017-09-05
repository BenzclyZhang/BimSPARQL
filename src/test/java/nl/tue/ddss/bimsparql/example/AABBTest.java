package nl.tue.ddss.bimsparql.example;

public class AABBTest extends QueryFunctionTest{
	
	public static void main(String[] args){		
        final String Q="SELECT ?window"
				+ "\n" + "WHERE{\n" +"?window a ifcowl:IfcWall .\n"+"?window spt:isOutside true.\n"
			    + "}";
        executeQuery(Q);
	}
}
