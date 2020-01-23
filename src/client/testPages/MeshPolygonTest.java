package client.testPages;

import java.util.Arrays;
import java.util.Random;

import geometry.Vertex3D;
import polygon.Polygon;
import polygon.PolygonRenderer;
import windowing.drawable.Drawable;
import windowing.graphics.Color;


public class MeshPolygonTest {
	
	
	private static final int NUM_IJ = 10;
	private static final int BORDER = 20;
	private static final int LENGTH = (650-(2 * BORDER))/9;
	public static final boolean NO_PERTURBATION = false;
	public static final boolean USE_PERTURBATION= true;
	private final PolygonRenderer renderer;
	private final Drawable panel;
	
	private final long SEED = 42; 
	
	public MeshPolygonTest(Drawable panel, PolygonRenderer renderer,boolean pert ) {
		this.panel = panel;
		this.renderer = renderer;
		
		render(pert);
	}
	
	private void render(boolean pert) {
		Color randColor;
		Vertex3D[] pointA = new Vertex3D[162];
		Vertex3D[] pointB = new Vertex3D[162];
		Vertex3D[] pointC = new Vertex3D[162];
		int index;
		int pertX[][] = new int[NUM_IJ][NUM_IJ];
		int pertY[][] = new int[NUM_IJ][NUM_IJ];
		Color color[][] = new Color[10][10];
		Random rand = new Random(SEED);
		
		color = makeColorArray();
		
		if(pert) {//if perterbing shift x, y values +/- 12 pixels
			for(int i=0; i<10; i++) {
				for(int j=0; j<10; j++) {
					pertX[i][j] = rand.nextInt(24)-12;
					pertY[i][j] = rand.nextInt(24)-12;
				}
			}
			
		} else {//perturb all by 0 pixels
		 for (int[] row : pertX) {
			 Arrays.fill(row, 0);}
		 
		 for(int[] row : pertY) {
			 Arrays.fill(row, 0);}
		}
		//loop through creating all regularly spaced flat bottomed triangles
		//pertX, Y values added by 10 by 10 perterbation matrix defined above
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				
			
				index = (i*9)+j;
				pointA[index] = new Vertex3D(BORDER + (j*LENGTH) + pertX[i][j], BORDER + (i*LENGTH) + pertY[i][j], 0, color[i][j]);
			
				pointB[index] = new Vertex3D(BORDER + ((j+1)*LENGTH) + pertX[i][j+1], BORDER + (i*LENGTH) + pertY[i][j+1], 0, color[i][j+1]);
				
				pointC[index] = new Vertex3D(BORDER + (j*LENGTH) + pertX[i+1][j], BORDER + ((i+1)*LENGTH) + pertY[i+1][j], 0, color[i+1][j]);
			}
		}
		//loop through all flat top triangles
		//pertX & Y values added from 10 by 10 matrix define above
		for(int i=0; i<9; i++) {
			for(int j=0; j<9; j++) {
				
				index = (i*9)+j+81;
				pointA[index] = new Vertex3D(BORDER + ((j+1)*LENGTH) + pertX[i+1][j+1], BORDER + ((i+1)*LENGTH) + pertY[i+1][j+1], 0, color[i+1][j+1]);
				
				pointC[index] = new Vertex3D(BORDER + ((j+1)*LENGTH) + pertX[i][j+1], BORDER + (i*LENGTH) + pertY[i][j+1], 0, color[i][j+1]);
				
				pointB[index] = new Vertex3D(BORDER + (j*LENGTH) + pertX[i+1][j], BORDER + ((i+1)*LENGTH) + pertY[i+1][j], 0, color[i+1][j]);
				
			}
		}
		
		for(int i =0; i < 162; i++) {//draw all polygons defined above
				Polygon polygon = Polygon.make(pointA[i],pointB[i],pointC[i]);
				renderer.drawPolygon(polygon,panel);
		}

	}

	private Color[][] makeColorArray() {
		Color color[][] = new Color[10][10];
		for(int i =0; i<10; i++) {
			for(int j=0; j<10; j++) {
				
				color[i][j]=Color.random();
			}
		}
		return color;
	}
}
