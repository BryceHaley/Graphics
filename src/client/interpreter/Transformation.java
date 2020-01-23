package client.interpreter;

import java.util.Arrays;

import geometry.Vertex3D;

public class Transformation {
	
	public double[][] M;
	private static int size =4;
	
	public Transformation() {
		this.M = new double[4][4];
		identify();
	}

	
	public void identify() {
		for(int i = 0; i< M.length;i++) {
			M[i][i]=1;
		}
	}
	
	public static Transformation identity() {
		return new Transformation();
	}

	public void scale(double sx, double sy, double sz) {
		double[][] scaleMatrix = {	{sx, 0, 0, 0},
									{0, sy, 0, 0},
									{0, 0, sz, 0},
									{0, 0, 0, 1}};
		
		lMultiply(scaleMatrix);
	}
	
	public void translate(double tx, double ty, double tz) {
		double[][] translateMatrix = {	{1, 0, 0, tx},
										{0, 1, 0, ty},
										{0, 0, 1, tz},
										{0, 0, 0, 1}};
		
		lMultiply(translateMatrix);
	}
	
	public void xRotate(double theta) {
		double[][] rotationMatrix = {	{1, 0, 					0, 					0},
										{0,	Math.cos(theta), 	-Math.sin(theta), 	0, },
										{0, Math.sin(theta), 	Math.cos(theta), 	0},
										{0, 0, 					0, 					1}};
		
		lMultiply(rotationMatrix);
	}
	
	public void yRotate(double theta) {
		double[][] rotationMatrix = {	{ Math.cos(theta),	0,	Math.sin(theta),	0},
										{0,					1,	0,					0},
										{-Math.sin(theta),	0,	Math.cos(theta),	0},
										{0,					0,	0,					1}};
		
	lMultiply(rotationMatrix);
	}
	
	public void zRotate(double theta) {
		double[][] rotationMatrix = {	{Math.cos(theta),	-Math.sin(theta),	0,					0},
										{Math.sin(theta),	Math.cos(theta),	0,					0},
										{0,					0,					1,					0},
										{0,					0,					0,					1}};
		
	lMultiply(rotationMatrix);
	}
	
	private void lMultiply(double[][] T) {
		
		double[][] result = new double[4][4];
		
		for(int i = 0; i < 4; i++) {
			for(int j =0; j < 4; j++) {
				for(int k =0; k<4; k++) {
					result[i][j] += M[i][k]*T[k][j];
				}
			}
		}
		M = result;
	}
	
	//grab frustum
	public static Vertex3D projToScreen(Vertex3D vertex, double d, double scaleX, double scaleY) {
			double[][] T = {	{1,0,0,0},
								{0,1,0,0},
								{0,0,1,0},
								{0,0,1/d,0}};
			Vertex3D p1;
			
			double z = vertex.getZ();
			
			double[] vertexCoords= new double[4];
			double[] newCoords = new double[4];
			
			vertexCoords[0] = vertex.getX();
			vertexCoords[1] = vertex.getY();
			vertexCoords[2] = vertex.getZ();
			vertexCoords[3] = 1;
			
			double holder;
			
			for(int i=0; i<4; i++) {
				holder =0;
				
				for(int j =0; j<4; j++) {
					holder +=T[i][j] * vertexCoords[j];
				}
				newCoords[i] = holder;
			}
			p1 = new Vertex3D((int)newCoords[0]*scaleX/newCoords[3],(int)newCoords[1]*scaleY/newCoords[3], z, vertex.getColor());
			
			return p1;
	}
	
	public Vertex3D transformVertex(Vertex3D vertex) {
		Vertex3D p1;
		double[] vertexCoords= new double[4];
		double[] newCoords = new double[4];
		vertexCoords[0] = vertex.getX();
		vertexCoords[1] = vertex.getY();
		vertexCoords[2] = vertex.getZ();
		vertexCoords[3] = 1;
		
		double holder;
		
		for(int i=0; i<4; i++) {
			holder =0;
			
			for(int j =0; j<4; j++) {
				holder +=M[i][j] * vertexCoords[j];
			}
			newCoords[i] = holder;
		}
		p1 = new Vertex3D((int)newCoords[0],(int)newCoords[1], newCoords[2], vertex.getColor());
		return p1;
	}
	
	//put into -1, 1 in screenspace
	public Vertex3D worldTransformVertex(Vertex3D vertex) {
		Vertex3D p1;
		double[] vertexCoords= new double[4];
		double[] newCoords = new double[4];
		vertexCoords[0] = vertex.getX();
		vertexCoords[1] = vertex.getY();
		vertexCoords[2] = vertex.getZ();
		vertexCoords[3] = 1;
		
		double holder;
		
		for(int i=0; i<4; i++) {
			holder =0;
			
			for(int j =0; j<4; j++) {
				holder +=M[i][j] * vertexCoords[j];
			}
			newCoords[i] = holder;
		}
		p1 = new Vertex3D((int)newCoords[0],(int)newCoords[1], newCoords[2], vertex.getColor());
		return p1;
	}
	
	public void printMatrix() {
		System.out.println("====TRANSFROM======");
		System.out.println(Arrays.toString(M[0]));
		System.out.println(Arrays.toString(M[1]));
		System.out.println(Arrays.toString(M[2]));
		System.out.println(Arrays.toString(M[3]));
		//System.out.println(Arrays.toString(M[4]));
	}
	
	public static Transformation copyOf(Transformation original) {
		Transformation copy = new Transformation(); 
		for(int i=0; i<4 ; i++) {
			for(int j =0; j<4; j++) {
				copy.M[i][j] = original.M[i][j];
			}
		}
		return copy;
	}
	
	//adapted from https://github.com/rchen8/Algorithms/blob/master/Matrix.java
	private static double determinant(double[][] matrix) {
		if (matrix.length == 2)
			return matrix[0][0] * matrix[1][1] - matrix[0][1] * matrix[1][0];
		double det = 0;
		for (int i = 0; i < matrix[0].length; i++)
			det += Math.pow(-1, i) * matrix[0][i]
					* determinant(minor(matrix, 0, i));
		return det;
	}
	
	//adapted from https://github.com/rchen8/Algorithms/blob/master/Matrix.java
	private static double[][] minor(double[][] matrix, int row, int column) {
		double[][] minor = new double[matrix.length - 1][matrix.length - 1];

		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; i != row && j < matrix[i].length; j++)
				if (j != column)
					minor[i < row ? i : i - 1][j < column ? j : j - 1] = matrix[i][j];
		return minor;
	}
	//adapted from https://github.com/rchen8/Algorithms/blob/master/Matrix.java
	private void inverseMatrix(double[][] matrix) {
		double[][] inverse = new double[matrix.length][matrix.length];

		// minors and cofactors
		for (int i = 0; i < matrix.length; i++)
			for (int j = 0; j < matrix[i].length; j++)
				inverse[i][j] = Math.pow(-1, i + j)
						* determinant(minor(matrix, i, j));

		// adjugate and determinant
		double det = 1.0 / determinant(matrix);
		for (int i = 0; i < inverse.length; i++) {
			for (int j = 0; j <= i; j++) {
				double temp = inverse[i][j];
				inverse[i][j] = inverse[j][i] * det;
				inverse[j][i] = temp * det;
			}
		}

		this.M= inverse;
	}
	
	public void inverse() {
		inverseMatrix(M);
	}
	
	
}
