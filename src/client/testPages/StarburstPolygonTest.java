package client.testPages;

import geometry.Vertex3D;
import line.LineRenderer;
import polygon.Chain;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class StarburstPolygonTest {
	private static final int NUM_RAYS = 90;
	
	private final PolygonRenderer renderer;
	private final Drawable panel;
	Vertex3D center;

	public StarburstPolygonTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer = renderer;
		
		makeCenter();
		render();
	}
	
	
	public void render() {
		double radius = 125;
		double angleDifference = (2.0 * Math.PI )/ NUM_RAYS;
		double angle = 0;
		Vertex3D p1;
		Vertex3D p2;
		Vertex3D p0;
		Color randColor;
		
		for(int ray=0; ray <NUM_RAYS; ray++) {
			randColor = Color.random();
			p1= radialPoint(radius,angle, randColor);
			p2 = radialPoint(radius, angle+angleDifference, randColor);
			p0 = makeColorCenter(randColor);
			p1 = p1.rounded();
			p2 = p2.rounded();
			
			Polygon polygon = Polygon.make(p0, p1, p2);
			renderer.drawPolygon(polygon, panel);
			angle+=angleDifference;
		}		
		
	}
	
	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
	}

	private Vertex3D makeColorCenter(Color color) {
		Vertex3D point = new Vertex3D(center.getX(),center.getY(), 0, color);
		return point;
	}
	
	private Vertex3D radialPoint(double radius, double angle, Color colour) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D(x, y, 0, colour);
	}
}
