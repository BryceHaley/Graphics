package client.testPages;

import java.util.Random;

import geometry.Vertex3D;
import line.LineRenderer;
import polygon.Chain;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class CenteredTriangleTest {
	private final PolygonRenderer renderer;
	private final Drawable panel;
	private double[] v = {1, .85, .7, .55, .4, .25};
	
	Vertex3D center;
	
	public CenteredTriangleTest(Drawable panel, PolygonRenderer renderer) {
		this.panel = panel;
		this.renderer= renderer;
		
		makeCenter();
		render();
	}
	
	public void render() {
		Vertex3D p1;
		Vertex3D p2;
		Vertex3D p3;
		double randZ;
		double randTheta;
		Color whitish;
		double radius = 275;
		double angleDifference = (2.0 * Math.PI )/3;
		
		Random rand = new Random();
		
		for(int i = 0; i< v.length; i++) {
			randZ = rand.nextInt(199)*-1;
			randTheta = (rand.nextInt(120)*Math.PI)/180;
			whitish = Color.WHITE.scale(v[i]);
			p1 = radialPoint(radius, randTheta, randZ, whitish);
			p2 = radialPoint(radius, randTheta + angleDifference, randZ, whitish);
			p3 = radialPoint(radius, randTheta+ (2*angleDifference), randZ, whitish);
			Polygon polygon = Polygon.makeEnsuringClockwise(p3,p2,p1);
			renderer.drawPolygon(polygon, panel);
		}
	}
	
	
	private void makeCenter() {
		int centerX = panel.getWidth() / 2;
		int centerY = panel.getHeight() / 2;
		center = new Vertex3D(centerX, centerY, 0, Color.WHITE);
	}
	
	private Vertex3D radialPoint(double radius, double angle, double z, Color colour) {
		double x = center.getX() + radius * Math.cos(angle);
		double y = center.getY() + radius * Math.sin(angle);
		return new Vertex3D(x, y, z, colour);
	}
}
