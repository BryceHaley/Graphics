package geometry;

import windowing.graphics.Color;

public class Vertex3D implements Vertex {
	protected Point3DH point;
	protected Color color;
//	private double normalX;
//	private double normalY;
//	private double normalZ;
	private Point3DH normal;
	private boolean hasNormal;
	private boolean doPhong;
	
	public Vertex3D(Point3DH point, Color color) {
		super();
		this.point = point;
		this.color = color;
	}
	public Vertex3D(double x, double y, double z, Color color) {
		this(new Point3DH(x, y, z), color);
	}

	public Vertex3D() {
	}
	public double getX() {
		return point.getX();
	}
	public double getY() {
		return point.getY();
	}
	public double getZ() {
		return point.getZ();
	}
	public Point getPoint() {
		return point;
	}
	public Point3DH getPoint3D() {
		return point;
	}
	
	public int getIntX() {
		return (int) Math.round(getX());
	}
	public int getIntY() {
		return (int) Math.round(getY());
	}
	public int getIntZ() {
		return (int) Math.round(getZ());
	}
	
	public Color getColor() {
		return color;
	}
	
	public Vertex3D rounded() {
		return new Vertex3D(point.round(), color);
	}
	public Vertex3D add(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.add(other3D.getPoint()),
				            color.add(other3D.getColor()));
	}
	public Vertex3D subtract(Vertex other) {
		Vertex3D other3D = (Vertex3D)other;
		return new Vertex3D(point.subtract(other3D.getPoint()),
				            color.subtract(other3D.getColor()));
	}
	public Vertex3D scale(double scalar) {
		return new Vertex3D(point.scale(scalar),
				            color.scale(scalar));
	}
	public Vertex3D replacePoint(Point3DH newPoint) {
		return new Vertex3D(newPoint, color);
	}
	public Vertex3D replaceColor(Color newColor) {
		Vertex3D newVertex = new Vertex3D(point, newColor);
		newVertex.normal = this.normal;
		return newVertex;
	}
	public Vertex3D euclidean() {
		Point3DH euclidean = getPoint3D().euclidean();
		return replacePoint(euclidean);
	}
	
	public void setNormal(double x, double y, double z) {
		Point3DH tempNorm= new Point3DH(x,y,z);
		tempNorm = Maths.makeUnit(tempNorm);
		
		this.normal = tempNorm;
	}
	public void setNormal(Point3DH newNormal) {
		newNormal = Maths.makeUnit(newNormal);
		
		this.normal = newNormal;
	}
	
	public void setCameraSpaceBuffer() {
		point.setCameraSpaceBuffer();
	}
	
	public double getCameraSpaceX() {
		return point.getCameraSpaceX();
	}
	public double getCameraSpaceY() {
		return point.getCameraSpaceY();
	}
	public double getCameraSpaceZ() {
		return point.getCameraSpaceZ();
	}
	public void setCameraSpaceBuffer(double csx, double csy, double csz) {
		point.setCameraSpaceBuffer(csx,csy,csz);
	}
	
	public Point3DH getCameraSpaceBuffer() {
		Point3DH buffer = new Point3DH(point.getCameraSpaceX(),point.getCameraSpaceY(),
				point.getCameraSpaceZ());
		return buffer;
	}
	
	public Point3DH getNormal() {
		if(this.hasNormal==true) {
			return normal;
		}
		else {
			System.out.println("has no normal - assigned 0");
			return new Point3DH(0,0,0);
		}
	}
	
	public void setHasNormal(boolean normal) {
		this.hasNormal = normal;
	}
	
	public boolean getHasNormal() {
		return this.hasNormal;
	}
	
	public void setDoPhong(boolean phong) {
		this.doPhong = phong;
	}
	
	public boolean getDoPhong() {
		return this.doPhong;
	}
	
	public String toString() {
		return "(" + getX() + ", " + getY() + ", " + getZ() + ", " + getColor().toIntString() + ")";
	}
	
	public String toIntString() {
		return "(" + getIntX() + ", " + getIntY() + getIntZ() + ", " + ", " + getColor().toIntString() + ")";
	}

}
