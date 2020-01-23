package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineWithColorLERP implements LineRenderer {
	// use the static factory make() instead of constructor.
	private DDALineWithColorLERP() {}

	
	/*
	 * (non-Javadoc)
	 * @see client.LineRenderer#drawLine(client.Vertex2D, client.Vertex2D, windowing.Drawable)
	 * 
	 * @pre: p2.x >= p1.x && p2.y >= p1.y
	 */
	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		double[] RGBp1 = getRGBArray(p1);
		double[] RGBp2 = getRGBArray(p2);
		int argbColor;
		
		double deltaX = p2.getIntX() - p1.getIntX();
		double deltaY = p2.getIntY() - p1.getIntY();
		double deltaZ = p2.getIntZ() - p1.getIntZ();
		
		double y = p1.getIntY();
		double z = p1.getIntZ();
		
		double slope = deltaY / deltaX;
		double zSlope = deltaZ / deltaX;
		
		double[] mRGB = getRGBSlope(RGBp1,RGBp2, deltaX);
		
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			argbColor = Color.makeARGB((int)Math.round(RGBp1[0]), (int)Math.round(RGBp1[1]),
					(int)Math.round(RGBp1[2]));
			
			drawable.setPixel(x, (int)Math.round(y), (int)Math.round(z), argbColor);
			y += slope;
			z += zSlope;
			
			for(int i=0; i<3; i++) {
				RGBp1[i] += mRGB[i];
			}
		}
	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new DDALineWithColorLERP());
	}
	
	private double[] getRGBSlope(double[] p1RGB, double[] p2RGB, double deltaX) {
		double[] mRGB = new double[3];
		for(int i=0; i<3;i++) {
			mRGB[i] = ((p2RGB[i]-p1RGB[i])/deltaX); 
		}
		
		return mRGB;
	}
	
	private double[] getRGBArray(Vertex3D p) {
		double[] pointRGB = new double[3];
		pointRGB[0] = (double)p.getColor().getIntR();
		pointRGB[1] = (double)p.getColor().getIntG();
		pointRGB[2] = (double)p.getColor().getIntB();
		return pointRGB;
	}
	
	
}


