package client;

import client.testPages.CenteredTriangleTest;
import client.testPages.MeshPolygonTest;
import client.testPages.ParallelogramTest;
import client.testPages.RandomLineTest;
import client.testPages.RandomPolygonTest;
import client.testPages.StarburstLineTest;
import client.testPages.StarburstPolygonTest;
import geometry.Point2D;
import line.AlternatingLineRenderer;
import line.AntialiasingLineRenderer;
import line.BresenhamLineRenderer;
import line.DDALineRenderer;
import line.ExpensiveLineRenderer;
import line.LineRenderer;
import client.ColoredDrawable;
import client.interpreter.RendererTrio;
import client.interpreter.SimpInterpreter;
//import notProvided.client.testpages.MeshPolygonTest;
//import notProvided.client.testpages.RandomPolygonTest;
//import client.testpages.StarburstPolygonTest;
import polygon.FilledPolygonRenderer;
//import notProvided.polygon.WireframePolygonRenderer;
import polygon.PolygonRenderer;
import polygon.WireframeRenderer;
import windowing.PageTurner;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
import windowing.drawable.GhostWritingDrawable;
import windowing.drawable.InvertedYDrawable;
import windowing.drawable.TranslatingDrawable;
import windowing.drawable.ZBufferDrawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class Client implements PageTurner {
	private static final int ARGB_WHITE = 0xff_ff_ff_ff;
	private static final int ARGB_GREEN = 0xff_00_ff_40;
	
	private static final int NUM_PAGES = 16;
	protected static final double GHOST_COVERAGE = 0.14;

	private static final int NUM_PANELS = 4;
	private static final Dimensions PANEL_SIZE = new Dimensions(300, 300);
	private static final Dimensions FULL_PANEL_SIZE = new Dimensions(650,650);
	private static final Point2D[] lowCornersOfPanels = {
			new Point2D( 50, 400),
			new Point2D(400, 400),
			new Point2D( 50,  50),
			new Point2D(400,  50),
	};
	private static final Point2D lowCornerFull = new Point2D(50,50);
	
	private final Drawable drawable;
	private int pageNumber = 0;
	
	private Drawable image;
	private Drawable[] panels;
	private Drawable panel;
	private Drawable[] ghostPanels;					// use transparency and write only white
	private Drawable largePanel;
	private Drawable fullPanel;
	
	private LineRenderer lineRenderers[];
	private PolygonRenderer polygonRenderer;
	private PolygonRenderer wireframeRenderer;
	
	Client(Drawable drawable) {
		this.drawable = drawable;	
		createDrawables();
		createRenderers();
	}

	public void createDrawables() {
		image = new InvertedYDrawable(drawable);
		image = new TranslatingDrawable(image, point(0, 0), dimensions(750, 750));
		image = new ColoredDrawable(image, ARGB_WHITE);
		
		
		//largePanel = new TranslatingDrawable(image, point(  50, 50),  dimensions(650, 650));
		fullPanel = new TranslatingDrawable(image, point(50,50), dimensions(650,650));
		fullPanel = new ZBufferDrawable(fullPanel);
//		createPanels();
		createFullPanel();
//		createGhostPanels();
	}

//	public void createPanels() {
//		panels = new Drawable[NUM_PANELS];
//		
//		for(int index = 0; index < NUM_PANELS; index++) {
//			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
//		}
//	}
	
	public void createPanels() {
		panels = new Drawable[NUM_PANELS];
		
		for(int index = 0; index < NUM_PANELS; index++) {
			panels[index] = new TranslatingDrawable(image, lowCornersOfPanels[index], PANEL_SIZE);
		}
	}	
	public void createFullPanel() {
		//panel = new Drawable;
		
		panel = new TranslatingDrawable(image, lowCornerFull, FULL_PANEL_SIZE);
		
	}

//	private void createGhostPanels() {
//		ghostPanels = new Drawable[NUM_PANELS];
//		
//		for(int index = 0; index < NUM_PANELS; index++) {
//			Drawable drawable = panels[index];
//			ghostPanels[index] = new GhostWritingDrawable(drawable, GHOST_COVERAGE);
//		}
//	}
	private Point2D point(int x, int y) {
		return new Point2D(x, y);
	}	
	private Dimensions dimensions(int x, int y) {
		return new Dimensions(x, y);
	}
	private void createRenderers() {
		
		lineRenderers = new LineRenderer[4];
		lineRenderers[0] = BresenhamLineRenderer.make();
//		lineRenderers[0] = ExpensiveLineRenderer.make();
		lineRenderers[1] = DDALineRenderer.make();
		lineRenderers[2] = AlternatingLineRenderer.make();
		lineRenderers[3] = AntialiasingLineRenderer.make();
		RendererTrio renderers = RendererTrio.make();
		
		polygonRenderer= FilledPolygonRenderer.make();
		wireframeRenderer = WireframeRenderer.make();
	}
	
	
	@Override
	public void nextPage() {
		if(Main.hasArgument) {
			argumentNextPage();
			System.out.println("argument nextpage");
		}
		else {
			noArgumentNextPage();
		}
	}

	RendererTrio renderers = RendererTrio.make();
	SimpInterpreter interpreter;
	
	private void argumentNextPage() {
		image.clear();
		fullPanel.clear();
		
		String filename=Main.arg;
		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}
	
	public void noArgumentNextPage() {
		System.out.println("PageNumber " + (pageNumber + 1));
		pageNumber = (pageNumber + 1) % NUM_PAGES;
		
		image.clear();
		fullPanel.clear();
		String filename;

		switch(pageNumber) {
		case 1:  filename = "page-a1";		 break;
		case 2:  filename = "page-a2";		 break;
		case 3:	 filename = "page-a3";		 break;
		case 4:  filename = "page-b1";		 break;
		case 5:  filename = "page-b2";		 break;
		case 6:  filename = "page-b3";		 break;
		case 7:  filename = "page-c1";		 break;
		case 8:  filename = "page-c2";		 break;
		case 9:  filename = "page-c3";		 break;
		case 10:  filename = "page-d";		 break;
		case 11:  filename = "page-e";		 break;
		case 12:  filename = "page-f1";		 break;
		case 13:  filename = "page-f2";		 break;
		case 14:  filename = "page-g";		 break;
		case 15:  filename = "page-h";		 break;
		case 0:  filename = "brycesPageI";	 break;

		default: defaultPage();
				 return;
		}				 
		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
	}

	private void centeredTriangleTest(Drawable fullPanel, PolygonRenderer polygonRenderer) {
		new CenteredTriangleTest(fullPanel,polygonRenderer);
		
	}

	@FunctionalInterface
	private interface TestPerformer {
		public void perform(Drawable drawable, LineRenderer renderer);
	}
	private void lineDrawerPage(TestPerformer test) {
		image.clear();

		for(int panelNumber = 0; panelNumber < panels.length; panelNumber++) {
			panels[panelNumber].clear();
			test.perform(panels[panelNumber], lineRenderers[panelNumber]);
		}
	}
	public void polygonDrawerPage(Drawable[] panelArray) {
		image.clear();
		for(Drawable panel: panels) {		// 'panels' necessary here.  Not panelArray, because clear() uses setPixel.
			panel.clear();
		}
		new StarburstPolygonTest(panelArray[0], polygonRenderer);
		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new MeshPolygonTest(panelArray[2], polygonRenderer, MeshPolygonTest.USE_PERTURBATION);
		new RandomPolygonTest(panelArray[3], polygonRenderer);
	}
	
	public void highlightPage(Drawable[] panelArray,Drawable[] ghostPanels) {
		new StarburstPolygonTest(panelArray[0], polygonRenderer);
		new MeshPolygonTest(panelArray[1], polygonRenderer, MeshPolygonTest.NO_PERTURBATION);
		new RandomLineTest(panelArray[2], lineRenderers[3]);
		new StarburstPolygonTest(ghostPanels[3],polygonRenderer);
	}

	private void defaultPage() {
		image.clear();
		fullPanel.fill(ARGB_GREEN, Double.MAX_VALUE);
		fullPanel.clear();
	//	largePanel.fill(ARGB_GREEN, Double.MAX_VALUE);
	}

	public void nextPage(String string) {
		image.clear();
		fullPanel.clear();
		
		String filename = string;
		interpreter = new SimpInterpreter(filename + ".simp", fullPanel, renderers);
		interpreter.interpret();
		
	}
}
