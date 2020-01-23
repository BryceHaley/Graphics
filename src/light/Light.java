package light;

import geometry.Point3DH;
import windowing.graphics.Color;

public class Light {
	Color intensity;
	Point3DH cameraSpaceLocation;
	double A;
	double B;
	
	public Light(Color color,Point3DH point, double A, double B) {
		this.A = A;
		this.B = B;
		
		intensity = color;
		cameraSpaceLocation = point;
	}
	
	public Point3DH getLocation() {
		return cameraSpaceLocation;
	}
	
	public Color getI() {
		return intensity;
	}
	
	public double[] getAB() {
		double[] arr = {A,B};
		return arr;
	}
	
//	public double[] getLight() {
//		double[] arr = {red, green, blue, A, B};
//		return arr;
//	}
}
