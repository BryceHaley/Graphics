package windowing.drawable;

import windowing.graphics.Color;

public class DepthCueingDrawable extends DrawableDecorator{
	private final double initZValue =-Double.MAX_VALUE;
	private double[][] zBuffer;
	private int xDim;
	private int yDim;
	private Color farColor;
	private int nearZ;
	private int farZ;
	
	public DepthCueingDrawable(Drawable delegate, int zStart, int zEnd, Color color) {
		super(delegate);
		yDim = delegate.getHeight()+1;
		xDim = delegate.getWidth()+1;
		nearZ = zStart;
		farZ = zEnd; 
		zBuffer = new double[yDim][xDim];
		farColor = color;
		setZBuffer();
	}


	private void setZBuffer() {
		for(int i=0; i<xDim; i++) {
			for(int j=0; j<yDim; j++) {
				zBuffer[j][i] = initZValue;
			}
		}
	}


	@Override
	public void setPixel(int x, int y, double z, int argbColor) {
		Color setColor;
		//System.out.println("in depth cueing drawable");
		if(x > 0 && x<xDim && y>0 && y<yDim && z <=0) {
			if(zBuffer[y][x] < z) {
				if(z< farZ) {
					// if the object is behind the far plane
					setColor = farColor;
				} else if ( z> nearZ) {
					//the object is infront of the near plane
					setColor = Color.fromARGB(argbColor);
				}else {
					//interpolate size z is between the two planes
					//get ratio
					double r = (z-farZ)/(nearZ-farZ);
					Color frontColor = Color.fromARGB(argbColor);
					frontColor = frontColor.scale(r);
					farColor = farColor.scale(1-r);
					setColor = farColor.add(frontColor);
				}
				//System.out.println("in depth cueing drawable");
				delegate.setPixel(x,y,z,setColor.asARGB());
				zBuffer[y][x] = 999999;
			}
		}
	}
	@Override
	public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
		setZBuffer();
	}
}
