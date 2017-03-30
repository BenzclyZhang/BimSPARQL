package nl.tue.ddss.bimsparql.geometry;

import javax.vecmath.Vector3d;

public class BoxOrientation{
    private	Vector3d n1;
    private	Vector3d n2;
    private	Vector3d n3;

public BoxOrientation(){
	
}

public BoxOrientation(Vector3d n1,Vector3d n2,Vector3d n3){
	this.n1=n1;
	this.n2=n2;
	this.n3=n3;
}
public Vector3d getN1() {
	     return n1;
}
public void setN1(Vector3d n1) {
	this.n1 = n1;
}
public Vector3d getN2() {
	return n2;
}
public void setN2(Vector3d n2) {
	this.n2 = n2;
}
public Vector3d getN3() {
	return n3;
}
public void setN3(Vector3d n3) {
	this.n3 = n3;
}		
}
