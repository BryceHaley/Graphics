package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import windowing.drawable.Drawable;
//import windowing.drawable.InvertedYDrawable;
import windowing.graphics.Color;

public class ParallelogramTest {
	private final LineRenderer renderer;
	private final Drawable panel;
	private static final int start = 0;
	private static final int end = 50;
	Vertex3D p1;
	Vertex3D p2;
	int height;
	public ParallelogramTest(Drawable panel, LineRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		
		render();
		
		
	}
	private void render() {
		height = panel.getHeight();
		for(int p = start; p<=end; p++) {
			p1 = new Vertex3D(20, height-(80+p),0,Color.WHITE);
			p2 = new Vertex3D(150,height-(150+p),0,Color.WHITE);
			renderer.drawLine(p1, p2, panel);
			
			p1 = new Vertex3D(160+p,height-270,0,Color.WHITE);
			p2 = new Vertex3D(240+p,height-40,0,Color.WHITE);
			renderer.drawLine(p1, p2, panel);
		}
	}
}