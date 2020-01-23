package client;

import windowing.drawable.Drawable;

public class ColoredDrawable extends windowing.drawable.DrawableDecorator{

	private int colour;
	public ColoredDrawable(Drawable delegate, int colour ) {
		super(delegate);
		this.colour=colour;
	}

	@Override
	public void clear() {
		fill(colour, Double.MAX_VALUE);
	}
	
	
}
