package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class AntialiasingLineRenderer implements LineRenderer {
	
	private AntialiasingLineRenderer() {}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double y = p1.getIntY();
		double slope = deltaY / deltaX;
		double b = y-(slope*p1.getIntX());
		int argbColor = p1.getColor().asARGB();
		int yprime;
		
		int newARGB;
		
		double dist=0;//distance from y+-1 pixel to unit width bar around line
		double r = Math.sqrt(2);// radius of circle centered on y+/-1 pixel 
		double ratio; // ratio or circle covered 
		int delta[]= {1,-1,2,-2};  
		
		//loop through all values in the line setting points that DDA would set and scaling colour
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			yprime = (int)Math.round(y);
			dist = getDistance(x,yprime, b, slope, 0);
			ratio = (r-dist)/r;
			newARGB= getARGB(ratio,x,yprime,drawable, argbColor);
			drawable.setPixel(x, yprime, 0.0, newARGB);
			// look two pixels up and down and set those (scaled)
			for(int i =0; i< delta.length; i++) {
				dist = getDistance(x, yprime, b, slope, delta[i]);
				if (dist < r) {
					ratio = getRatio(r, dist);
					newARGB = getARGB(ratio, x, yprime+delta[i], drawable, argbColor);
					drawable.setPixel(x, yprime + delta[i],0.0, newARGB);
				}
			}
			y += slope;//iterate slope
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new AntialiasingLineRenderer());
	}
	
	public double getDistance(int x, int y,double b, double slope, int delta) {
		double distance;
		double d;
		y+= delta;
		if (delta>0) {
			d = 0.5;
		} else if(delta <0) {
			d = -0.5;
		} else {
			d=0;
		}
		distance= Math.abs(slope*(double)x-(double)y+(b+d))/(Math.sqrt(slope*slope +1));
		return distance;
	}
	
	public double getRatio(double r, double dist) {
		double theta = Math.acos(dist/r);
		double ratio = 1 - (((1-(theta/Math.PI))*Math.PI*r*r)+(dist*Math.sqrt((r*r)-(dist*dist))))/(Math.PI*r*r);
		return ratio;
	}
	//set new colour as brightest of old or new pixel colour 
	public int getARGB(double ratio, int x, int y, Drawable drawable, int argbColor) {
		int oldARGB;
		int newARGB;
		double valueOld;
		double valueNew;
		Color newColor = Color.fromARGB(argbColor);
		Color oldColor;
		
		newColor = newColor.scale(ratio);
		newColor= newColor.clamp();
		oldARGB= drawable.getPixel(x,y);
		oldColor = Color.fromARGB(oldARGB);
		valueOld = oldColor.getR() + oldColor.getB() + oldColor.getG();
		valueNew = newColor.getR() + newColor.getB() + newColor.getG();
		newARGB=newColor.asARGB();
		if(valueOld > valueNew) {
			newARGB = oldARGB;
		}
		return newARGB;
	}
}