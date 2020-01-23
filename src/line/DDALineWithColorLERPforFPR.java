package line;

import geometry.Vertex;
import geometry.Vertex3D;
import polygon.Polygon;
import shading.PixelShader;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class DDALineWithColorLERPforFPR implements LineRendererWithPoly {
	// use the static factory make() instead of constructor.
	private DDALineWithColorLERPforFPR() {}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable, Polygon polygon, 
			PixelShader pixelShader) {
		
		int argbColor;
		Color color;
		Vertex3D vertex;
		
		double deltaX = p2.getX() - p1.getX();
		double deltaY = p2.getY() - p1.getY();
		double deltaZ =  1/p2.getZ() - 1/p1.getZ();
		
		double y = p1.getY();

		double mY = deltaY / deltaX;
		double mZ = deltaZ / deltaX;
		
		//perspective friendly
		double[] RGBp1p = getRGBpArray(p1);
		double[] RGBp2p = getRGBpArray(p2);
	
		double z = 1/p1.getZ();
		
		double norm1X = p1.getNormal().getX();
		double norm1Y = p1.getNormal().getY();
		double norm1Z = p1.getNormal().getZ();
		double norm2X = p2.getNormal().getX();
		double norm2Y = p2.getNormal().getY();
		double norm2Z = p2.getNormal().getZ();
		//System.out.println("DDA p1N: "+p1.getNormal().toString());
		//System.out.println("DDA p2N: "+p2.getNormal().toString());
		
		double deltaNormX = norm2X/p2.getZ() - norm1X/p1.getZ();
		double deltaNormY = norm2Y/p2.getZ() - norm1Y/p1.getZ();
		double deltaNormZ = norm2Z/p2.getZ() - norm1Z/p1.getZ();
		
		double normX = norm1X/p1.getZ();
		double normY= norm1Y/p1.getZ();
		double normZ = norm1Z/p1.getZ();
		
		double csX = p1.getCameraSpaceX()/p1.getZ();
		double csY = p1.getCameraSpaceY()/p1.getZ();
		double csZ = 1/p1.getCameraSpaceZ();
		
		double deltaCSX = p2.getCameraSpaceX()/p2.getZ() - p1.getCameraSpaceX()/p1.getZ();
		double deltaCSY = p2.getCameraSpaceY()/p2.getZ() - p1.getCameraSpaceY()/p1.getZ();
		double deltaCSZ = 1/p2.getCameraSpaceZ() - 1/p1.getCameraSpaceZ();
		
		double mCSX = deltaCSX/deltaX;
		double mCSY = deltaCSY/deltaX;
		double mCSZ = deltaCSZ/deltaX;
		
		double mNormX =deltaNormX/deltaX;
		double mNormY =deltaNormY/deltaX;
		double mNormZ =deltaNormZ/deltaX;
		
		double[] mRGBp = getRGBSlope(RGBp1p,RGBp2p, deltaX);
		//System.out.println("line draw");
		for(int x = p1.getIntX(); x <= p2.getIntX(); x++) {
			argbColor = Color.makeARGB((int)Math.round(RGBp1p[0]/z), (int)Math.round(RGBp1p[1]/z),
					(int)Math.round(RGBp1p[2]/z));
			
			vertex = new Vertex3D(x,(int)Math.round(y),1.0/z,Color.fromARGB(argbColor));
			
			vertex.setNormal(normX/z, normY/z, normZ/z);
			vertex.setHasNormal(true);
			
			vertex.setCameraSpaceBuffer(csX/z, csY/z, 1/csZ);
//			System.out.println("In line: ");
		//	System.out.println("DDA: "+vertex.getCameraSpaceBuffer().toString());
//			System.out.println("DDA CSZ"+ csZ/z+", "+csZ);
//			System.out.println("LineRenderer Color");			
//			color.print();
			color = pixelShader.shade(polygon, vertex);
			
			//System.out.println("DDA CSP: "+ vertex.getCameraSpaceBuffer().toString());
			//System.out.println("DDA NORMAL :"+ vertex.getNormal().toString());
//			System.out.println("DDA COLOR: "+ color.toString());
//			System.out.println();
			drawable.setPixel(x, (int)Math.round(y), 1.0/z, color.asARGB());
			
			y += mY;
			z += mZ;
			normX += mNormX;
			normY += mNormY;
			normZ += mNormZ;
			
			csX += mCSX;
			csY += mCSY;
			csZ += mCSZ;
			
			for(int i=0; i<3; i++) {
				RGBp1p[i] += mRGBp[i];
			}
		}	
	}
	
	public static LineRendererWithPoly make() {
		return new DDALineWithColorLERPforFPR();
	}
	
	private double[] getRGBSlope(double[] p1RGB, double[] p2RGB, double deltaX) {
		double[] mRGB = new double[3];
		for(int i=0; i<3;i++) {
			mRGB[i] = ((p2RGB[i]-p1RGB[i])/deltaX); 
		}
		
		return mRGB;
	}
	
	private double[] getRGBpArray(Vertex3D p) {
		double[] pointRGBp = new double[3];
		pointRGBp[0] = (double)(p.getColor().getIntR()/p.getZ());
		pointRGBp[1] = (double)p.getColor().getIntG()/p.getZ();
		pointRGBp[2] = (double)p.getColor().getIntB()/p.getZ();
		return pointRGBp;
	}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		// TODO Auto-generated method stub
		System.out.println("DDA - you're not suppose to be here.");
		
	}
}


