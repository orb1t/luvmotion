package br.com.luvmotion.motion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import com.badlogic.gdx.math.Vector3;

import br.com.etyllica.awt.SVGColor;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.MouseButton;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphics;
import br.com.etyllica.core.linear.Point3D;
import br.com.etyllica.motion.feature.Component;
import br.com.etyllica.motion.filter.ColorFilter;
import br.com.luvia.core.graphics.Graphics3D;
import br.com.luvia.graphics.Sphere;
import br.com.luvmotion.ar.LuvMotionReality;

public class MotionSphere extends LuvMotionReality {

	int windowHeight = 40;
	
	private ColorFilter filter;
	protected Component feature;
	
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
	public void init(Graphics3D drawable) {

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
				
		cameraGL.setY(1.5f);
		cameraGL.setX(2.6f);
		
		sphere = new Sphere(BALL_RADIUS_TABLE_TENNIS);
		sphere.setX(1);
		sphere.setColor(sphereColor);
		
		origin = new Sphere(BALL_RADIUS_TABLE_TENNIS);
		origin.setColor(SVGColor.ALICE_BLUE);
		
		//Load Color Filter based on PipCamera attributes
		filter = new ColorFilter(w, h);
		filter.setColor(sphereColor);
		filter.setTolerance(0x30);
		
		feature = new Component(w, h);
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
	public void reshape(Graphics3D drawable, int x, int y, int width, int height) {

		GL2 gl = drawable.getGL2();
		GLU glu = drawable.getGLU();
		
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
	public void updateKeyboard(KeyEvent event) {

		if(event.isKeyDown(KeyEvent.VK_D)) {
			scene.x += offset;
		} else if(event.isKeyDown(KeyEvent.VK_A)) {
			scene.x -= offset;
		}

		if(event.isKeyDown(KeyEvent.VK_W)) {
			scene.y += offset;
		} else if(event.isKeyDown(KeyEvent.VK_S)) {
			scene.y -= offset;
		}

		if(event.isKeyDown(KeyEvent.VK_Q)) {
			scene.z += offset;
		} else if(event.isKeyDown(KeyEvent.VK_E)) {
			scene.z -= offset;
		}

		if(event.isKeyDown(KeyEvent.VK_UP_ARROW)) {
			scene.offsetAngleX(+5);
		} else if(event.isKeyDown(KeyEvent.VK_DOWN_ARROW)) {
			scene.offsetAngleX(-5);
		}

		if(event.isKeyDown(KeyEvent.VK_LEFT_ARROW)) {
			scene.offsetAngleY(+5);
		} else if(event.isKeyDown(KeyEvent.VK_RIGHT_ARROW)) {
			scene.offsetAngleY(-5);
		}

		if(event.isKeyDown(KeyEvent.VK_M)) {
			scene.offsetAngleZ(-5);
		} else if(event.isKeyDown(KeyEvent.VK_N)) {
			scene.offsetAngleZ(+5);
		}
		
		if(event.isKeyDown(KeyEvent.VK_Z)) {
			cameraGL.offsetY(-0.5f);
		} else if(event.isKeyDown(KeyEvent.VK_X)) {
			cameraGL.offsetY(+0.5f);
		}
		
	}

	public void updateMouse(PointerEvent event) {

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
			cameraGL.setTarget(sphere.position);
		}
		
	}

	@Override
	public void display(Graphics3D drawable) {

		GL2 gl = drawable.getGL2();
		GLU glu = drawable.getGLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

		/*gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glDepthMask(true);*/
	
		//Transform by Camera
		drawable.updateCamera(cameraGL);
				
		gl.glPushMatrix();
				
		scene.updateScene(gl);
		
		sphere.draw(drawable);
		origin.draw(drawable);
		
		drawFloor(gl);
		
		drawable.drawCamera(cameraGL);
		
		gl.glPopMatrix();

		//gl.glFlush();
		
		updatePipCamera();
		
		reset(pipCamera.getBuffer());
	}
	
	private void reset(BufferedImage buffer) {
		
	  Component screen = new Component(buffer.getWidth(), buffer.getHeight());
		
		feature = filter.filterFirst(buffer, screen);
	}
	
	@Override
	public void draw(Graphics g) {

		drawPipCamera(g);
		
		if(feature != null) {
			g.drawRect(feature.getX(), feature.getY()-windowHeight, feature.getW(), feature.getH());
		}
		
		//Draw Info
		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);
		g.drawShadow(20,20, "Scene");
		g.drawShadow(20,40, "AngleX: "+(scene.getAngleX()-5));
		g.drawShadow(20,60, "AngleY: "+(scene.getAngleY()));		
		
		g.drawShadow(20,100, "DistanceX: "+(sphere.getX()));
		g.drawShadow(20,120, "DistanceY: "+(cameraGL.getY()+scene.y));
		g.drawShadow(20,140, "DistanceZ: "+(cameraGL.getZ()+scene.z));
		
		drawCoordinates(g, cameraGL.position);
	}
	
	private void drawCoordinates(Graphics g, Vector3 point) {

		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);
		
		g.drawShadow(500,20, "X: "+(point.x));
		g.drawShadow(500,40, "Y: "+(point.y));
		g.drawShadow(500,60, "Z: "+(point.z));
	}

}
