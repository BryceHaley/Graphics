package geometry;

import polygon.Polygon;

public class Maths {
	
	public static Point3DH triangleNormal(Polygon polygon) {
		Vertex3D p0 = polygon.get(0);
		Vertex3D p1 = polygon.get(1);
		Vertex3D p2 = polygon.get(2);
		return triangleNormal(p0,p1,p2);
	}
	
	public static Point3DH triangleNormal(Vertex3D p0, Vertex3D p1, Vertex3D p2) {
		Point3DH normal;
		double x,y,z, magnitude;
		
		Point3DH u = new Point3DH(p1.getCameraSpaceX() - p0.getCameraSpaceX(), 
				p1.getCameraSpaceY()-p0.getCameraSpaceY(), 
				p1.getCameraSpaceZ() - p0.getCameraSpaceZ());
		Point3DH v = new Point3DH(p2.getCameraSpaceX() - p0.getCameraSpaceX(), 
				p2.getCameraSpaceY()-p0.getCameraSpaceY(), 
				p2.getCameraSpaceZ() - p0.getCameraSpaceZ());
		
		x = (u.getY()*v.getZ()) - (u.getZ()*v.getY());
		y = (u.getZ()*v.getX()) - (u.getX()*v.getZ());
		z = (u.getX()*v.getY()) - (u.getY()*v.getX());
		
		magnitude = Math.sqrt((x*x) + (y*y) + (z*z));
		normal = new Point3DH(x/magnitude,y/magnitude,z/magnitude);
		
//		System.out.println("mag: "+ magnitude);
//		System.out.println("Math'sNormal: " + normal.toString());
		
		return normal;
	}
	
	public static double dotProduct(Vertex3D p0, Vertex3D p1) {
		double dot;
		dot = (p0.getX()*p1.getX()) + (p0.getY()*p1.getY()) + (p0.getZ()*p1.getZ());
		if(dot <0)	 	return 0;
		else 			return dot;
	}
	
	public static double dotProduct(Point3DH p0, Point3DH p1) {
		double dot = (p0.getX()*p1.getX()) + (p0.getY()*p1.getY()) + (p0.getZ()*p1.getZ());
//		System.out.print(">>>");
//		System.out.println(p0.getX());
		if(dot <0)		return 0;
		else 			return dot;
	}
	
	public static double getDistance(double x, double y, double z) {
		double magnitude = Math.sqrt((x*x) +(y*y) + (z*z));
		return magnitude;
	}
	
	public static Vertex3D getMidPoint(Vertex3D p0, Vertex3D p1, Vertex3D p2) {
		Vertex3D midVertex = new Vertex3D();
		midVertex = p0.add(p1).add(p2).scale(1.0/3.0);
		midVertex.replaceColor(p0.getColor());
		
		return midVertex;
	}
	
	public static Point3DH makeUnit(Point3DH point) {
		double x = point.getX();
		double y =  point.getY();
		double z = point.getZ();
		double magnitude = getDistance(x, y, z);
		if(magnitude == 0) {
			return new Point3DH(0,0,0);
		}
		
		point = point.scale(1/magnitude);
		return point;
	}
	
}
