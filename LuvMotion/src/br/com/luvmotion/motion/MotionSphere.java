package br.com.luvmotion.motion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GLAutoDrawable;

import br.com.abby.linear.Point3D;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.luvia.geom.Sphere;
import br.com.luvmotion.ar.LuvMotionReality;

public class MotionSphere extends LuvMotionReality {

	//Ball Radius in meters (Source: Wikipedia)
	public static final double BALL_RADIUS_POOL_RUSSIAN = 0.034;
	public static final double BALL_RADIUS_POOL_CAROM = 0.03075;
	public static final double BALL_RADIUS_POOL_AMERICAN = 0.028575;
	public static final double BALL_RADIUS_POOL_BRITISH = 0.028;
	public static final double BALL_RADIUS_SNOOKER = 0.026;
	public static final double BALL_RADIUS_TABLE_TENNIS = 0.02;
	
	//Scene Stuff
	protected boolean click = false;

	protected Color markerColor = Color.BLACK;
	protected Color sphereColor = SVGColor.DARK_SALMON;
	
	private Sphere sphere;
	private Sphere origin;

	private double offset = 0.5;

	public MotionSphere(int w, int h) {
		super(w, h);
	}

	@Override
	public void init(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		// Global settings.
		gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LEQUAL);
		gl.glShadeModel(GL2.GL_SMOOTH);
		gl.glHint(GL2.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);		
	}
	
	@Override
	public void load() {
		super.load();
		
		cameraGL.setY(3);
		
		sphere = new Sphere(BALL_RADIUS_TABLE_TENNIS);
		sphere.setX(1);
		sphere.setColor(sphereColor);
		
		origin = new Sphere(BALL_RADIUS_TABLE_TENNIS);
		origin.setColor(SVGColor.MISTYROSE);
	}
	
	@Override
	protected BufferedImage generateMarkerImage(int w, int h) {

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.GREEN);
		g.fillOval(50, 50, w/2, h/2);

		g.setColor(markerColor);

		int strokeSize = 16;

		g.setStroke(new BasicStroke(strokeSize));
		g.drawRect(strokeSize, strokeSize, w-strokeSize*2, h-strokeSize*2);

		return image;

	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glViewport (x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();

		float aspect = (float)width / (float)height; 

		//gl.glOrtho(left*aspect, right*aspect, bottom, top, 0.1, 500);
		glu.gluPerspective(40, aspect, 1, 100);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		//gl.glLoadIdentity();
	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		if(event.isKeyDown(KeyEvent.TSK_D)) {
			scene.setOffsetX(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_A)) {
			scene.setOffsetX(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_W)) {
			scene.setOffsetY(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_S)) {
			scene.setOffsetY(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_Q)) {
			scene.setOffsetZ(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_E)) {
			scene.setOffsetZ(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_UP_ARROW)) {
			scene.setOffsetAngleX(+5);
		} else if(event.isKeyDown(KeyEvent.TSK_DOWN_ARROW)) {
			scene.setOffsetAngleX(-5);
		}

		if(event.isKeyDown(KeyEvent.TSK_LEFT_ARROW)) {
			scene.setOffsetAngleY(+5);
		} else if(event.isKeyDown(KeyEvent.TSK_RIGHT_ARROW)) {
			scene.setOffsetAngleY(-5);
		}

		if(event.isKeyDown(KeyEvent.TSK_M)) {
			scene.setOffsetAngleZ(-5);
		} else if(event.isKeyDown(KeyEvent.TSK_N)) {
			scene.setOffsetAngleZ(+5);
		}
		
		if(event.isKeyDown(KeyEvent.TSK_Z)) {
			cameraGL.setOffsetY(-0.5);
		} else if(event.isKeyDown(KeyEvent.TSK_X)) {
			cameraGL.setOffsetY(+0.5);
		}

		return GUIEvent.NONE;
	}

	public GUIEvent updateMouse(PointerEvent event) {

		mx = event.getX();
		my = event.getY();

		if(event.isButtonDown(MouseButton.MOUSE_BUTTON_LEFT)) {
			cameraGL.setZ(cameraGL.getZ()+0.1f);
			click = true;
		}

		if(event.isButtonUp(MouseButton.MOUSE_BUTTON_LEFT)) {
			cameraGL.setZ(cameraGL.getZ()-0.1f);
			click = false;
		}
		
		if(event.isButtonUp(MouseButton.MOUSE_BUTTON_RIGHT)) {
			cameraGL.setTarget(sphere);
		}
		

		return GUIEvent.NONE;
	}

	@Override
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

		/*gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glDepthMask(true);*/
	
		//Transform by Camera
		updateCamera(gl, cameraGL);
				
		gl.glPushMatrix();
				
		scene.updateScene(gl);		
		
		sphere.draw(gl, glu);
		origin.draw(gl, glu);
		
		drawFloor(gl);
		
		gl.glPopMatrix();

		//gl.glFlush();

		updatePipCamera();
	}

	@Override
	public void draw(Graphic g) {

		drawPipCamera(g);
		
		//Draw Info
		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);
		g.drawShadow(20,20, "Scene");
		g.drawShadow(20,40, "AngleX: "+(scene.getAngleX()-5));
		g.drawShadow(20,60, "AngleY: "+(scene.getAngleY()));		
		
		g.drawShadow(20,100, "DistanceX: "+(sphere.getX()));
		g.drawShadow(20,120, "DistanceY: "+(cameraGL.getY()+scene.getY()));
		g.drawShadow(20,140, "DistanceZ: "+(cameraGL.getZ()+scene.getZ()));
		
		drawCoordinates(g, cameraGL);
	}
	
	private void drawCoordinates(Graphic g, Point3D point) {

		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);
		
		g.drawShadow(500,20, "X: "+(point.getX()));
		g.drawShadow(500,40, "Y: "+(point.getY()));
		g.drawShadow(500,60, "Z: "+(point.getZ()));
	}

}
