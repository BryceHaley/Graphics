package line;

import geometry.Vertex;
import geometry.Vertex3D;
import windowing.drawable.Drawable;
import windowing.graphics.Color;

public class BresenhamLineRenderer  implements LineRenderer {
	// use the static factory make() instead of constructor.
	private BresenhamLineRenderer() {}

	@Override
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable) {
		int deltaX = p2.getIntX() - p1.getIntX();
		int deltaY = p2.getIntY() - p1.getIntY();
		
		int m = 2*deltaY ;
		int q = m - (2 * deltaX);
		
		int y = p1.getIntY();
		int err = m - deltaX;
		
		int argbColor = p1.getColor().asARGB();
		drawable.setPixel(p1.getIntX(), y, 0.0, argbColor);
		
		for(int x = p1.getIntX()+1; x <= p2.getIntX(); x++) {
			if( err >= 0){
				err += q;
				y++;
			} else {
				err +=m;
			}
			drawable.setPixel(x, y, 0.0, argbColor);
		}

	}

	public static LineRenderer make() {
		return new AnyOctantLineRenderer(new BresenhamLineRenderer());
	}
}