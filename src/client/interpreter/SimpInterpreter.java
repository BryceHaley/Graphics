package client.interpreter;

import java.util.Stack;
import client.interpreter.LineBasedReader;
import geometry.Maths;
import geometry.Point3DH;
import geometry.Rectangle;
import geometry.Vertex3D;
import light.Light;
import light.Lighting;
import line.LineRenderer;
import polygon.Chain;
//import client.Clipper;
//import client.DepthCueingDrawable;
//import client.RendererTrio;
//import geometry.Transformation;
import polygon.Polygon;
import polygon.PolygonRenderer;
import polygon.Shader;
import shading.FaceShader;
import shading.PixelShader;
import shading.VertexShader;
import windowing.drawable.DepthCueingDrawable;
import windowing.drawable.Drawable;
import windowing.graphics.Color;
import windowing.graphics.Dimensions;

public class SimpInterpreter {
	private static final int NUM_TOKENS_FOR_POINT = 3;
	private static final int NUM_TOKENS_FOR_COMMAND = 1;
	private static final int NUM_TOKENS_FOR_COLORED_VERTEX = 6;
	private static final int NUM_TOKENS_FOR_UNCOLORED_VERTEX = 3;
	private static final char COMMENT_CHAR = '#';
	public RenderStyle renderStyle;
	
	public Transformation CTM;
	public Transformation worldToScreen;
	private Transformation scaleProjTransformation;
	public Transformation postClipCenter = new Transformation();
	
	private static int WORLD_LOW_X = -100;
	private static int WORLD_HIGH_X = 100;
	private static int WORLD_LOW_Y = -100;
	private static int WORLD_HIGH_Y = 100;
	private static int PROJ_SCALE = 325*4/13;
	
	public double scaleX;
	public double scaleY;

	 public double d =-1;
	 public double specularCoefficient = 0.3;
	 public double specularExponent = 8;
	 Lighting lighting = new Lighting();
	
	private LineBasedReader reader;
	private Stack<LineBasedReader> readerStack;
	
	private Stack<Transformation> transformationStack;
	
	private Color defaultColor = Color.WHITE;
	private Color ambientLight = Color.BLACK;
	public Shader ambientShader = c-> ambientLight.multiply(c);
	
	public FaceShader faceShader = polygon -> faceShade(polygon);
	public PixelShader pixelShader = (polygon, vertex) -> pixelShade(polygon, vertex);
	public VertexShader vertexShader = (polygon, vertex) -> vertexShade(polygon, vertex);
	
	public Drawable drawable;
	public Drawable depthCueingDrawable;
	
	
	private LineRenderer lineRenderer;
	public PolygonRenderer filledRenderer;
	public PolygonRenderer wireframeRenderer;
	private Transformation cameraToScreen;
	public Transformation worldToCamera;
	public Clipper clipper;

	public enum RenderStyle {
		FILLED,
		WIREFRAME;
	}
	
	public enum ShaderStyle {
		FLAT, GOURAUD, PHONG;
	}
	
	private ShaderStyle shaderStyle = ShaderStyle.PHONG;
	
	public SimpInterpreter(String filename, 
			Drawable drawable,
			RendererTrio renderers) {
		this.drawable = drawable;
		this.depthCueingDrawable = drawable;
		this.lineRenderer = RendererTrio.getLineRenderer();
		this.filledRenderer = RendererTrio.getFilledRenderer();
		this.wireframeRenderer = RendererTrio.getWireframeRenderer();
		this.defaultColor = Color.WHITE;
		this.ambientLight = Color.BLACK;
		makeWorldToScreenTransform(drawable.getDimensions());
		
		reader = new LineBasedReader(filename);
		readerStack = new Stack<>();
		renderStyle = RenderStyle.FILLED;
		CTM = Transformation.identity();
		transformationStack=new Stack<>();
	}

	private void makeWorldToScreenTransform(Dimensions dimensions) {
		worldToScreen = Transformation.identity();
		worldToScreen.translate(325, 325, 0);
	}
	
	public void interpret() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretLine(line);
			while(!reader.hasNext()) {
				if(readerStack.isEmpty()) {
					return;
				}
				else {
					reader = readerStack.pop();
				}
			}
		}
	}
	
	public void interpretLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretCommand(tokens);
			}
		}
	}
	
	private void interpretCommand(String[] tokens) {
		switch(tokens[0]) {
		case "{" :      push();   break;
		case "}" :      pop();    break;
		case "wire" :   wire();   break;
		case "filled" : filled(); break;
		
		case "file" :		interpretFile(tokens);		break;
		case "scale" :		interpretScale(tokens);		break;
		case "translate" :	interpretTranslate(tokens);	break;
		case "rotate" :		interpretRotate(tokens);	break;
		case "line" :		interpretLine(tokens);		break;
		case "polygon" :	interpretPolygon(tokens);	break;
		case "camera" :		interpretCamera(tokens);	break;
		case "surface" :	interpretSurface(tokens);	break;
		case "ambient" :	interpretAmbient(tokens);	break;
		case "depth" :		interpretDepth(tokens);		break;
		case "obj" :		interpretObj(tokens);		break;
		case "light" :		interpretLight(tokens);		break;
		case "flat":		shaderStyle = ShaderStyle.FLAT;	
		System.out.println("is flat");
		break;
		case "gouraud":		shaderStyle = ShaderStyle.GOURAUD;
		System.out.println("is gouraud");
		break;
		case "phong":		shaderStyle = ShaderStyle.PHONG;		
		System.out.println("is phong");
		break;
		
		default :
			System.err.println("bad input line: " + tokens);
			break;
		}
	}
	
	private void interpretLight(String[] tokens) {
		double red = cleanNumber(tokens[1]);
		double green = cleanNumber(tokens[2]);
		double blue = cleanNumber(tokens[3]);
		double A = cleanNumber(tokens[4]);
		double B = cleanNumber(tokens[5]);;
		Color color = new Color(red, green, blue);
		Vertex3D cameraVertex = new Vertex3D(0,0,0,Color.WHITE);
		cameraVertex = transformToCamera(CTM.transformVertex(cameraVertex));
		Point3DH cameraSpaceLocation = cameraVertex.getPoint3D();
		Light light = new Light(color, cameraSpaceLocation, A, B);
		lighting.addLight(light);
	}

	private void interpretDepth(String[] tokens) {
		int nearZ = (int)cleanNumber(tokens[1]);
		int farZ = (int)cleanNumber(tokens[2]);
		int farARGB = (int)cleanNumber(tokens[3]);
		Color farColor = Color.fromARGB(farARGB);
		this.depthCueingDrawable= new DepthCueingDrawable(drawable,nearZ, farZ, farColor);
		
	}

	private void interpretSurface(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		double k = cleanNumber(tokens[4]);
		double p = cleanNumber(tokens[5]);
		defaultColor = new Color(r,g,b);
		
		specularCoefficient = k;
		specularExponent = p;
		
	}
	
	private void interpretAmbient(String[] tokens) {
		double r = cleanNumber(tokens[1]);
		double g = cleanNumber(tokens[2]);
		double b = cleanNumber(tokens[3]);
		ambientLight = new Color(r,g,b);		
	}

	private void interpretCamera(String[] tokens) {
		double xLow = cleanNumber(tokens[1]);
		double yLow = cleanNumber(tokens[2]);
		double xHigh = cleanNumber(tokens[3]);
		double yHigh = cleanNumber(tokens[4]);
		double near = cleanNumber(tokens[5]);
		double far = cleanNumber(tokens[6]);
		
		int height = drawable.getHeight();
		int width = drawable.getWidth();
		
		worldToCamera =  Transformation.copyOf(CTM);
		worldToCamera.inverse();

		double dx = xHigh-xLow;
		double dy = yHigh-yLow;
		
		scaleX = width/dx;
		scaleY = height/dy;
		postClipCenter.identify();
		
		double postClipScale = Math.min(2/dx, 2/dy);
		postClipCenter.scale(postClipScale, postClipScale, 1);
		
		if(dx>dy) {
			double translate = (dy/dx)*(height/2);
			postClipCenter.translate(0, translate, 0);
		}else if (dy<dx) {
			double translate = (dx/dy)*(height/2);
			postClipCenter.translate(translate, 0, 0);
		}
		
		clipper = new Clipper(xLow*width+width, yLow*height+height, xHigh*(width/2)+width/2,
				yHigh*(height/2)+(height/2),near, far);
	}

	private void push() {
		transformationStack.push(Transformation.copyOf(CTM));
	}
	private void pop() {
		CTM = transformationStack.pop();
	}
	private void wire() {
		renderStyle = renderStyle.WIREFRAME;
	}
	private void filled() {
		renderStyle=renderStyle.FILLED;
	}
	
	// this one is complete.
	private void interpretFile(String[] tokens) {
		String quotedFilename = tokens[1];
		int length = quotedFilename.length();
		assert quotedFilename.charAt(0) == '"' && quotedFilename.charAt(length-1) == '"'; 
		String filename = quotedFilename.substring(1, length-1);
		file(filename + ".simp");
	}
	private void file(String filename) {
		readerStack.push(reader);
		reader = new LineBasedReader(filename);
	}	

	private void interpretScale(String[] tokens) {
		double sx = cleanNumber(tokens[1]);
		double sy = cleanNumber(tokens[2]);
		double sz = cleanNumber(tokens[3]);
		
		CTM.scale(sx,sy,sz);
	}
	private void interpretTranslate(String[] tokens) {
		double tx = cleanNumber(tokens[1]);
		double ty = cleanNumber(tokens[2]);
		double tz = cleanNumber(tokens[3]);
		
		CTM.translate(tx,ty,tz);
	}
	private void interpretRotate(String[] tokens) {
		String axisString = tokens[1];
		double angleInDegrees = cleanNumber(tokens[2]);
		
		if(axisString.equals("X")) {
			CTM.xRotate(Math.toRadians(angleInDegrees));
		} else if (axisString.equals("Y")) {
			CTM.yRotate(Math.toRadians(angleInDegrees));
		} else {
			CTM.zRotate(Math.toRadians(angleInDegrees));
		}
	}
	private static double cleanNumber(String string) {
		return Double.parseDouble(string);
	}
	
	private enum VertexColors {
		COLORED(NUM_TOKENS_FOR_COLORED_VERTEX),
		UNCOLORED(NUM_TOKENS_FOR_UNCOLORED_VERTEX);
		
		private int numTokensPerVertex;
		
		private VertexColors(int numTokensPerVertex) {
			this.numTokensPerVertex = numTokensPerVertex;
		}
		public int numTokensPerVertex() {
			return numTokensPerVertex;
		}
	}
	private void interpretLine(String[] tokens) {			
		Vertex3D[] vertices = interpretVertices(tokens, 2, 1);

		lineRenderer.drawLine(vertices[0], vertices[1], drawable);
	}	
	
	private void interpretPolygon(String[] tokens) {			
		double csx, csy,csz;
		
		//put into camera space
		Vertex3D[] vertices = interpretVertices(tokens, 3, 1);
		//Chain oldChain = new Chain(vertices);
		//preform Z clip
		Chain oldChain = clipper.zClip(vertices);
		//Chain newChain = new Chain();
		
		//put back into vertex array
		Vertex3D[] newVertices = new Vertex3D[oldChain.length()];
		
		//projective transformation
		for(int i = 0;i < oldChain.length(); i++) {
			Vertex3D vertex = oldChain.get(i);
			csx =vertex.getCameraSpaceX();
			csy = vertex.getCameraSpaceY();
			csz = vertex.getCameraSpaceZ();
			
			vertex = Transformation.projToScreen(vertex,d,scaleX,scaleY);
			vertex = worldToScreen.worldTransformVertex(vertex);
			vertex.setCameraSpaceBuffer(csx, csy, csz);
			newVertices[i] = vertex;
			
		}

		//clip left and right
		Chain projChain = clipper.postProjClip(newVertices);
		
		//turn chain into array
		Vertex3D[] projVertices = new Vertex3D[projChain.length()];
		for(int i = 0 ; i<projChain.length();i++) {
			projVertices[i] = projChain.get(i);
		}
		for(int i =0; i < projVertices.length;i++) {
			csx = projVertices[i].getCameraSpaceX();
			csy = projVertices[i].getCameraSpaceY();
			csz = projVertices[i].getCameraSpaceZ();
			projVertices[i]= postClipCenter.transformVertex(projVertices[i]);
			projVertices[i].setCameraSpaceBuffer(csx,csy,csz);
		}
		if(projVertices.length>0) {
			Polygon[] polygons = triangulate(projVertices);
				for(int i=0; i<polygons.length; i++ ) {
				if(renderStyle == RenderStyle.FILLED) {
					filledRenderer.drawPolygon(polygons[i], depthCueingDrawable,
							faceShader, vertexShader, pixelShader);
			//	} else {
			//		wireframeRenderer.drawPolygon(polygons[i], drawable,ambientShader);
				}
			}
		}
	}
	//called by objReader.render();
	public void renderObjPolygon(Polygon polygon) {
	//	if(renderStyle == RenderStyle.FILLED) {
		System.out.println("in obj polyrender");
			filledRenderer.drawPolygon(polygon, drawable, faceShader, vertexShader, pixelShader);
	//	} else {
		//	wireframeRenderer.drawPolygon(polygon, drawable, ambientShader);
	//	}
	}
		
	public Vertex3D[] interpretVertices(String[] tokens, int numVertices, int startingIndex) {
		VertexColors vertexColors = verticesAreColored(tokens, numVertices);	
		Vertex3D vertices[] = new Vertex3D[numVertices];
		
		for(int index = 0; index < numVertices; index++) {
			vertices[index] = interpretVertex(tokens, startingIndex + index * vertexColors.numTokensPerVertex(), vertexColors);
		}
		return vertices;
	}
	public VertexColors verticesAreColored(String[] tokens, int numVertices) {
		return hasColoredVertices(tokens, numVertices) ? VertexColors.COLORED :
														 VertexColors.UNCOLORED;
	}
	public boolean hasColoredVertices(String[] tokens, int numVertices) {
		return tokens.length == numTokensForCommandWithNVertices(numVertices);
	}
	public int numTokensForCommandWithNVertices(int numVertices) {
		return NUM_TOKENS_FOR_COMMAND + numVertices*(NUM_TOKENS_FOR_COLORED_VERTEX);
	}

	
	private Vertex3D interpretVertex(String[] tokens, int startingIndex, VertexColors colored) {
		Point3DH point = interpretPoint(tokens, startingIndex);
		
		Color color = defaultColor;
		if(colored == VertexColors.COLORED) {
			color = interpretColor(tokens, startingIndex + NUM_TOKENS_FOR_POINT);
		}
		Vertex3D vertex = new Vertex3D(point,color);
		
		vertex = CTM.transformVertex(vertex);
		vertex = worldToCamera.transformVertex(vertex);
		vertex.setCameraSpaceBuffer();
		
		return vertex;
	}
	
	public static Point3DH interpretPoint(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);

		Point3DH point = new Point3DH(x,y,z);
		
		return point;
	}
	
	public static Color interpretColor(String[] tokens, int startingIndex) {
		double r = cleanNumber(tokens[startingIndex]);
		double g = cleanNumber(tokens[startingIndex + 1]);
		double b = cleanNumber(tokens[startingIndex + 2]);

		Color color = new Color(r,g,b);
		return color;
	}
	
	//TODO: figure out if this needs to be done. What is Point 3D
	
	public static Point3DH interpretPointWithW(String[] tokens, int startingIndex) {
		double x = cleanNumber(tokens[startingIndex]);
		double y = cleanNumber(tokens[startingIndex + 1]);
		double z = cleanNumber(tokens[startingIndex + 2]);
		double w = cleanNumber(tokens[startingIndex + 3]);
		Point3DH point = new Point3DH(x, y, z, w);
		return point;
	}
	
	private void interpretObj(String[] tokens) {
		String filename = tokens[1].replace("\"", "");
		filename = filename + ".obj";
		objFile(filename);
	}
		
	private void objFile(String filename) {
		ObjReader objReader = new ObjReader(filename, defaultColor);
		objReader.read();
		objReader.render(SimpInterpreter.this);
	}

	private void line(Vertex3D p1, Vertex3D p2) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		// TODO: finish this method
	}
	private void polygon(Vertex3D p1, Vertex3D p2, Vertex3D p3) {
		Vertex3D screenP1 = transformToCamera(p1);
		Vertex3D screenP2 = transformToCamera(p2);
		Vertex3D screenP3 = transformToCamera(p3);
		// TODO: finish this method
	}

	private Vertex3D transformToCamera(Vertex3D vertex) {
	return worldToCamera.transformVertex(vertex);	
	}
	
	public Vertex3D objTransformPreZ(Vertex3D vertex) {
		vertex = CTM.transformVertex(vertex);
		vertex = worldToCamera.transformVertex(vertex);
		vertex.setCameraSpaceBuffer();
		return vertex;
	}
	
	public Vertex3D objTransformPreXY(Vertex3D vertex) {
		vertex = Transformation.projToScreen(vertex,d,scaleX,scaleY);
	
		vertex = worldToScreen.worldTransformVertex(vertex);
		
		
		return vertex;
	}
	
	public Polygon[] triangulate(Vertex3D[] vertices) {
		int size = vertices.length;
		Polygon[] polygons = new Polygon[size-2];
		for(int i = 0; i < size-2; i++) {
			polygons[i]=Polygon.make(vertices[0],vertices[i+1], vertices[i+2]);
		}
		return polygons;
	}
	
	
	public Polygon faceShade(Polygon polygon) {
		if(shaderStyle == ShaderStyle.FLAT) {
			//System.out.println("in faceshade flat");
			Point3DH polyNormal;
			lighting.setAmbient(ambientLight);
			Vertex3D midVertex = Maths.getMidPoint(polygon.get(0), polygon.get(1), polygon.get(2));
			if(polygon.get(0).getHasNormal()==false)
			{
				polyNormal  = Maths.triangleNormal(polygon.get(0),polygon.get(1), polygon.get(2));
			}else {
				polyNormal = polygon.get(0).getNormal().add(polygon.get(1).getNormal()
						.add(polygon.get(2).getNormal()));
				polyNormal.scale(1.0/3);
			}
			Color faceColor=lighting.light(midVertex, defaultColor, polyNormal, specularCoefficient, specularExponent);// Color.WHITE; //faceColor =
			polygon.setColor(faceColor);
			//System.out.println("in face");
			return polygon;
		}else
		{
			return polygon;
		}
	}

	private Color pixelShade(Polygon polygon, Vertex3D vertex) {
		
		if(shaderStyle == ShaderStyle.FLAT) {
			return polygon.getColor();
			
		}else if (shaderStyle==ShaderStyle.GOURAUD){
			return vertex.getColor();
			
		}else {
			lighting.setAmbient(ambientLight);
//			System.out.println("pixelShaderNomral:");
//			System.out.println(vertex.getNormal().toString());
			Color pixelColor =lighting.light(vertex, defaultColor, vertex.getNormal(), specularCoefficient, specularExponent);
			return pixelColor;
			
		}
	}
	
	private Vertex3D vertexShade(Polygon polygon, Vertex3D vertex) {
		if(shaderStyle == ShaderStyle.FLAT) {
			return vertex;
			
		} else if(shaderStyle == ShaderStyle.GOURAUD) {
			lighting.setAmbient(ambientLight);
//			System.out.println("ambientlight");
//			ambientLight.print();
			
			if(vertex.getHasNormal()== false) {
				Point3DH normal = Maths.triangleNormal(polygon);
				vertex.setNormal(normal);
				vertex.setHasNormal(true);
			}
			Color vertexColor=lighting.light(vertex, defaultColor, vertex.getNormal(), specularCoefficient, specularExponent);
			vertex = vertex.replaceColor(vertexColor);
			return vertex;
		}else {
			if(vertex.getHasNormal()== false) {
				Point3DH normal = Maths.triangleNormal(polygon);
				vertex.setNormal(normal);
				vertex.setHasNormal(true);
			}
			vertex.setDoPhong(true);
			return vertex;
		}
	}
}
