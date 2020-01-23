package windowing.drawable;

import java.awt.Color;

public class ZBufferDrawable extends DrawableDecorator{
	private final int initZValue =-201;
	private double[][] zBuffer;
	
	private int xDim;
	private int yDim;
	
	public ZBufferDrawable(Drawable delegate) {
		super(delegate);
		yDim = delegate.getHeight()+1;
		xDim = delegate.getWidth()+1;
		zBuffer = new double[yDim][xDim];
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
		//System.out.println("in depth cueing drawable");
		if(x > 0 && x<xDim && y>0 && y<yDim) {
			if(zBuffer[y][x] < z) {
				delegate.setPixel(x,y,z,argbColor);
				zBuffer[y][x] = z;
			}
		}
	}
	@Override
	public void clear() {
		fill(ARGB_BLACK, Double.MAX_VALUE);
		setZBuffer();
	}

}
