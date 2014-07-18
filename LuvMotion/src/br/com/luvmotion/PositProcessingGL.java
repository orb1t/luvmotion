package br.com.luvmotion;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.linear.Point2D;
import br.com.etyllica.linear.Point3D;
import br.com.etyllica.motion.core.features.Component;
import br.com.etyllica.motion.core.helper.RotationAxis;
import br.com.etyllica.motion.filter.color.ColorStrategy;
import br.com.etyllica.motion.filter.search.FloodFillSearch;
import br.com.etyllica.motion.modifier.PositCoplanarModifier;
import br.com.etyllica.motion.modifier.hull.AugmentedMarkerModifier;
import br.com.etyllica.motion.modifier.hull.FastConvexHullModifier;
import br.com.etyllica.motion.modifier.hull.HullModifier;

public class PositProcessingGL extends LuvMotionReality {

	//Image Processing Stuff
	protected FloodFillSearch cornerFilter;

	protected ColorStrategy colorStrategy;

	protected HullModifier hullModifier;
	
	private RotationAxis axis;
	
	protected PositCoplanarModifier positModifier;

	protected boolean hide = false;
	protected boolean pixels = true;

	protected int xOffset = 0;
	protected int yOffset = 0;

	protected Component feature;

	private int textHeight = 125;
	
	private boolean drawSphere = false;
	
	private Point3D axisMarker = new Point3D(0, 5, 0);

	public PositProcessingGL(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		super.load();

		loadingPhrase = "Configuring Filter";

		int width = w;

		int height = h;

		loading = 40;

		colorStrategy = new ColorStrategy(markerColor);
		
		colorStrategy.setTolerance(0x30);
		
		positModifier = new PositCoplanarModifier(width, height);
		
		cornerFilter = new FloodFillSearch(width, height);

		cornerFilter.setBorder(30);
		
		cornerFilter.setStep(1);

		cornerFilter.setPixelStrategy(colorStrategy);

		hullModifier = new AugmentedMarkerModifier();
		
		cornerFilter.setComponentModifierStrategy(hullModifier);

		feature = new Component(0, 0, w, h);

	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		super.updateKeyboard(event);

		if(event.isKeyDown(KeyEvent.TSK_SPACE)) {
			drawSphere = !drawSphere;
		}
		
		return GUIEvent.NONE;

	}
	
	private Point3D point = new Point3D(0, 0, 0);
	
	public void display(GLAutoDrawable drawable) {
		
		GL2 gl = drawable.getGL().getGL2();
		
		//gl.glPushMatrix();
		//Draw Marker Scene
		super.display(drawable);
				
		//gl.glPopMatrix();
				
		if(feature.getPoints().size()>3) {

			resetScene(gl);
						
			double angle = axis.getAngle();
			double rx = axis.getRotationX();
			double ry = axis.getRotationY();
			double rz = axis.getRotationZ();
			
			gl.glPushMatrix();
			
			gl.glRotated(angle, rx, ry, rz);
						
			if(!hide) {
				
				drawAxis(gl);
				
				//Flip Y Axis
				//gl.glScalef(1.f, -1.f, 1.f);
				
				if(drawSphere) {
					drawSphere(gl);
				}else{
					//drawCube(gl);
					drawPyramid(gl);
				}
			}
		
			point = axis.transformPoint(axisMarker);
			
			gl.glPopMatrix();
						
		}
		
		drawSphere(gl, 0.5, point.getX(), point.getZ(), point.getY());
			
		calculate(pipCamera);
				
	}
	
	private void resetScene(GL2 gl) {
		gl.glLoadIdentity();
		updateCamera(gl, cameraGL);
	}
	
	private void drawAxis(GL2 gl) {
				
		float axisSize = 5;
		
		gl.glLineWidth(3);
		
		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(1, 0, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(axisSize, 0, 0);

		gl.glColor3f(0, 1, 0);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, axisSize, 0);

		gl.glColor3f(0, 0, 1);
		gl.glVertex3f(0, 0, 0);
		gl.glVertex3f(0, 0, axisSize);
		gl.glEnd();
		
	}
	
	private void calculate(BufferedImage b) {

		loading = 60;

		loadingPhrase = "Start Filter";

		feature = cornerFilter.filterFirst(b, new Component(0, 0, b.getWidth(), b.getHeight()));
		
		axis = positModifier.modify(feature);
		
		loading = 65;
		loadingPhrase = "Show Result";

		loading = 70;
		loadingPhrase = "Show Angle";
	}

	@Override
	public void draw(Graphic g) {

		//Title Window Bar = 36 pixels
		int translateOffset = 36;
		
		g.translate(0, -translateOffset);
		
		if(!hide) {

			//g.drawImage(pipCamera, xOffset, yOffset);

			drawPipCamera(g);

			drawSceneData(g);

			if(feature!=null) {

				g.setColor(Color.BLUE);

				for(Point2D ponto: feature.getPoints()) {
					g.fillCircle(xOffset+(int)ponto.getX(), yOffset+(int)ponto.getY(), 5);
				}

				if(feature.getPoints().size()>3) {

					drawFilterData(g);

				}
			}

		}
		
		g.translate(0, translateOffset);

	}
	
	private void drawFilterData(Graphic g) {
		
		drawBox(g, feature);

		//g.drawString("Filter", 20, textHeight+100);
		g.drawString("Filter(Posit/angle): "+axis.getAngle(), 20, textHeight+100);

		g.drawString("Points = "+feature.getPoints().size(), 20, textHeight+125);

		g.drawString("Angle = "+axis.getAngle(), 20, textHeight+150);
		
		g.drawString("AxisX = "+axis.getRotationX(), 20, textHeight+175);

		g.drawString("AxisY = "+axis.getRotationY(), 20, textHeight+200);
		
		g.drawString("AxisZ = "+axis.getRotationZ(), 20, textHeight+225);
		
		g.drawString("X = "+axis.getX(), 20, textHeight+250);
		g.drawString("Y = "+axis.getY(), 20, textHeight+275);
		g.drawString("Z = "+axis.getZ(), 20, textHeight+300);
		
		Point2D a = feature.getPoints().get(0);
		Point2D b = feature.getPoints().get(1);
		Point2D c = feature.getPoints().get(2);
		Point2D d = feature.getPoints().get(3);
		
		Point2D ac = new Point2D((a.getX()+c.getX())/2, (a.getY()+c.getY())/2);
		Point2D ab = new Point2D((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);

		Point2D bd = new Point2D((b.getX()+d.getX())/2, (b.getY()+d.getY())/2);
		Point2D cd = new Point2D((c.getX()+d.getX())/2, (c.getY()+d.getY())/2);
		
		g.drawString("Dist(AB) = "+a.distance(b), 20, textHeight+350);
		g.drawString("Dist(AC) = "+a.distance(c), 20, textHeight+375);
		g.drawString("Dist(DB) = "+d.distance(b), 20, textHeight+400);
		g.drawString("Dist(DC) = "+d.distance(c), 20, textHeight+425);
		
		/*g.drawString("Dist((AC)~(BD)) = "+ac.distance(bd), 20, textHeight+325);
		g.drawString("Dist((AB)~(CD)) = "+ab.distance(cd), 20, textHeight+350);*/
				
		g.drawString("Dist((AC/AB)) = "+Double.toString(a.distance(c)/a.distance(b)), 20, textHeight+450);
		g.drawString("Dist((BD/CD)) = "+Double.toString(b.distance(d)/c.distance(d)), 20, textHeight+475);
		g.drawString("Dist((AC/AB)*(CD/BD)) = "+Double.toString((a.distance(c)/a.distance(b))*(d.distance(c)/b.distance(d))), 20, textHeight+500);
		
		//g.drawString("Lateral Distance = "+modifier.getLateralDistance(), 20, textHeight+400);
	}

	private void drawBox(Graphic g, Component box) {

		g.setColor(Color.RED);

		Point2D a = box.getPoints().get(0);
		Point2D b = box.getPoints().get(1);
		Point2D c = box.getPoints().get(2);
		Point2D d = box.getPoints().get(3);

		Point2D ac = new Point2D((a.getX()+c.getX())/2, (a.getY()+c.getY())/2);
		Point2D ab = new Point2D((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);

		Point2D bd = new Point2D((b.getX()+d.getX())/2, (b.getY()+d.getY())/2);
		Point2D cd = new Point2D((c.getX()+d.getX())/2, (c.getY()+d.getY())/2);

		drawLine(g, a, b);
		drawLine(g, a, c);

		drawLine(g, b, d);
		drawLine(g, c, d);

		drawPoint(g, a);
		drawPoint(g, b);
		drawPoint(g, c);
		drawPoint(g, d);

		g.setColor(Color.YELLOW);
		drawLine(g, ab, cd);
		drawPoint(g, ab);
		drawPoint(g, cd);

		g.setColor(Color.GREEN);
		drawLine(g, ac, bd);

		drawPoint(g, ac);
		drawPoint(g, bd);

		g.setColor(Color.ORANGE);
		drawPoint(g, box.getCenter());

		g.setColor(Color.BLACK);
		g.drawString("A", xOffset+(int)a.getX()-20, yOffset+(int)a.getY()-10);
		g.drawString("B", xOffset+(int)b.getX()+15, yOffset+(int)b.getY()-10);

		g.drawString("C", xOffset+(int)c.getX()-20, yOffset+(int)c.getY()+10);
		g.drawString("D", xOffset+(int)d.getX()+15, yOffset+(int)d.getY()+10);

	}

	private void drawLine(Graphic g, Point2D a, Point2D b) {		
		g.drawLine(xOffset+(int)a.getX(), yOffset+(int)a.getY(), xOffset+(int)b.getX(), yOffset+(int)b.getY());		
	}

	private void drawPoint(Graphic g, Point2D point) {
		g.fillCircle(xOffset+(int)point.getX(), yOffset+(int)point.getY(), 3);
	}

	private void drawSceneData(Graphic g) {

		g.setColor(Color.WHITE);

		g.drawShadow(20,textHeight+20, "Scene",Color.BLACK);

		g.drawShadow(20,textHeight+40, "AngleX: "+(angleX),Color.BLACK);

		g.drawShadow(20,textHeight+60, "AngleY: "+(angleY),Color.BLACK);
	}

}
