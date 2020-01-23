package polygon;

import windowing.drawable.Drawable;
import windowing.graphics.Color;
import polygon.PolygonRenderer;
import polygon.Shader;
import shading.FaceShader;
import shading.PixelShader;
import shading.VertexShader;
import polygon.Chain;
import polygon.Polygon;
import geometry.Vertex3D;
import line.DDALineRenderer;
import line.DDALineWithColorLERP;
import line.LineRenderer;


public class WireframeRenderer implements PolygonRenderer{
	
	private WireframeRenderer() {}
	
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		//get chains
		
		LineRenderer DDA = DDALineWithColorLERP.make();
		Chain lChain = polygon.leftChain();
		Chain rChain = polygon.rightChain();
		if (lChain.length()==1 && rChain.length()==1) {
			return;
		}
		Chain fullChain;
		Vertex3D p1;
		Vertex3D p2;
		boolean leftLong;
		
		
		leftLong = lChain.length()==3;
		
		if (leftLong) {
			fullChain = lChain;
		} else {
			fullChain = rChain;
		}
		
		for(int i = 0; i<3; i++) {
			p1 = fullChain.get(i);
			p2 = fullChain.get((i+1)%3);
			
			DDA.drawLine(p1,p2, drawable);
		}
	}
	public static PolygonRenderer make(){
		return new WireframeRenderer();	
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, FaceShader faceShader, VertexShader vertexShader,
			PixelShader pixelShader) {
		// TODO Auto-generated method stub
		
	}
}
