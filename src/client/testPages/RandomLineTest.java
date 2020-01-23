package client.testPages;

import java.util.Random;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import line.LineRenderer;

public class RandomLineTest {
	private final LineRenderer renderer;
	private final Drawable panel;
	private static final long SEED = new Random().nextLong();
	Vertex3D p1;
	Vertex3D p2;
	int x;
	int y;
	int height;
	int width;
	
	public RandomLineTest(Drawable panel, LineRenderer renderer) {
			this.panel = panel;
			this.renderer = renderer;
			render();
			
	}
	private void render() {
		Random random = new Random(SEED);

		Color randColour;
		height = panel.getHeight();
		width = panel.getWidth();
		for(int i = 0; i<30; i ++) {
		x = random.nextInt(width);
		y= random.nextInt(height);
		randColour = Color.random(random);
		p1 = new Vertex3D(x,y,0,randColour);
		
		x= random.nextInt(width);
		y = random.nextInt(height);
		randColour = Color.random(random);
		p2 = new Vertex3D(x,y,0,randColour);
		
		renderer.drawLine(p1, p2, panel);
		}
	}
}	

