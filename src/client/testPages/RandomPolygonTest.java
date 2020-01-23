package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class RandomPolygonTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
	public RandomPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		render();
	}
	
	public void render() {
		Random rand = new Random();
		Vertex3D p[] = new Vertex3D[3];
		int X[] = new int[3];
		int Y[] = new int[3];
		Color randColour;
		
		for(int i=1; i<20; i++) {
			randColour=Color.random();
			for(int j=0; j<3; j++){
				X[j] = rand.nextInt(299);
				Y[j] = rand.nextInt(299);
				p[j] = new Vertex3D(X[j],Y[j],0, randColour);
			}
			Polygon polygon = Polygon.makeEnsuringClockwise(p[0],p[1],p[2]);
			renderer.drawPolygon(polygon, panel);
				
			}
			
		}
	}

