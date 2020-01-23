package client.interpreter;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import geometry.Point3DH;
import geometry.Vertex3D;
import polygon.Chain;
import polygon.Polygon;
import windowing.graphics.Color;
import client.interpreter.LineBasedReader;
import client.interpreter.SimpInterpreter.RenderStyle;

class ObjReader {
	private static final char COMMENT_CHAR = '#';
	private static final int NOT_SPECIFIED = -1;	

	private class ObjVertex {
		int vertex;
		int texture;
		int normal;
		
		public ObjVertex(int v, int t, int n) {
			this.vertex = v;
			this.texture = t;
			this.normal = n;
		}
	
		public int getVertex() {
			return vertex;
		}
		
		public int getTexture() {
			return texture;
		}
		
		public int getNormal() {
			return normal;
		}
	}
	
	private class ObjFace extends ArrayList<ObjVertex> {
		//DO NOT TOUCH
		private static final long serialVersionUID = -4130668677651098160L;
	}	
	
	private LineBasedReader reader;
	
	private List<Vertex3D> objVertices;
	private List<Vertex3D> transformedVertices;
	private List<Point3DH> objNormals;
	private List<ObjFace> objFaces;
	private boolean flag;

	private Color defaultColor;
	private String filename;
	
	ObjReader(String filename, Color defaultColor) {
		this.defaultColor=defaultColor;
		
		this.filename =filename;
		reader = new LineBasedReader(this.filename);
		
		//set new empty array lists
		objVertices = new ArrayList<Vertex3D>();
		transformedVertices = new ArrayList<Vertex3D>();
		objNormals = new ArrayList<Point3DH>();
		objFaces = new ArrayList<ObjFace>();
		
		//read input
		read();
	}

	public void render(SimpInterpreter simp) {
		Vertex3D newVertex;
		Vertex3D vertex;
		ObjFace face;
		Vertex3D[] vertices;
		Chain oldChain;
		Chain projChain;
		double csx, csy, csz;
		
		
		//step1: transform all objVertices
		for(int i =0; i < objVertices.size(); i++) {
			vertex = objVertices.get(i);
			vertex = simp.CTM.transformVertex(vertex);
			vertex = simp.worldToCamera.transformVertex(vertex);
			vertex.setCameraSpaceBuffer();
			transformedVertices.add(vertex);
		}
		
		
		for(int i = 0; i <objFaces.size();i++) {
			
			face = objFaces.get(i);
			
			//clip near and far
			
			vertices = arrayForFace(face);
			
			if (flag && vertices.length >2) {
				
				//if(vertices.length != 0) {
				oldChain = simp.clipper.zClip(vertices);
			
		
				Vertex3D[] newVertices = new Vertex3D[oldChain.length()];
				//project z-clipped vertices
			
				for(int j = 0;j < oldChain.length(); j++) {
					newVertex = oldChain.get(j);
					csx = newVertex.getCameraSpaceX();
					csy = newVertex.getCameraSpaceY();
					csz = newVertex.getCameraSpaceZ();
					
					newVertex = Transformation.projToScreen(newVertex,simp.d,simp.scaleX,simp.scaleY);
					newVertex = simp.worldToScreen.worldTransformVertex(newVertex);
					
					newVertex.setCameraSpaceBuffer(csx,csy, csz);
					newVertices[j] = newVertex;
				}
			
				//clip left and right
				projChain = simp.clipper.postProjClip(newVertices);
			
				//turn chain into array
				Vertex3D[] projVertices = new Vertex3D[projChain.length()];
				for(int j = 0 ; j<projChain.length();j++) {
					projVertices[j] = projChain.get(j);
				}
			
				if(projVertices.length>0) {
					Polygon[] polygons = simp.triangulate(projVertices);
					for(int j=0; j<polygons.length; j++ ) {
						//if(simp.renderStyle == RenderStyle.FILLED) {
						simp.filledRenderer.drawPolygon(polygons[j], simp.depthCueingDrawable, simp.faceShader,
								simp.vertexShader, simp.pixelShader);
						//} else {
						//	simp.wireframeRenderer.drawPolygon(polygons[j], simp.drawable,simp.ambientShader);
						//}
					}
				}
			}
			}
	}
	
	private Vertex3D[] arrayForFace(ObjFace face) {
		//Polygon polygon = polygonForFace(face);
		
		//Vertex3D[] vertices = new Vertex3D[polygon.length()];
		flag = true;
		int length = face.size();
		Vertex3D[] vertices = new Vertex3D[length];
	
		for(int i =0; i<length;i++) {
			try {
				ObjVertex thisObjVertex = face.get(i);
				
				int vertex = thisObjVertex.getVertex();
				int normal = thisObjVertex.getNormal();
				vertices[i] = transformedVertices.get(vertex -1);
				
				//set normals if they exist
				if(!objNormals.isEmpty()) {
					vertices[i].setNormal(objNormals.get(normal-1));
					vertices[i].setHasNormal(true);
				}
				
			}catch(IndexOutOfBoundsException e){
				System.out.println("Exception thrown in arrayForFace: " +  e);
				flag = false;
			}
		}
		return vertices;
	}

	public void read() {
		while(reader.hasNext() ) {
			String line = reader.next().trim();
			interpretObjLine(line);
		}
	}
	private void interpretObjLine(String line) {
		if(!line.isEmpty() && line.charAt(0) != COMMENT_CHAR) {
			String[] tokens = line.split("[ \t,()]+");
			if(tokens.length != 0) {
				interpretObjCommand(tokens);
			}
		}
	}

	private void interpretObjCommand(String[] tokens) {
		switch(tokens[0]) {
		case "v" :
		case "V" :
			interpretObjVertex(tokens);
			break;
		case "vn":
		case "VN":
			interpretObjNormal(tokens);
			break;
		case "f":
		case "F":
			interpretObjFace(tokens);
			break;
		default:	// do nothing
			break;
		}
	}
	private void interpretObjFace(String[] tokens) {
		ObjFace face = new ObjFace();
		
		for(int i = 1; i<tokens.length; i++) {
			String token = tokens[i];
			String[] subtokens = token.split("/");
			
			int vertexIndex  = objIndex(subtokens, 0, objVertices.size());
			//int textureIndex = objIndex(subtokens, 1, 0);
			int normalIndex  = objIndex(subtokens, 2, objNormals.size());

			ObjVertex objVertex = new ObjVertex(vertexIndex, NOT_SPECIFIED, normalIndex);
			face.add(objVertex);
		}
		objFaces.add(face);
	}

	private int objIndex(String[] subtokens, int tokenIndex, int baseForNegativeIndices) {
		int intVal;
		//test null
		//assert(subtokens[tokenIndex] != null && subtokens[tokenIndex] != "");
//		if(subtokens[tokenIndex].equals(""))
//		{
//			return NOT_SPECIFIED;
//		}
		//grab integer value of index
		if(tokenIndex >baseForNegativeIndices) {
			//negative index, set tokenIndex to tokenIndex and base, return that
			intVal = baseForNegativeIndices %tokenIndex +1;
			return intVal;
		}
		intVal = Integer.parseInt(subtokens[tokenIndex]);
		
		if(intVal < 0) {
			//negative index, set tokenIndex to tokenIndex and base, return that
			intVal = intVal +baseForNegativeIndices;
		}
		// returns the original token index if non negative, else return adjusted index
		return intVal;
	}

	private void interpretObjNormal(String[] tokens) {
		int numArgs = tokens.length - 1;
		if(numArgs != 3) {
			throw new BadObjFileException("vertex normal with wrong number of arguments : " + numArgs + ": " + tokens);				
		}
		Point3DH normal = SimpInterpreter.interpretPoint(tokens, 1);
		objNormals.add(normal);
	}
	
	private void interpretObjVertex(String[] tokens) {
		int numArgs = tokens.length - 1;
		Point3DH point = objVertexPoint(tokens, numArgs);
		Color color = objVertexColor(tokens, numArgs);
		
		Vertex3D vertex = new Vertex3D(point, color);
		objVertices.add(vertex);
		
	}

	private Color objVertexColor(String[] tokens, int numArgs) {
		if(numArgs == 6) {
			return SimpInterpreter.interpretColor(tokens, 4);
		}
		if(numArgs == 7) {
			return SimpInterpreter.interpretColor(tokens, 5);
		}
		return defaultColor;
	}

	private Point3DH objVertexPoint(String[] tokens, int numArgs) {
		if(numArgs == 3 || numArgs == 6) {
			return SimpInterpreter.interpretPoint(tokens, 1);
		}
		else if(numArgs == 4 || numArgs == 7) {
			return SimpInterpreter.interpretPointWithW(tokens, 1);
		}
		throw new BadObjFileException("vertex with wrong number of arguments : " + numArgs + ": " + tokens);
	}
}	