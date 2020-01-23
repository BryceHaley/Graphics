package client.interpreter;

import line.DDALineRenderer;
import line.LineRenderer;
import line.DDALineWithColorLERP;
import polygon.FilledPolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframeRenderer;

public class RendererTrio {

    public static LineRenderer getLineRenderer() {
        return DDALineWithColorLERP.make();
    }

    public static PolygonRenderer getFilledRenderer() {
        return FilledPolygonRenderer.make();
    }

    public static PolygonRenderer getWireframeRenderer() {
        return WireframeRenderer.make();
    }
    
    public static RendererTrio make() {
    	return  new RendererTrio();
    }
}
