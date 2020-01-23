package client.interpreter;

import geometry.Vertex3D;
import polygon.Chain;

public class Clipper {
	double highZ;	//front
	double lowZ; 	//back
	double highY; 	//top
	double lowY;  	//btm
	double highX; 	//right
	double lowX; 	//left
	double d = -1; //viewPlane
	
	public Clipper(double xLow, double yLow, double xHigh, double yHigh, double front, double back) {
		lowX = xLow;//*650+650;
		lowY = yLow;//*650+650;
		highX = xHigh;//*325+325;
		highY = yHigh;
		highZ = front;
		lowZ = back;
	}
	
	public Chain zClip(Vertex3D[] verArr) {
		Chain chain = new Chain();
		//System.out.println("Into z-Clip: "+verArr.length);

		chain = closeZClip(verArr);
		//System.out.println("out near clip: " + chain.length());
		chain = farZClip(chain);
		//System.out.println("out far clip: " + chain.length());
		return chain;
	}
	
	private Chain closeZClip(Vertex3D[] verArr) {
		Chain chain = new Chain();
		int n = verArr.length;
		
		for(int i =0; i<n; i++) {
			Vertex3D v_0 = verArr[i];
			Vertex3D v_1 = verArr[(i+1)%n];	
			if(v_0.getZ()<= highZ && v_1.getZ()<=highZ) {
				//both inside
				//add second point
				chain.add(v_1);

			} else if (v_1.getZ()>highZ && v_0.getZ()>highZ) {
				//both outside do nothing
				//add nothing
			} else {
				// calculate intersection with plane because exactly one point is outside the plane
			
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
				double dz = v_1.getZ() - v_0.getZ();
			
				//get known intersection value
				double iz = highZ;
			
				//Calculate ratio
				double r = (iz-v_0.getZ())/dz;
			
				//get unknown intersection values
				double ix = v_0.getX()+(dx*r);
				double iy = v_0.getY()+(dy*r);
			
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
				if(v_0.getZ() <= highZ && v_1.getZ() > highZ) {
					//inside and outside
					//add intersect only
					chain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					chain.add(iVertex);
					chain.add(v_1);
				}
			}	
		}
		return chain;
	}
	
	private Chain farZClip(Chain oldChain) {
		Chain chain = new Chain();
		int  n = oldChain.length();
		
		for(int i =0; i<n; i++) {
			Vertex3D v_0 = oldChain.get(i);
			Vertex3D v_1 = oldChain.get((i+1)%n);	
		
			if(v_0.getZ()>= lowZ && v_1.getZ()>=lowZ) {
				//both inside
				//add second point
				chain.add(v_1);
			} else if (v_0.getZ()<lowZ && v_1.getZ()<lowZ) {
				//both outside do nothing
				//add nothing
			} else {
				// calculate intersection with plane because exactly one point is outside the plane
			
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
				double dz = v_1.getZ() - v_0.getZ();
			
				//get known intersection value
				double iz = lowZ;
			
				//Calculate ratio
				double r = (iz-v_0.getZ())/dz;
			
				//get unknown intersection values
				double ix = v_0.getX()+(dx*r);
				double iy = v_0.getY()+(dy*r);
			
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
			
				if(v_0.getZ() >= lowZ && v_1.getZ() < lowZ) {
					//inside and outside
					//add intersect only
					chain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					chain.add(iVertex);
					chain.add(v_1);
				}
			}	
		}	
		return chain;
	}
	
	public Chain postProjClip(Vertex3D[] verArr){
		Chain chain = new Chain();
		//System.out.println("in: "+ verArr.length);
		chain = topClip(verArr);
		//System.out.println("out top: "+ chain.length());
		chain = btmClip(chain);
		//System.out.println("out btm: "+ chain.length());
		chain = leftClip(chain);
		//System.out.println("out L: "+ chain.length());
		chain = rightClip(chain);
		//System.out.println("out R: "+ chain.length());
		
		return chain;
	}
	
	private Chain topClip(Vertex3D[] verArr) {
		Chain chain = new Chain();
		int n = verArr.length;
		//loop through every set on neighbouring nodes and perform clip top clip
		for(int i =0; i < n; i++ ) {
			Vertex3D v_0 = verArr[i];
			Vertex3D v_1 = verArr[(i+1)%n];	
		//	System.out.println("yvals: " + v_0.getY() + v_1.getY());
			//4 cases
			if(v_0.getY()<= highY && v_1.getY()<=highY) {
				//both in -> add second point
				chain.add(v_1);
			} else if(v_0.getY() > highY && v_1.getY() > highY) {
				//both out --> trivial reject
			} else {
				//one in one out --> calculate intersection point
				// extra math adjusting for perspective and depth
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
				
				//get known intersection value
				double iy = highY;
			
				//Calculate ratio
				double r = (iy-v_0.getY())/dy;
			
				//get unknown intersection values
				double ix = v_0.getX()+(dx*r);
				
				//get adjusted z-values
				double iz = getIZ(v_0, v_1,r);
				
				//CameraSpace deltas
				double dCSX = v_1.getCameraSpaceX()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceX()/v_0.getCameraSpaceZ();
				double dCSY = v_1.getCameraSpaceY()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceY()/v_0.getCameraSpaceZ();
				double dCSZ = 1/v_1.getCameraSpaceZ() - 1/v_0.getCameraSpaceZ();
				//intersect CS
				double iCSZ = 1/(1/v_0.getZ()+dCSZ*r);
				double iCSX = (v_0.getCameraSpaceX()/v_0.getZ()+ dCSX*r)*iCSZ;
				double iCSY = (v_0.getCameraSpaceY()/v_0.getZ()+ dCSY*r)*iCSZ;
				
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
				iVertex.setCameraSpaceBuffer(iCSX, iCSY, iCSZ);
				
				
				//determine in-out or out-in case
				if(v_0.getY() <= highY && v_1.getY() > highY) {
					//inside and outside
					//add intersect only
					chain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					chain.add(iVertex);
					chain.add(v_1);
				}
			} 
		}
		return chain;
	}

	private Chain btmClip(Chain oldChain) {
		Chain newChain = new Chain();
		int n = oldChain.length();
		
		//loop through every set on neighboring nodes and perform clip top clip
		for(int i =0; i < n; i++) {
			Vertex3D v_0 = oldChain.get(i);
			Vertex3D v_1 = oldChain.get((i+1)%n);	
			
			//4 cases
			if(v_0.getY()>= lowY && v_1.getY()>=lowY) {
				//both in -> add second point
				newChain.add(v_1);
			} else if(v_0.getY() < lowY && v_1.getY() < lowY) {
				//both out --> trivial reject
			} else {
				//one in one out --> calculate intersection point
				// extra math adjusting for perspective and depth
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
			
				//get known intersection value
				double iy = lowY;
			
				//Calculate ratio
				double r = (iy-v_0.getY())/dy;
			
				//get unknown intersection values
				double ix = v_0.getX()+(dx*r);
				
				//get adjusted z-values
				double iz = getIZ(v_0, v_1, r);
				
				//CameraSpace deltas
				double dCSX = v_1.getCameraSpaceX()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceX()/v_0.getCameraSpaceZ();
				double dCSY = v_1.getCameraSpaceY()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceY()/v_0.getCameraSpaceZ();
				double dCSZ = 1/v_1.getCameraSpaceZ() - 1/v_0.getCameraSpaceZ();
				//intersect CS
				double iCSZ = 1/(1/v_0.getZ()+dCSZ*r);
				double iCSX = (v_0.getCameraSpaceX()/v_0.getZ()+ dCSX*r)*iCSZ;
				double iCSY = (v_0.getCameraSpaceY()/v_0.getZ()+ dCSY*r)*iCSZ;
				
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
				iVertex.setCameraSpaceBuffer(iCSX, iCSY, iCSZ);
				
				//determine in-out or out-in case
				if(v_0.getY() >= lowY && v_1.getY() < lowY) {
					//inside and outside
					//add intersect only
					newChain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					newChain.add(iVertex);
					newChain.add(v_1);
				}
			} 
		}
		return newChain;
	}
	
	private Chain leftClip(Chain oldChain) {
		Chain newChain = new Chain();
		int n = oldChain.length();
		
		//loop through every set on neighboring nodes and perform top clip
		for(int i =0; i < n; i++ ) {
			Vertex3D v_0 = oldChain.get(i);
			Vertex3D v_1 = oldChain.get((i+1)%n);	
			
			//4 cases
			if(v_0.getX()>= lowX && v_1.getX()>=lowX) {
				//both in -> add second point
				newChain.add(v_1);
			} else if(v_0.getX() < lowX && v_1.getX() < lowX) {
				//both out --> trivial reject
			} else {
				//one in one out --> calculate intersection point
				// extra math adjusting for perspective and depth
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
				//get known intersection value
				double ix = lowX;
			
				//Calculate ratio
				double r = (ix-v_0.getX())/dx;
			
				//get unknown intersection values
				double iy = v_0.getY()+(dy*r);
				
				//get adjusted z-values
				double iz = getIZ(v_0, v_1, r);
				
				
				//CameraSpace deltas
				double dCSX = v_1.getCameraSpaceX()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceX()/v_0.getCameraSpaceZ();
				double dCSY = v_1.getCameraSpaceY()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceY()/v_0.getCameraSpaceZ();
				double dCSZ = 1/v_1.getCameraSpaceZ() - 1/v_0.getCameraSpaceZ();
				//intersect CS
				double iCSZ = 1/(1/v_0.getZ()+dCSZ*r);
				double iCSX = (v_0.getCameraSpaceX()/v_0.getZ()+ dCSX*r)*iCSZ;
				double iCSY = (v_0.getCameraSpaceY()/v_0.getZ()+ dCSY*r)*iCSZ;
				
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
				iVertex.setCameraSpaceBuffer(iCSX, iCSY, iCSZ);
				
				//determine in-out or out-in case
				if(v_0.getX() >= lowX && v_1.getX() < lowX) {
					//inside and outside
					//add intersect only
					newChain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					newChain.add(iVertex);
					newChain.add(v_1);
				}
			} 
		}
		return newChain;
	}

	private Chain rightClip(Chain oldChain) {
		Chain newChain = new Chain();
		int n = oldChain.length();
		
		//loop through every set on neighboring nodes and perform clip top clip
		for(int i =0; i < n; i++ ) {
			Vertex3D v_0 = oldChain.get(i);
			Vertex3D v_1 = oldChain.get((i+1)%n);	
			
			//4 cases
			if(v_0.getX()<= highX && v_1.getX() <= highX) {
				//both in -> add second point
				newChain.add(v_1);
				
			} else if(v_0.getX() > highX  && v_1.getX() > highX) {
				//both out --> trivial reject
			} else {
				//one in one out --> calculate intersection point
				//extra math adjusting for perspective and depth
				//get delta values
				double dx = v_1.getX() - v_0.getX();
				double dy = v_1.getY() - v_0.getY();
				
			
				//get known intersection value
				double ix = highX;
			
				//Calculate ratio
				double r = (ix-v_0.getX())/dx;
			
				//get unknown intersection values
				double iy = v_0.getY()+(dy*r);
				
				//get adjusted z-values
				double iz = getIZ(v_0, v_1, r);
				
				//CameraSpace deltas
				double dCSX = v_1.getCameraSpaceX()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceX()/v_0.getCameraSpaceZ();
				double dCSY = v_1.getCameraSpaceY()/v_1.getCameraSpaceZ() - v_0.getCameraSpaceY()/v_0.getCameraSpaceZ();
				double dCSZ = 1/v_1.getCameraSpaceZ() - 1/v_0.getCameraSpaceZ();
				//intersect CS
				double iCSZ = 1/((1/v_0.getZ())+dCSZ*r);
				double iCSX = (v_0.getCameraSpaceX()/v_0.getZ()+ dCSX*r)*iCSZ;
				double iCSY = (v_0.getCameraSpaceY()/v_0.getZ()+ dCSY*r)*iCSZ;
				
				//create intersection vertex
				Vertex3D iVertex = new Vertex3D(ix,iy,iz,v_0.getColor());
				iVertex.setCameraSpaceBuffer(iCSX, iCSY, iCSZ);
				
				//determine in-out or out-in case
				if(v_0.getX() <= highX && v_1.getX() > highX) {
					//inside and outside
					//add intersect only
					newChain.add(iVertex);
				} else {
					//outside and inside
					//add intersect and v_2
					newChain.add(iVertex);
					newChain.add(v_1);
				}
			} 
		}
		return newChain;
	}
	
	private double getIZ(Vertex3D p0, Vertex3D p1, double ratio) {
		//get dz
		double delta = (1/p1.getZ())-(1/p0.getZ());
		//Determine Z intercept value
		double intersect = 1/((1/p0.getZ())+(delta*ratio));
		
		return intersect;
	}
}
