package light;

import java.util.ArrayList;
import java.util.List;

import geometry.Maths;
import geometry.Point3DH;
import geometry.Vertex3D;
import windowing.graphics.Color;

public class Lighting {
	private List<Light> lightList = new ArrayList<Light>();
	private Color ambient;
	
	
	public Lighting() {
		
	}
	
	public void setAmbient(Color color) {
		this.ambient = color;
	}
	
	public void addLight(Light light) {
		lightList.add(light);
	}
	
	public Color light(
			Vertex3D cameraSpacePoint,
			Color kDiffuse,
			Point3DH normal,
			double kSpecular,
			double specularExponent) 
	{
		Color newColor = ambient.multiply(kDiffuse);
		double surfaceX = cameraSpacePoint.getCameraSpaceX();//1.5476347651860107;
		double surfaceY = cameraSpacePoint.getCameraSpaceY();//-14.913043478260917;;
		double surfaceZ = cameraSpacePoint.getCameraSpaceZ();//-28.126155740732994;;
		
//		double surfaceX = -2.6023365383982995;
//		double surfaceY = -14.81966866857862;
//		double surfaceZ = -37.025797921185614;
		
//		System.out.println("specular: " + kSpecular);
//		System.out.println("spec exp: " + specularExponent);
//		System.out.println("ambient: "+ ambient);
//		System.out.println("kDiffuse below: ");
//		kDiffuse.print();
		
		Color specular = new Color(kSpecular, kSpecular, kSpecular);
		
		double magnitude = Maths.getDistance(surfaceX, surfaceY, surfaceZ);
		//System.out.println("Lighting CSP: "+ cameraSpacePoint.getCameraSpaceBuffer().toString());
//		System.out.println("lighting CSP: "+ surfaceX+", " + surfaceY+", " +surfaceZ);
//		System.out.println("lighting Norms: "+ normal.toString());
		//System.out.print("point: "+ cameraSpacePoint.toString());
		Point3DH viewVector = new Point3DH(-surfaceX/magnitude, -surfaceY/magnitude, -surfaceZ/magnitude);
//		System.out.print(">>>");
//		System.out.println(magnitude);
		
		Color diffComponent= Color.BLACK;
		Color specComponent=Color.BLACK;
		Color lightI;
		double[] arrAB;
		Point3DH lightPosn;
		double d;
		double fatso;
		Point3DH lHat;
		Point3DH reflection;
		
		//ambient.print();
		for (Light light : lightList) {
			lightPosn = light.getLocation();
			lightI = light.getI();
			arrAB = light.getAB();
			d = Maths.getDistance(surfaceX-lightPosn.getX(),surfaceY-lightPosn.getY(),surfaceZ-lightPosn.getZ());
			fatso = 1/(arrAB[0]+(arrAB[1]*d));
			
			lHat = new Point3DH(lightPosn.getX()-surfaceX, lightPosn.getY()-surfaceY, lightPosn.getZ()-surfaceZ);
			lHat = Maths.makeUnit(lHat);
			
			diffComponent=lightI.multiply(kDiffuse.scale(Maths.dotProduct(lHat, normal)).scale(fatso));
		//	System.out.println("diff component: "+ diffComponent.toString());
			
			newColor = newColor.add(diffComponent);
//			System.out.print(">>>");
//			newColor.print();
			
			reflection = normal.scale(2*Maths.dotProduct(normal, lHat)).subtract(lHat);
			reflection = Maths.makeUnit(reflection);
			
			specComponent= specular.scale(Math.pow(Maths.dotProduct(viewVector, reflection),specularExponent));
//			System.out.print(">>>");
//			System.out.println(Math.pow(Maths.dotProduct(viewVector, reflection),specularExponent));
//			System.out.println("specular component: " + specComponent.toString());
			//specComponent.print();
			
			newColor = newColor.add(specComponent);

		}
		
		return newColor;
	}
	//testing
}
