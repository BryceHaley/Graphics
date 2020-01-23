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
import geometry.Point3DH;
import geometry.Vertex3D;
import line.DDALineWithColorLERP;
import line.DDALineWithColorLERPforFPR;
import line.LineRenderer;
import line.LineRendererWithPoly;


public class FilledPolygonRenderer implements PolygonRenderer{
	
	private FilledPolygonRenderer() {}

	@SuppressWarnings("static-access")
	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, 
			FaceShader faceShader, VertexShader vertexShader, PixelShader pixelShader) {
		Vertex3D tempVertex = new Vertex3D();
		Chain tempVertices = new Chain();
		
		
		faceShader.shade(polygon);
		
		for(int i =0; i<3; i++) {
			tempVertex = vertexShader.shade(polygon, polygon.get(i));
			tempVertices.add(tempVertex);
		}
		
		Polygon tempPoly = Polygon.make(tempVertices.get(0), tempVertices.get(1), tempVertices.get(2));
		tempPoly.setColor(polygon.getColor());
		polygon = tempPoly;
		
		
		Chain lChain = polygon.leftChain();
		Chain rChain = polygon.rightChain();
		
		//System.out.println("in poly renderer");
		
		if (lChain.length()==1 && rChain.length()==1) {
			return;
		}
		
		if(polygon.isClockwise(polygon.get(0), polygon.get(1), polygon.get(2))==true) {
			return;
		}
	
		Color color1;
		Color color2;
		
		LineRendererWithPoly DDA = DDALineWithColorLERPforFPR.make();
		
		//variables
		double mLeft;

		double mNormXLeft;
		double mNormYLeft;
		double mNormZLeft;
		
		double mRight;
		double mNormXRight;
		double mNormYRight;
		double mNormZRight;
		
		double fxLeft;
		double frLeft;
		double fgLeft;
		double fbLeft;
		double fzLeft;
		double fNormXLeft;
		double fNormYLeft;
		double fNormZLeft;
		
		double fxRight;
		double frRight;
		double fgRight;
		double fbRight;
		double fzRight;
		double fNormXRight;
		double fNormYRight;
		double fNormZRight;
		
		double drRight;
		double dgRight;
		double dbRight;
		
		double drLeft;
		double dgLeft;
		double dbLeft;
		
		double dy;
		
		int xLeft;
		double xNormLeft;
		double yNormLeft;
		double zNormLeft;
		
		int xRight;
		
		double xNormRight;
		double yNormRight;
		double zNormRight;
		
		int top;
		int mdl;
		int btm;
		
		Vertex3D p1;
		Vertex3D p2;
		boolean leftLong;
		
		//get initial slopes values and pixel colour
		leftLong = lChain.length()==3;
		p1 = lChain.get(0);
		p2 = lChain.get(lChain.length()-1);
		top=p1.getIntY();
		btm=p2.getIntY();
		
		fxLeft = p1.getIntX();
		p1 = rChain.get(0);
		fxRight = p1.getIntX();
		
		frLeft = p1.getColor().getR();
		fgLeft = p1.getColor().getG();
		fbLeft = p1.getColor().getB();
		fzLeft = p1.getZ();
		
		//perpective correct interpolation
		double CSZfrLeft = frLeft/fzLeft;
		double CSZfgLeft = fgLeft /fzLeft;
		double CSZfbLeft = fbLeft/fzLeft;
		
		double CSZfzLeft = 1/fzLeft;
		
		fNormXLeft = p1.getNormal().getX()/fzLeft;
		fNormYLeft = p1.getNormal().getY()/fzLeft;
		fNormZLeft = p1.getNormal().getZ()/fzLeft;
//		
//		System.out.println(p1.getNormal().toString());
//		System.out.println("Norm xyz "+fNormXLeft+", "+fNormYLeft+", "+fNormZLeft);
//		System.out.println("vert XYZ: "+ p1.getX()+ ", "+p1.getY()+", "+ p2.getZ());
//		System.out.println("CSXYZ: "+ p1.getCameraSpaceX()+ ", "+p1.getCameraSpaceY()+ ", "+p1.getCameraSpaceZ());
//		
		fNormXRight = fNormXLeft;
		fNormYRight = fNormYLeft;
		fNormZRight = fNormZLeft;
		
		double csXLeft = p1.getCameraSpaceX()/fzLeft;
		double csYLeft = p1.getCameraSpaceY()/fzLeft;
		double csZLeft = p1.getCameraSpaceZ()/fzLeft;
		
		double csXRight = csXLeft;
		double csYRight = csYLeft;
		double csZRight = csZLeft;
		
		double CSZfrRight = CSZfrLeft;
		double CSZfgRight = CSZfgLeft;
		double CSZfbRight = CSZfbLeft;
		double CSZfzRight = CSZfzLeft;
		
		frRight =frLeft;
		fgRight = fgLeft;
		fbRight = fbLeft;
		fzRight = fzLeft;
		
		double dCSXLeft, dCSYLeft, dCSZLeft, dCSXRight, dCSYRight, dCSZRight; 
		
		//determine which side of the triangle has 3 points and set the middle point
		if(leftLong) {
			p1 = lChain.get(1);
			mdl = p1.getIntY();
		} else {
			p1 = rChain.get(1);
			mdl = p1.getIntY();
		}
		
		//get slopes
		
		dy = lChain.get(0).getY()-lChain.get(1).getY();
		mLeft = getInvSlope(lChain.get(0).getX(),lChain.get(1).getX(),dy);
	
		
		dy = rChain.get(0).getY()-rChain.get(1).getY();
		mRight= getInvSlope(rChain.get(0).getX(),rChain.get(1).getX(),dy);
	
		//get perspective correct slope values
		dy = lChain.get(0).getIntY()-lChain.get(1).getIntY();
		
		double z0 = lChain.get(0).getZ();
		double z1 = lChain.get(1).getZ();
		
		double CSZmrLeft = getPerCorInvSlope(lChain.get(0).getColor().getR(),lChain.get(1).getColor().getR(),z0,z1,dy);
		double CSZmgLeft =getPerCorInvSlope(lChain.get(0).getColor().getG(),lChain.get(1).getColor().getG(),z0,z1,dy);
		double CSZmbLeft = getPerCorInvSlope(lChain.get(0).getColor().getB(),lChain.get(1).getColor().getB(),z0,z1,dy);
		double CSZmzLeft = getPerCorInvSlope(1,1,z0,z1,dy);
		
		mNormXLeft = getPerCorInvSlope(lChain.get(0).getNormal().getX(), lChain.get(1).getNormal().getX(),z0,z1,dy);
		mNormYLeft = getPerCorInvSlope(lChain.get(0).getNormal().getY(), lChain.get(1).getNormal().getY(),z0,z1,dy);
		mNormZLeft = getPerCorInvSlope(lChain.get(0).getNormal().getZ(), lChain.get(1).getNormal().getZ(),z0,z1,dy);
		
		double mCSXLeft = getPerCorInvSlope(lChain.get(0).getCameraSpaceX(),lChain.get(1).getCameraSpaceX(),z0,z1,dy);
		double mCSYLeft = getPerCorInvSlope(lChain.get(0).getCameraSpaceY(),lChain.get(1).getCameraSpaceY(),z0,z1,dy);
		double mCSZLeft = getPerCorInvSlope(1,1,lChain.get(0).getCameraSpaceZ(),lChain.get(1).getCameraSpaceZ(),dy);
		
		dy = rChain.get(0).getIntY()-rChain.get(1).getIntY();
		
		z0 = rChain.get(0).getZ();
		z1 = rChain.get(1).getZ();
		
		double CSZmrRight = getPerCorInvSlope(rChain.get(0).getColor().getR(),rChain.get(1).getColor().getR(), z0, z1, dy);
		double CSZmgRight = getPerCorInvSlope(rChain.get(0).getColor().getG(),rChain.get(1).getColor().getG(), z0, z1, dy);
		double CSZmbRight = getPerCorInvSlope(rChain.get(0).getColor().getB(),rChain.get(1).getColor().getB(), z0, z1, dy);
		double CSZmzRight = getPerCorInvSlope(1,1,z0,z1,dy);
		
		mNormXRight = getPerCorInvSlope(rChain.get(0).getNormal().getX(), rChain.get(1).getNormal().getX(),z0,z1,dy);
		mNormYRight = getPerCorInvSlope(rChain.get(0).getNormal().getY(), rChain.get(1).getNormal().getY(),z0,z1,dy);
		mNormZRight = getPerCorInvSlope(rChain.get(0).getNormal().getZ(), rChain.get(1).getNormal().getZ(),z0,z1,dy);
		
		double mCSXRight = getPerCorInvSlope(rChain.get(0).getCameraSpaceX(),rChain.get(1).getCameraSpaceX(),z0,z1,dy);
		double mCSYRight = getPerCorInvSlope(rChain.get(0).getCameraSpaceY(),rChain.get(1).getCameraSpaceY(),z0,z1,dy);
		double mCSZRight = CSZmzRight;//getPerCorInvSlope(1,1,rChain.get(0).getCameraSpaceZ(),rChain.get(1).getCameraSpaceZ(),dy);
		
		// skip initial point for infinite slope case
		if(Double.isInfinite(mLeft)) {
			if(lChain.length()==2) { return;}
			dy = lChain.get(1).getY()-lChain.get(2).getY();
			mLeft = getInvSlope(lChain.get(1).getX(),lChain.get(2).getX(),dy);
			
			//get perspective correct values
			z0 = lChain.get(1).getZ();
			z1 = lChain.get(2).getZ();
			CSZmzLeft = getPerCorInvSlope(1,1,z0,z1,dy);
			CSZmrLeft = getPerCorInvSlope(lChain.get(1).getColor().getR(),lChain.get(2).getColor().getR(),z0,z1,dy);
			CSZmgLeft = getPerCorInvSlope(lChain.get(1).getColor().getG(),lChain.get(2).getColor().getG(),z0,z1,dy);
			CSZmbLeft = getPerCorInvSlope(lChain.get(1).getColor().getB(),lChain.get(2).getColor().getB(),z0,z1,dy);
			
			p1 = lChain.get(1);
			
			fxLeft = p1.getX();
			fzLeft = p1.getZ();
			frLeft = p1.getColor().getR();
			fgLeft = p1.getColor().getG();
			fbLeft = p1.getColor().getB();
			
			//perspective correct values
			CSZfzLeft = 1/fzLeft;
			CSZfrLeft = frLeft/fzLeft;
			CSZfgLeft = fgLeft/fzLeft;
			CSZfbLeft = fbLeft/fzLeft;
			
			mNormXLeft = getPerCorInvSlope(lChain.get(1).getNormal().getX(), lChain.get(2).getNormal().getX(),z0,z1,dy);
			mNormYLeft = getPerCorInvSlope(lChain.get(1).getNormal().getY(), lChain.get(2).getNormal().getY(),z0,z1,dy);
			mNormZLeft = getPerCorInvSlope(lChain.get(1).getNormal().getZ(), lChain.get(2).getNormal().getZ(),z0,z1,dy);
			
			fNormXLeft = p1.getNormal().getX()/fzLeft;
			fNormYLeft = p1.getNormal().getY()/fzLeft;
			fNormZLeft = p1.getNormal().getZ()/fzLeft;
			
			csXLeft = p1.getCameraSpaceX()/fzLeft;
			csYLeft = p1.getCameraSpaceY()/fzLeft;
			csZLeft = p1.getCameraSpaceZ()/fzLeft;
			
			mCSXLeft = getPerCorInvSlope(lChain.get(1).getCameraSpaceX(),lChain.get(2).getCameraSpaceX(),z0,z1,dy);
			mCSYLeft = getPerCorInvSlope(lChain.get(1).getCameraSpaceY(),lChain.get(2).getCameraSpaceY(),z0,z1,dy);
			mCSZLeft = getPerCorInvSlope(1,1,lChain.get(1).getCameraSpaceZ(),lChain.get(2).getCameraSpaceZ(),dy);
			
			
			mdl = btm;
			btm=top;
		}
		
		if(Double.isInfinite(mRight)) {
			if(rChain.length()<3) {
				return;}
			dy = rChain.get(1).getY()-rChain.get(2).getY();
			mRight  = getInvSlope(rChain.get(1).getIntX(),rChain.get(2).getIntX(),dy);
			
			//perspective correct values
			z0 = rChain.get(1).getZ();
			z1 = rChain.get(2).getZ();
			CSZmzRight = getPerCorInvSlope(1,1,z0,z1,dy);
			CSZmrRight = getPerCorInvSlope(rChain.get(1).getColor().getR(),rChain.get(2).getColor().getR(),z0,z1,dy);
			CSZmgRight = getPerCorInvSlope(rChain.get(1).getColor().getG(),rChain.get(2).getColor().getG(),z0,z1,dy);
			CSZmbRight = getPerCorInvSlope(rChain.get(1).getColor().getB(),rChain.get(2).getColor().getB(),z0,z1,dy);
			
			p1 = rChain.get(1);
			
			fxRight = p1.getIntX();
			
			//perspective correct values
			CSZfzRight = 1/fzRight;
			CSZfrRight = frRight/fzRight;
			CSZfgRight = fgRight/fzRight;
			CSZfbRight = fbRight/fzRight;
			
			
			mNormXRight = getPerCorInvSlope(rChain.get(1).getNormal().getX(), rChain.get(2).getNormal().getX(),z0,z1,dy);
			mNormYRight = getPerCorInvSlope(rChain.get(1).getNormal().getY(), rChain.get(2).getNormal().getY(),z0,z1,dy);
			mNormZRight = getPerCorInvSlope(rChain.get(1).getNormal().getZ(), rChain.get(2).getNormal().getZ(),z0,z1,dy);
			
			fNormXRight = p1.getNormal().getX()/fzRight;
			fNormYRight = p1.getNormal().getY()/fzRight;
			fNormZRight = p1.getNormal().getZ()/fzRight;
			
			csXRight = p1.getCameraSpaceX()/fzRight;
			csYRight = p1.getCameraSpaceY()/fzRight;
			csZRight = p1.getCameraSpaceZ()/fzRight;
			
			mCSXRight = getPerCorInvSlope(rChain.get(1).getCameraSpaceX(),rChain.get(2).getCameraSpaceX(),z0,z1,dy);
			mCSYRight = getPerCorInvSlope(rChain.get(1).getCameraSpaceY(),rChain.get(2).getCameraSpaceY(),z0,z1,dy);
			mCSZRight = getPerCorInvSlope(1,1,rChain.get(1).getCameraSpaceZ(),rChain.get(2).getCameraSpaceZ(),dy);
			
			
			mdl = btm;
			btm=top;
		}
		
		for(int y =top; y>mdl; y--) {
			
			xLeft = (int)Math.round(fxLeft);
			
			drLeft = CSZfrLeft/CSZfzLeft;
			dgLeft = (CSZfgLeft/CSZfzLeft);
			dbLeft = (CSZfbLeft/CSZfzLeft);

			color1 = new Color(drLeft, dgLeft, dbLeft);
			p1 = new Vertex3D(xLeft, y, 1/CSZfzLeft, color1);
			
			xNormLeft = fNormXLeft/CSZfzLeft;
			yNormLeft = fNormYLeft/CSZfzLeft;
			zNormLeft = fNormZLeft/CSZfzLeft;
			
			Point3DH normal1 = new Point3DH(xNormLeft, yNormLeft, zNormLeft);
			p1.setNormal(normal1);
			p1.setHasNormal(true);
		//	System.out.println(normal1.toString());
			
			dCSXLeft = csXLeft/CSZfzLeft;
			dCSYLeft =csYLeft/CSZfzLeft;
			dCSZLeft =csZLeft/CSZfzLeft;
			
			p1.setCameraSpaceBuffer(dCSXLeft, dCSYLeft, dCSZLeft);
			
			xRight = (int)Math.round(fxRight);
			
			drRight = (CSZfrRight/CSZfzRight);
			dgRight = (CSZfgRight/CSZfzRight);
			dbRight = (CSZfbRight/CSZfzRight);

			color2 = new Color(drRight, dgRight, dbRight); 
			p2 = new Vertex3D(xRight, y, 1/CSZfzRight, color2);
			
			xNormRight = fNormXRight/CSZfzRight;
			yNormRight = fNormYRight/CSZfzRight;
			zNormRight = fNormZRight/CSZfzRight;
			
			
			
			Point3DH normal2 = new Point3DH(xNormRight, yNormRight, zNormRight);
			p2.setNormal(normal2);
			p2.setHasNormal(true);
			
			dCSXRight = csXRight/CSZfzRight;
			dCSYRight = csYRight/CSZfzRight;
			dCSZRight = csZRight/CSZfzRight;
			
			p2.setCameraSpaceBuffer(dCSXRight, dCSYRight, dCSZRight);
			
			DDA.drawLine(p1, p2, drawable,polygon,pixelShader);
			
			fxRight -= mRight;
			CSZfzRight -= CSZmzRight;
			CSZfrRight -= CSZmrRight;
			CSZfgRight -= CSZmgRight;
			CSZfbRight -= CSZmbRight;
			
			
			fxLeft -= mLeft;
			CSZfzLeft -= CSZmzLeft;
			CSZfrLeft -= CSZmrLeft;
			CSZfgLeft -= CSZmgLeft;
			CSZfbLeft -= CSZmbLeft;
			

			fNormXRight -= mNormXRight;
			fNormYRight -= mNormYRight;
			fNormZRight -= mNormZRight;
			
			fNormXLeft -= mNormXLeft;
			fNormYLeft -= mNormYLeft;
			fNormZLeft -= mNormZLeft;
			
			csXLeft -= mCSXLeft;
			csYLeft -= mCSYLeft;
			csZLeft -= mCSZLeft;
			
			csXRight -= mCSXRight;
			csYRight -= mCSYRight;
			csZRight -= mCSZRight;
			
			
		}
		
		//set second set of slopes
		if (leftLong) {
			dy = lChain.get(1).getIntY()-lChain.get(2).getIntY();
			mLeft = getInvSlope(lChain.get(1).getX(),lChain.get(2).getX(),dy);			
			
			//perCor
			z0 = lChain.get(1).getZ();
			z1 = lChain.get(2).getZ();
			CSZmzLeft = getPerCorInvSlope(1,1,z0,z1,dy);
			CSZmrLeft = getPerCorInvSlope(lChain.get(1).getColor().getR(),lChain.get(2).getColor().getR(),z0,z1,dy);
			CSZmgLeft = getPerCorInvSlope(lChain.get(1).getColor().getG(),lChain.get(2).getColor().getG(),z0,z1,dy);
			CSZmbLeft = getPerCorInvSlope(lChain.get(1).getColor().getB(),lChain.get(2).getColor().getB(),z0,z1,dy);
			
			//TODO: delete this
//			fNormXLeft = p2.getNormal().getX();
//			fNormYLeft = p2.getNormal().getY();
//			fNormZLeft = p2.getNormal().getZ();
//			
//			csXLeft = p2.getCameraSpaceX()/fzLeft;
//			csYLeft = p2.getCameraSpaceY()/fzLeft;
//			csZLeft = p2.getCameraSpaceZ()/fzLeft;
			
			mNormXLeft = getPerCorInvSlope(lChain.get(1).getNormal().getX(), lChain.get(2).getNormal().getX(),z0,z1,dy);
			mNormYLeft = getPerCorInvSlope(lChain.get(1).getNormal().getY(), lChain.get(2).getNormal().getY(),z0,z1,dy);
			mNormZLeft = getPerCorInvSlope(lChain.get(1).getNormal().getZ(), lChain.get(2).getNormal().getZ(),z0,z1,dy);
			
			mCSXLeft = getPerCorInvSlope(lChain.get(1).getCameraSpaceX(),lChain.get(2).getCameraSpaceX(),z0,z1,dy);
			mCSYLeft = getPerCorInvSlope(lChain.get(1).getCameraSpaceY(),lChain.get(2).getCameraSpaceY(),z0,z1,dy);
			mCSZLeft = getPerCorInvSlope(1,1,lChain.get(1).getCameraSpaceZ(),lChain.get(2).getCameraSpaceZ(),dy);
			
		}else {
			dy = rChain.get(1).getIntY()-rChain.get(2).getIntY();
			mRight = getInvSlope(rChain.get(1).getIntX(),rChain.get(2).getIntX(),dy);
			
			//perCor
			
			z0 = rChain.get(1).getZ();
			z1 = rChain.get(2).getZ();
			CSZmzRight = getPerCorInvSlope(1,1,z0,z1,dy);
			CSZmrRight = getPerCorInvSlope(rChain.get(1).getColor().getR(),rChain.get(2).getColor().getR(),z0,z1,dy);
			CSZmgRight = getPerCorInvSlope(rChain.get(1).getColor().getG(),rChain.get(2).getColor().getG(),z0,z1,dy);
			CSZmbRight = getPerCorInvSlope(rChain.get(1).getColor().getB(),rChain.get(2).getColor().getB(),z0,z1,dy);	
			
			mNormXRight = getPerCorInvSlope(rChain.get(1).getNormal().getX(), rChain.get(2).getNormal().getX(),z0,z1,dy);
			mNormYRight = getPerCorInvSlope(rChain.get(1).getNormal().getY(), rChain.get(2).getNormal().getY(),z0,z1,dy);
			mNormZRight = getPerCorInvSlope(rChain.get(1).getNormal().getZ(), rChain.get(2).getNormal().getZ(),z0,z1,dy);
			
			mCSXRight = getPerCorInvSlope(rChain.get(1).getCameraSpaceX(),rChain.get(2).getCameraSpaceX(),z0,z1,dy);
			mCSYRight = getPerCorInvSlope(rChain.get(1).getCameraSpaceY(),rChain.get(2).getCameraSpaceY(),z0,z1,dy);
			mCSZRight = CSZmzRight;
		}
		
		for(int y = mdl; y>btm; y--) {
			csZRight -= mCSZRight;
			xLeft = (int)Math.round(fxLeft);
			
			drLeft = CSZfrLeft/CSZfzLeft;
			dgLeft = (CSZfgLeft/CSZfzLeft);
			dbLeft = (CSZfbLeft/CSZfzLeft);
			
			color1 = new Color(drLeft, dgLeft, dbLeft);

			p1 = new Vertex3D(xLeft, y, 1/CSZfzLeft, color1);
			
			xNormLeft = fNormXLeft/CSZfzLeft;
			yNormLeft = fNormYLeft/CSZfzLeft;
			zNormLeft = fNormZLeft/CSZfzLeft;
			
			Point3DH normal1 = new Point3DH(xNormLeft, yNormLeft, zNormLeft);
			p1.setNormal(normal1);
			p1.setHasNormal(true);
			
			dCSXLeft = csXLeft/CSZfzLeft;
			dCSYLeft = csYLeft/CSZfzLeft;
			dCSZLeft = csZLeft/CSZfzLeft;
			
			p1.setCameraSpaceBuffer(dCSXLeft, dCSYLeft, dCSZLeft);
			
			xRight = (int)Math.round(fxRight);
			
			drRight = (CSZfrRight/CSZfzRight);
			dgRight = (CSZfgRight/CSZfzRight);
			dbRight = (CSZfbRight/CSZfzRight);
			
			color2= new Color(drRight, dgRight, dbRight);
			p2 = new Vertex3D(xRight, y, 1/CSZfzRight, color2);
			
			xNormRight = fNormXRight/CSZfzRight;
			yNormRight = fNormYRight/CSZfzRight;
			zNormRight = fNormZRight/CSZfzRight;
			
			Point3DH normal2 = new Point3DH(xNormRight, yNormRight, zNormRight);
			p2.setNormal(normal2);
			p2.setHasNormal(true);
			
			dCSXRight = csXRight/CSZfzRight;
			dCSYRight = csYRight/CSZfzRight;
			dCSZRight = csZRight/CSZfzRight;
			
			p2.setCameraSpaceBuffer(dCSXRight, dCSYRight, dCSZRight);
			
			DDA.drawLine(p1, p2, drawable, polygon, pixelShader);
			
			fxRight -= mRight;
			CSZfzRight -= CSZmzRight;
			CSZfrRight -= CSZmrRight;
			CSZfgRight -= CSZmgRight;
			CSZfbRight -= CSZmbRight;
			
			fxLeft -= mLeft;
			CSZfzLeft -= CSZmzLeft;
			CSZfrLeft -= CSZmrLeft;
			CSZfgLeft -= CSZmgLeft;
			CSZfbLeft -= CSZmbLeft;
			
			fNormXRight -= mNormXRight;
			fNormYRight -= mNormYRight;
			fNormZRight -= mNormZRight;
			
			fNormXLeft -= mNormXLeft;
			fNormYLeft -= mNormYLeft;
			fNormZLeft -= mNormZLeft;
			
			csXLeft -= mCSXLeft;
			csYLeft -= mCSYLeft;
			csZLeft -= mCSZLeft;
			
			csXRight -= mCSXRight;
			csYRight -= mCSYRight;
			csZRight -= mCSZRight;
		}
	}
	
	public static PolygonRenderer make(){
		return new FilledPolygonRenderer();
		
	}
	
	public double getInvSlope(double p1, double p2, double dy) {
		double dx = p1 - p2;
		double m = dx/dy;
		return m;
	}
	
	public double getPerCorInvSlope(double p1, double p2,double z1, double z2, double dy) {
		double dx = p1/z1 - p2/z2;
		double m = dx/dy;
		return m;
	}

	@Override
	public void drawPolygon(Polygon polygon, Drawable drawable, Shader vertexShader) {
		System.out.println("oldPoly - You shoudl not be here");
		
	}
}

//System.out.println("First CSX Slope");
//System.out.println(mCSXLeft+", "+mCSXRight);
//System.out.println(mCSYLeft+", "+mCSYRight);
//System.out.println(mCSZLeft+", "+mCSZRight);
//
//System.out.println("First normie Slope");
//System.out.println(mNormXLeft+", "+mNormXRight);
//System.out.println(mNormYLeft+", "+mNormYRight);
//System.out.println(mNormZLeft+", "+mNormZRight);

// set the first "half" of pixels
//System.out.println("Top LeftNormsSlope: "+ mNormXLeft + ",  " + mNormYLeft+ ", "+ mNormZLeft );
//System.out.println("Top RightNormsSlope: "+ mNormXRight + ",  " + mNormYRight+ ", "+ mNormZRight );
//
//System.out.println("top LeftCS Slope: "+ mCSXLeft + ",  " + mCSYLeft+ ", "+ mCSZLeft );
//System.out.println("top RightCS Slope: "+ mCSXRight + ",  " + mCSYRight+ ", "+ mCSZRight );
//System.out.println("top half poly fill");

//TODO: delete this
//mCSXLeft =-0.0017404051043614407;
//mCSXRight =0;
//mCSYLeft = -0.0030900283399223957;
//mCSYRight = -0.0030916603940010848;
//mCSZLeft = 0;
//mCSZRight = 0;


//xLeft = (int)Math.round(fxLeft);
//		
//		drLeft = CSZfrLeft/CSZfzLeft;
//		dgLeft = (CSZfgLeft/CSZfzLeft);
//		dbLeft = (CSZfbLeft/CSZfzLeft);
//
//		color1 = new Color(drLeft, dgLeft, dbLeft);
//		p1 = new Vertex3D(xLeft, y, 1/CSZfzLeft, color1);
//		
//		xNormLeft = fNormXLeft;
//		yNormLeft = fNormYLeft;
//		zNormLeft = fNormZLeft;
//		
//		Point3DH normal1 = new Point3DH(xNormLeft, yNormLeft, zNormLeft);
//		p1.setNormal(normal1);
//		p1.setHasNormal(true);
//	//	System.out.println(normal1.toString());
//		
//		dCSXLeft = csXLeft/CSZfzLeft;
//		dCSYLeft = csYLeft/CSZfzLeft;
//		dCSZLeft = csZLeft/CSZfzLeft;
//		
//		p1.setCameraSpaceBuffer(dCSXLeft, dCSYLeft, dCSZLeft);
//		
//		xRight = (int)Math.round(fxRight);
//		
//		drRight = (CSZfrRight/CSZfzRight);
//		dgRight = (CSZfgRight/CSZfzRight);
//		dbRight = (CSZfbRight/CSZfzRight);
//
//		color2 = new Color(drRight, dgRight, dbRight); 
//		p2 = new Vertex3D(xRight, y, 1/CSZfzRight, color2);
//		
//		xNormRight = fNormXRight;
//		yNormRight = fNormYRight;
//		zNormRight = fNormZRight;
//		
//		
//		
//		Point3DH normal2 = new Point3DH(xNormRight, yNormRight, zNormRight);
//		p2.setNormal(normal2);
//		p2.setHasNormal(true);
//		
//		dCSXRight = csXRight/CSZfzRight;
//		dCSYRight = csYRight/CSZfzRight;
//		dCSZRight = csZRight/CSZfzRight;
//		
//		p2.setCameraSpaceBuffer(dCSXRight, dCSYRight, dCSZRight);
//		
//		DDA.drawLine(p1, p2, drawable,polygon,pixelShader);
//		
//		fxRight -= mRight;
//		CSZfzRight -= CSZmzRight;
//		CSZfrRight -= CSZmrRight;
//		CSZfgRight -= CSZmgRight;
//		CSZfbRight -= CSZmbRight;
//		
//		
//		fxLeft -= mLeft;
//		CSZfzLeft -= CSZmzLeft;
//		CSZfrLeft -= CSZmrLeft;
//		CSZfgLeft -= CSZmgLeft;
//		CSZfbLeft -= CSZmbLeft;
//		
//
//		fNormXRight -= 0;//mNormXRight;
//		fNormYRight -= 0;//mNormYRight;
//		fNormZRight -= 0;//mNormZRight;
//		
//		fNormXLeft -= 0;//mNormXLeft;
//		fNormYLeft -= 0;//mNormYLeft;
//		fNormZLeft -= 0;//mNormZLeft;
//		
//		csXLeft -= mCSXLeft;
//		csYLeft -= mCSYLeft;
//		csZLeft -= mCSZLeft;
//		
//		csXRight -= mCSXRight;
//		csYRight -= mCSYRight;

//System.out.println("btm LeftNormsSlope: "+ mNormXLeft + ",  " + mNormYLeft+ ", "+ mNormZLeft );
//System.out.println("btm RightNormsSlope: "+ mNormXRight + ",  " + mNormYRight+ ", "+ mNormZRight );
//
//System.out.println("btm LeftCS Slope: "+ mCSXLeft + ",  " + mCSYLeft+ ", "+ mCSZLeft );
//System.out.println("btm RightCS Slope: "+ mCSXRight + ",  " + mCSYRight+ ", "+ mCSZRight );
// //set second half of pixels (if no initial infinite slope)
//System.out.println("btm half poly fill");
//mCSXLeft = 0.003024989824247266;
//mCSXRight = 0;
//mCSYLeft = -0.0030944970594235684;
//mCSYRight = -0.0030916603940010848;
//mCSZLeft = 0;
//mCSZRight = 0;
//
//mNormXLeft = 0;
//mNormXRight = 0;
//mNormYLeft = 0;
//mNormYRight =0;
//mNormZLeft = 0;
//mNormZRight = 0;
//

//System.out.println("Second CSX Slope");
//System.out.println(mCSXLeft+", "+mCSXRight);
//System.out.println(mCSYLeft+", "+mCSYRight);
//System.out.println(mCSZLeft+", "+mCSZRight);
//
//System.out.println("Second normie Slope");
//System.out.println(mNormXLeft+", "+mNormXRight);
//System.out.println(mNormYLeft+", "+mNormYRight);
//System.out.println(mNormZLeft+", "+mNormZRight);
//System.out.println();