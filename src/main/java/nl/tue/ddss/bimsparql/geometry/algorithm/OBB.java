package nl.tue.ddss.bimsparql.geometry.algorithm;


import java.util.ArrayList;
import java.util.List;

import javax.vecmath.Matrix3d;
import javax.vecmath.Vector3d;

import nl.tue.ddss.bimsparql.geometry.Box;
import nl.tue.ddss.bimsparql.geometry.BoxOrientation;
import nl.tue.ddss.bimsparql.geometry.Geometry;
import nl.tue.ddss.bimsparql.geometry.GeometryUtils;
import nl.tue.ddss.bimsparql.geometry.Point3d;


public class OBB {
	
	static final double EPS=Geometry.EPS;
	
	double volume;
	Box box;
	BoxOrientation bo;
	
	public Polyhedron convexHull;
	
    public OBB(Polyhedron convexHull){
    	this.convexHull=convexHull;
    }
	
	public void computeMinBB(){		
		for (Edge e1:convexHull.getEdges()){
			List<Edge> edges=findSidePodalEdges(e1);
			if(edges.size()>0){
				for (Edge e2:edges){
					List<Edge> edges2=findSidePodalEdges(e1,e2,edges);
					if(edges2.size()>0){
						for (Edge e3:edges2){
							List<BoxOrientation> bos=new ArrayList<BoxOrientation>();
							int numBo=computeBasis(e1,e2,e3,bos);
							if (numBo>0){
								for (BoxOrientation bo:bos){
								Box box=computeOBB(bo);
								recordOBB(box);
								}
							}
						}
					}
					List<Edge> edges3=findAntiPodalEdges(e1);
					if(edges3.size()>0){
						for (Edge e3:edges3){
							Vector3d n=antiPodalDirection(e1, e3);
							BoxOrientation bo=completeBasis(n,e2);
							if(bo!=null){
								Box box=computeOBB(bo);
								recordOBB(box);
							}
						}
					}
					
				}
			}
			
		}
		
		for (Face f:convexHull.getFaces()){
			Edge e1=f.getEdge(0);
			Vector3d n=f.getNormal();
			List<Edge> edges=findSidePodalEdges(e1);
			if(edges.size()>0){
				for (Edge e3:edges){
					BoxOrientation bo=completeBasis(n,e3);
					if (bo!=null){
						Box box=computeOBB(bo);
						recordOBB(box);
					}
				}
			}
		}
		
	}
	
	
	private BoxOrientation completeBasis(Vector3d n1, Edge e) {
		if (n1==null){
			return null;
		}
		Vector3d v1=e.getNFaces().get(0).getNormal();
		Vector3d v2=e.getNFaces().get(1).getNormal();
		double p=n1.dot(v1);
		double q=n1.dot(GeometryUtils.vectorSubtract(v1, v2));
		if (!epsEqual(q,0)){
			double u=p/q;
			Vector3d n2=GeometryUtils.vectorAdd(v1,GeometryUtils.vectorMul(GeometryUtils.vectorSubtract(v2, v1), u));
			n2.normalize();
			Vector3d n3=GeometryUtils.vectorCross(n1, n2);
			return new BoxOrientation(n1,n2,n3);
		}
		else if (epsEqual(p,0)){
			Vector3d n2=v1;
			Vector3d n3=GeometryUtils.vectorCross(n1, n2);
			return new BoxOrientation(n1,n2,n3);
		}
		return null;
	}
	
	private boolean epsEqual(double d1,double d2){
		return d1>d2-EPS&&d1<d2+EPS;
	}


	private List<Edge> findAntiPodalEdges(Edge e) {
		List<Edge> edges=new ArrayList<Edge>();
		for (Edge e1:convexHull.getEdges()){
			if (areAntiPodal(e,e1)){
				edges.add(e1);
			}
		}
		return edges;
	}


	private void recordOBB(Box box) {
		double volume=box.getVolume();
		if (this.box==null){
			this.box=box;
		}
		else if (volume<this.box.getVolume()){
			
			this.box=box;
		}
		
	}


	private Box computeOBB(BoxOrientation bo){
		Box box=new Box();
		box.setOrientation(bo);
		AABB bbox=new AABB();
//		HashMap<Point3d,Point3d> pointMap=new HashMap<Point3d,Point3d>();
		for (Vertex vertex:convexHull.getVertices()){
			Point3d p=transform(vertex.pnt,bo);
			bbox.addPoint3d(p);
//			pointMap.put(p, vertex.pnt);
		}
//		double volume=bbox.getVolume()/1000000000;
            box.setMin(transBack(bbox.min,bo));
            box.setMax(transBack(bbox.max,bo));
		return box;
	}
	
	private Point3d transBack(Point3d point,BoxOrientation bo){
		Vector3d n1=GeometryUtils.vectorAdd(new Vector3d(1,0,0), transform(GeometryUtils.vectorSubtract(new Vector3d(1,0,0), bo.getN1()),bo));
		Vector3d n2=GeometryUtils.vectorAdd(new Vector3d(0,1,0), transform(GeometryUtils.vectorSubtract(new Vector3d(0,1,0), bo.getN2()),bo));
		Vector3d n3=GeometryUtils.vectorAdd(new Vector3d(0,0,1), transform(GeometryUtils.vectorSubtract(new Vector3d(0,0,1), bo.getN3()),bo));
		Vector3d v=point.asVector3d();
		return new Point3d(v.dot(n1),v.dot(n2),v.dot(n3));
	}
	
	private Point3d transform(Point3d point, BoxOrientation bo){
		Vector3d v=point.asVector3d();
		return new Point3d(v.dot(bo.getN1()),v.dot(bo.getN2()),v.dot(bo.getN3()));
	}
	
	private Vector3d transform(Vector3d v, BoxOrientation bo){
		return new Vector3d(v.dot(bo.getN1()),v.dot(bo.getN2()),v.dot(bo.getN3()));
	}
	
	private boolean areAntiPodal(Vertex v, Edge e){
		double t1=0;
		double t2=1;
		for (Vertex w:convexHull.neighborVertices(v)){
		Vector3d v1=e.getNFaces().get(0).getNormal();
		Vector3d v2=e.getNFaces().get(1).getNormal();
		double p=v1.dot(GeometryUtils.vectorSubtract(w.pnt, v.pnt));
		double q=GeometryUtils.vectorSubtract(v1, v2).dot(GeometryUtils.vectorSubtract(w.pnt, v.pnt));
		if (q>0){
			t2=Double.min(t2,p/q);
		}else if (q<0){
			t1=Double.max(t1, p/q);
		} else if (p<0){
			return false;
		}
		
	}
		return t1<=t2;
}
	
	private boolean areAntiPodal(Edge e1,Edge e2){
		return areAntiPodal(e1.getVertex(0), e2)&&areAntiPodal(e1.getVertex(1), e2);
	}
	
	private Vector3d antiPodalDirection(Edge e1,Edge e2){
		Vector3d e1v1=e1.getNFaces().get(0).getNormal();
		Vector3d e1v2=e1.getNFaces().get(1).getNormal();
		Vector3d e2v1=e2.getNFaces().get(0).getNormal();
		Vector3d e2v2=e2.getNFaces().get(1).getNormal();
		Matrix3d m=new Matrix3d();
		m.setColumn(0, GeometryUtils.vectorSubtract(e1v1, e1v2));
		m.setColumn(1, e2v1);
		m.setColumn(2, GeometryUtils.vectorSubtract(e2v2, e2v1));
		try{
		m.invert();
		}catch (Exception e){
		return null;
		}
		double t=m.m00*e1v1.x+m.m01*e1v1.y+m.m02*e1v1.z;
		double c=m.m10*e1v1.x+m.m11*e1v1.y+m.m12*e1v1.z;
		double cu=m.m20*e1v1.x+m.m21*e1v1.y+m.m22*e1v1.z;
		double u=cu/c;
		if (c<0&&t>=0-EPS&&t<=1+EPS&&u>=0-EPS&&u<=1+EPS){
			Vector3d n=new Vector3d();
			Vector3d vv=GeometryUtils.vectorSubtract(e1v2, e1v1);
			vv.scale(t);
			n.add(e1v1,vv);
			n.normalize();
			return n;
		}
		return null;
	}
	
	
	private List<Edge> findSidePodalEdges(Edge e1,Edge e2, List<Edge> edges){
		List<Edge> edges2=new ArrayList<Edge>();
		for (Edge e3:edges){
			if (areSidePodal(e1,e3)&&areSidePodal(e2,e3)){
				edges2.add(e3);
			}
		}
		return edges2;
	}
	
	private List<Edge> findSidePodalEdges(Edge e){
		List<Edge> edges=new ArrayList<Edge>();
		for (Edge e1:convexHull.getEdges()){
			if (areSidePodal(e,e1)){
				edges.add(e1);
			}
		}
		return edges;
	}
	
	private boolean areSidePodal(Edge e1,Edge e2){
		Vector3d e1v1=e1.getNFaces().get(0).getNormal();
		Vector3d e1v2=e1.getNFaces().get(1).getNormal();
		Vector3d e2v1=e2.getNFaces().get(0).getNormal();
		Vector3d e2v2=e2.getNFaces().get(1).getNormal();
		double a=e1v2.dot(e2v2);
		double b=GeometryUtils.vectorSubtract(e1v1, e1v2).dot(e2v2);
		double c=GeometryUtils.vectorSubtract(e2v1, e2v2).dot(e1v2);
		double d=GeometryUtils.vectorSubtract(e1v1, e1v2).dot(GeometryUtils.vectorSubtract(e2v1, e2v2));
		double v1=Double.min(Double.min(Double.min(a, a+b), a+c), a+b+c+d);
		double v2=Double.max(Double.max(Double.max(a, a+b), a+c), a+b+c+d);
		return v1<=0+EPS&&v2>=0-EPS;
	}
	
	private int computeBasis (Edge e1,Edge e2,Edge e3,List<BoxOrientation> basis){
		Vector3d e1v1=e1.getNFaces().get(0).getNormal();
		Vector3d e1v2=e1.getNFaces().get(1).getNormal();
		Vector3d e2v1=e2.getNFaces().get(0).getNormal();
		Vector3d e2v2=e2.getNFaces().get(1).getNormal();
		Vector3d e3v1=e3.getNFaces().get(0).getNormal();
		Vector3d e3v2=e3.getNFaces().get(1).getNormal();
		return computeBasis(e1v1,e1v2,e2v1,e2v2,e3v1,e3v2,basis);
	}
	
	
	
	private int computeBasis(Vector3d f1a, Vector3d f1b,
			Vector3d f2a, Vector3d f2b,
			Vector3d f3a, Vector3d f3b, List<BoxOrientation> basis)
		{
			double angleEps = 1e-3f;

			{
				Vector3d a = f1b;
				Vector3d b = GeometryUtils.vectorSubtract(f1a,f1b);
				Vector3d c = f2b;
				Vector3d d = GeometryUtils.vectorSubtract(f2a,f2b);
				Vector3d e = f3b;
				Vector3d f = GeometryUtils.vectorSubtract(f3a,f3b);

				double g = a.dot(c)*d.dot(e) - a.dot(d)*c.dot(e);
				double h = a.dot(c)*d.dot(f) - a.dot(d)*c.dot(f);
				double i = b.dot(c)*d.dot(e) - b.dot(d)*c.dot(e);
				double j = b.dot(c)*d.dot(f) - b.dot(d)*c.dot(f);

				double k = g*b.dot(e) - a.dot(e)*i;
				double l = h*b.dot(e) + g*b.dot(f) - a.dot(f)*i - a.dot(e)*j;
				double m = h*b.dot(f) - a.dot(f)*j;

				double s = l*l - 4*m*k;

				if (Math.abs(m) < 1e-5f ||Math.abs(s) < 1e-5f)
				{
					// The equation is linear instead.

					double v = -k / l;
					double t = -(g + h*v) / (i + j*v);
					double u = -(c.dot(e) + c.dot(f)*v) / (d.dot(e) + d.dot(f)*v);
					// If we happened to divide by zero above, the following checks handle them.
					int nSolutions=0;
					if (v >= -EPS && t >= -EPS && u >= -EPS && v <= 1.f + EPS && t <= 1.f + EPS && u <= 1.f + EPS)
					{   
						
						Vector3d n1=GeometryUtils.vectorAdd(a , GeometryUtils.vectorMul(b, t));
						n1.normalize();
						Vector3d n2=GeometryUtils.vectorAdd(c , GeometryUtils.vectorMul(d, u));
						n2.normalize();
						Vector3d n3=GeometryUtils.vectorAdd(e , GeometryUtils.vectorMul(f, v));
						n3.normalize();
						
						if (Math.abs(n1.dot(n2)) < angleEps
							&& Math.abs(n1.dot(n3)) < angleEps
							&& Math.abs(n2.dot(n3)) < angleEps){
							BoxOrientation bo=new BoxOrientation();
							bo.setN1(n1);
							bo.setN2(n2);
							bo.setN3(n3);
							basis.add(bo);
							nSolutions++;
							return nSolutions;
						}
						else
							return nSolutions;
					}
					return nSolutions;
				}

				if (s < 0.f)
					return 0; // Discriminant negative, no solutions for v.

				double sgnL = l < 0 ? -1.f : 1.f;
				double V1 = -(l + sgnL*Math.sqrt(s))/ (2.f*m);
				double V2 = k / (m*V1);

				double T1 = -(g + h*V1) / (i + j*V1);
				double T2 = -(g + h*V2) / (i + j*V2);

				double U1 = -(c.dot(e) + c.dot(f)*V1) / (d.dot(e) + d.dot(f)*V1);
				double U2 = -(c.dot(e) + c.dot(f)*V2) / (d.dot(e) + d.dot(f)*V2);

				int nSolutions = 0;
				if (V1 >= -EPS && T1 >= -EPS && U1 >= -EPS && V1 <= 1.f + EPS && T1 <= 1.f + EPS && U1 <= 1.f + EPS)
				{   
					Vector3d n1=GeometryUtils.vectorAdd(a , GeometryUtils.vectorMul(b, T1));
					n1.normalize();
				Vector3d n2=GeometryUtils.vectorAdd(c , GeometryUtils.vectorMul(d, U1));
				n2.normalize();
				Vector3d n3=GeometryUtils.vectorAdd(e , GeometryUtils.vectorMul(f, V1));
				n3.normalize();
				
					if (Math.abs(n1.dot(n2)) < angleEps
						&& Math.abs(n1.dot(n3)) < angleEps
						&& Math.abs(n2.dot(n3)) < angleEps){
						BoxOrientation bo=new BoxOrientation();
					bo.setN1(n1);
					bo.setN2(n2);
					bo.setN3(n3);
					basis.add(bo);
					nSolutions++;
					}
				}
				if (V2 >= -EPS && T2 >= -EPS && U2 >= -EPS && V2 <= 1.f + EPS && T2 <= 1.f + EPS && U2 <= 1.f + EPS)
				{
					Vector3d n1=GeometryUtils.vectorAdd(a , GeometryUtils.vectorMul(b, T2));
					n1.normalize();
				Vector3d n2=GeometryUtils.vectorAdd(c , GeometryUtils.vectorMul(d, U2));
				n2.normalize();
				Vector3d n3=GeometryUtils.vectorAdd(e , GeometryUtils.vectorMul(f, V2));
				n3.normalize();
			
					if (Math.abs(n1.dot(n2)) < angleEps
						&& Math.abs(n1.dot(n3)) < angleEps
						&& Math.abs(n2.dot(n3)) < angleEps){
						BoxOrientation bo=new BoxOrientation();
					bo.setN1(n1);
					bo.setN2(n2);
					bo.setN3(n3);
					basis.add(bo);
					nSolutions++;
					}
				}
				if (s < 1e-4f && nSolutions == 2)
					 nSolutions = 1;
						
				return nSolutions;
			}
		}

	public Box getBox() {
		// TODO Auto-generated method stub
		return box;
	}
	
	

	
	
	
	
}
