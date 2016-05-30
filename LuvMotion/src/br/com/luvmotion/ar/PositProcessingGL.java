package br.com.luvmotion.ar;

import java.awt.Color;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.graphics.Graphics;
import br.com.etyllica.core.linear.Point2D;
import br.com.etyllica.core.linear.Point3D;
import br.com.etyllica.motion.core.helper.RotationAxis;
import br.com.etyllica.motion.feature.Component;
import br.com.etyllica.motion.filter.color.ColorStrategy;
import br.com.etyllica.motion.filter.search.flood.FloodFillSearch;
import br.com.etyllica.motion.filter.search.flood.SoftFloodFillSearch;
import br.com.etyllica.motion.filter.validation.MaxDensityValidation;
import br.com.etyllica.motion.math.interpolation.QuadraticInterpolator;
import br.com.etyllica.motion.modifier.PositCoplanarModifier;
import br.com.etyllica.motion.modifier.hull.HullModifier;
import br.com.etyllica.motion.modifier.ogr.RectangularOGRModifier;
import br.com.luvia.core.graphics.Graphics3D;

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
	
	public PositProcessingGL(int w, int h) {
		super(w, h);
	}

	@Override
	public void load() {
		super.load();

		loadingInfo = "Configuring Filter";

		int width = w;

		int height = h;

		loading = 40;

		colorStrategy = new ColorStrategy(markerColor);
		
		colorStrategy.setTolerance(0x30);
		
		double focalLength = w/2;
		
		positModifier = new PositCoplanarModifier(width, height, focalLength);
		
		cornerFilter = new SoftFloodFillSearch(width, height);
		cornerFilter.addValidation(new MaxDensityValidation(w));
		
		cornerFilter.setBorder(10);		
		cornerFilter.setStep(1);
		cornerFilter.setPixelStrategy(colorStrategy);

		//hullModifier = new AugmentedMarkerModifier();
		hullModifier = new RectangularOGRModifier();
		
		cornerFilter.setComponentModifierStrategy(hullModifier);

		feature = new Component(0, 0, w, h);
	}

	@Override
	public void updateKeyboard(KeyEvent event) {
		super.updateKeyboard(event);

		if(event.isKeyDown(KeyEvent.VK_SPACE)) {
			drawSphere = !drawSphere;
		}
		
	}
	
	private Point3D point = new Point3D(0, 0, 0);
	
	@Override
	public void display(Graphics3D drawable) {
		//Draw Marker Scene
		super.display(drawable);		

		GL2 gl = drawable.getGL().getGL2();
		
		//Erase Marker
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);
				
		if(feature.getPoints().size() > 3) {

			resetScene(drawable);
						
			double angle = axis.getAngle();
			double rx = axis.getRotationX();
			double ry = axis.getRotationY();
			double rz = axis.getRotationZ();
			
			double size = 1;
			
			gl.glPushMatrix();

			gl.glTranslated(axis.getX(), -axis.getY()-0.5, -axis.getZ());
			gl.glRotated(angle, rx, ry, rz);
			
			//
			//gl.glTranslated(axis.getX(), axis.getY()-12, -axis.getZ());
						
			if(!hide) {
				
				drawAxis(gl);
				
				//Flip Y Axis
				//gl.glScalef(1.f, -1.f, 1.f);
				//gl.glTranslated(0, +0.5, 0);
				
				if(drawSphere) {
					drawable.drawSphere(1, 0, 0, 0);
				} else {
					drawable.setColor(Color.BLACK);
					drawable.drawWireCube(size);
					drawable.setColor(Color.CYAN);
					drawable.drawCube();
					//drawable.drawPyramid(1);
				}
				
			}
					
			gl.glPopMatrix();
						
			double xFactor = 4.6;
			
			//double zFactor = suggestZFactor(-axis.getZ());
			double zFactor = xFactor;
			
			//Point3D axisMarker = new Point3D(axis.getX(), -axis.getZ(), axis.getY());
			Point3D axisMarker = new Point3D(axis.getX(), -axis.getY(), -axis.getZ());
						
			point = axis.transformPoint(axisMarker);
						
			drawable.drawSphere(0.5, point.getX(), point.getY(), point.getZ());
			
		}
					
		calculate(pipCamera.getBuffer());
				
	}
	
	private double suggestZFactor(double axis) {
		
		QuadraticInterpolator interpolator = new QuadraticInterpolator();
		interpolator.addPoint(-6, -5.0);
		interpolator.addPoint(0, 0);		
		interpolator.addPoint(5.5, -4.07);
		
		return interpolator.interpolate(axis);
	}
	
	
	private void resetScene(Graphics3D g) {
		GL2 gl = g.getGL2();
		gl.glLoadIdentity();
		g.updateCamera(cameraGL);
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

		loadingInfo = "Start Filter";

		feature = cornerFilter.filterFirst(b, new Component(0, 0, b.getWidth()-1, b.getHeight()-1));
		
		axis = positModifier.modify(feature);
		
		loading = 65;
		loadingInfo = "Show Result";

		loading = 70;
		loadingInfo = "Show Angle";
	}

	@Override
	public void draw(Graphics g) {

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
					
					drawRealData(g);

				}
			}

		}
		
		g.translate(0, translateOffset);

	}
	
	private void drawFilterData(Graphics g) {
		
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
	
	private void drawRealData(Graphics g) {
		
		g.drawString("X = "+scene.getX(), 720, textHeight+250);
		g.drawString("Y = "+scene.getY(), 720, textHeight+275);
		g.drawString("Z = "+scene.getZ(), 720, textHeight+300);
		
		double xFactor = scene.getX()/axis.getX();
		double zFactor = scene.getY()/axis.getZ();
		
		g.drawString("FX = "+xFactor, 720, textHeight+175);
		g.drawString("FZ = "+zFactor, 720, textHeight+200);
		
		//g.drawString("SY = "+suggest(axis.getY()), 790, textHeight+275);
		g.drawString("PX = "+point.getX(), 800, textHeight+250);
		g.drawString("PY = "+point.getY(), 800, textHeight+275);
		g.drawString("PZ = "+point.getZ(), 800, textHeight+300);
		
	}
	
	QuadraticInterpolator interpolator = new QuadraticInterpolator();
	
	private double suggest(double x) {
	
		interpolator.reset();
		
		interpolator.addPoint(2.35, 5);
		interpolator.addPoint(4.87, 10);
		interpolator.addPoint(7.31, 15);
		
		return interpolator.interpolate(x);	
	}

	private void drawBox(Graphics g, Component box) {

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

	private void drawLine(Graphics g, Point2D a, Point2D b) {		
		g.drawLine(xOffset+(int)a.getX(), yOffset+(int)a.getY(), xOffset+(int)b.getX(), yOffset+(int)b.getY());		
	}

	private void drawPoint(Graphics g, Point2D point) {
		g.fillCircle(xOffset+(int)point.getX(), yOffset+(int)point.getY(), 3);
	}

	private void drawSceneData(Graphics g) {

		g.setColor(Color.WHITE);

		g.drawShadow(20,textHeight+20, "Scene",Color.BLACK);

		g.drawShadow(20,textHeight+40, "AngleX: "+(scene.getAngleX()),Color.BLACK);

		g.drawShadow(20,textHeight+60, "AngleY: "+(scene.getAngleY()),Color.BLACK);
	}

}
