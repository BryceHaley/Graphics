package line;

import geometry.Vertex3D;
import windowing.drawable.Drawable;
import polygon.Polygon;
import shading.PixelShader;

public interface LineRendererWithPoly extends LineRenderer {
	public void drawLine(Vertex3D p1, Vertex3D p2, Drawable drawable, Polygon polygon, 
			PixelShader pixelShader);
}
