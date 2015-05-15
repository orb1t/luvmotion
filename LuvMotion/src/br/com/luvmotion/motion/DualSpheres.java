package br.com.luvmotion.motion;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import br.com.abby.linear.Point3D;
import br.com.etyllica.context.UpdateIntervalListener;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.graphics.SVGColor;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.motion.core.features.Component;
import br.com.etyllica.motion.filter.ColorFilter;
import br.com.etyllica.motion.trail.TripleAxisTrail;
import br.com.luvia.core.video.Graphics3D;
import br.com.luvia.geom.Sphere;
import br.com.luvmotion.ar.LuvMotionReality;

public class DualSpheres extends LuvMotionReality implements UpdateIntervalListener {

	int windowHeight = 40;

	private ColorFilter filter;
	protected Component orangeFeature;
	protected Component blueFeature;

	protected Color orangeColor = SVGColor.ORANGE;
	protected Color blueColor = SVGColor.SKY_BLUE;

	private Sphere orange;
	private Sphere blue;

	private double offset = 0.05;
	private double turnSpeed = 2;

	private double offsetBall = 0.06;

	private boolean needReset = false;

	private Set<Integer> registeredKeys;
	private Map<Integer, Boolean> keyStates;

	private TripleAxisTrail orangeTrail;
	private TripleAxisTrail blueTrail;
	
	private int trailSize = 50;

	public DualSpheres(int w, int h) {
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

		cameraGL.setY(1.5);
		cameraGL.setZ(3);//Distance in Meters

		orange = new Sphere(MotionSphere.BALL_RADIUS_TABLE_TENNIS);
		orange.setX(0);
		orange.setY(1);
		orange.setZ(1);
		orange.setColor(orangeColor);

		blue = new Sphere(MotionSphere.BALL_RADIUS_TABLE_TENNIS);
		blue.setY(0.5);
		blue.setX(-0.64);
		blue.setColor(blueColor);

		//Load Color Filter based on PipCamera attributes
		filter = new ColorFilter(w, h);
		filter.setTolerance(0x10);

		orangeFeature = new Component(w, h);
		blueFeature = new Component(w, h);

		orangeTrail = new TripleAxisTrail(trailSize);
		blueTrail = new TripleAxisTrail(trailSize);

		registeredKeys = new HashSet<Integer>();
		keyStates = new HashMap<Integer, Boolean>();

		registerKey(KeyEvent.TSK_W);
		registerKey(KeyEvent.TSK_S);
		registerKey(KeyEvent.TSK_D);
		registerKey(KeyEvent.TSK_A);
		registerKey(KeyEvent.TSK_Q);
		registerKey(KeyEvent.TSK_E);
		registerKey(KeyEvent.TSK_M);
		registerKey(KeyEvent.TSK_N);
		registerKey(KeyEvent.TSK_CTRL_LEFT);
		registerKey(KeyEvent.TSK_CTRL_RIGHT);
		registerKey(KeyEvent.TSK_UP_ARROW);
		registerKey(KeyEvent.TSK_DOWN_ARROW);
		registerKey(KeyEvent.TSK_LEFT_ARROW);
		registerKey(KeyEvent.TSK_RIGHT_ARROW);

		updateAtFixedRate(50, this);
	}

	@Override
	public void timeUpdate(long now) {
		needReset = true;

		orangeTrail.add(orange);
		blueTrail.add(blue);

		//Move Camera
		if(isPressed(KeyEvent.TSK_CTRL_DIREITA)||isPressed(KeyEvent.TSK_CTRL_ESQUERDA)) {

			if(isPressed(KeyEvent.TSK_D)) {
				scene.setOffsetX(+offset);
			} else if(isPressed(KeyEvent.TSK_A)) {
				scene.setOffsetX(-offset);
			}

			if(isPressed(KeyEvent.TSK_W)) {
				scene.setOffsetZ(+offset);
			} else if(isPressed(KeyEvent.TSK_S)) {
				scene.setOffsetZ(-offset);
			}

			if(isPressed(KeyEvent.TSK_Q)) {
				scene.setOffsetY(+offset);
			} else if(isPressed(KeyEvent.TSK_E)) {
				scene.setOffsetY(-offset);
			}

			if(isPressed(KeyEvent.TSK_UP_ARROW)) {
				scene.setOffsetAngleX(+turnSpeed);
			} else if(isPressed(KeyEvent.TSK_DOWN_ARROW)) {
				scene.setOffsetAngleX(-turnSpeed);
			}

			if(isPressed(KeyEvent.TSK_LEFT_ARROW)) {
				scene.setOffsetAngleY(+turnSpeed);
			} else if(isPressed(KeyEvent.TSK_RIGHT_ARROW)) {
				scene.setOffsetAngleY(-turnSpeed);
			}

			if(isPressed(KeyEvent.TSK_M)) {
				scene.setOffsetAngleZ(-turnSpeed);
			} else if(isPressed(KeyEvent.TSK_N)) {
				scene.setOffsetAngleZ(+turnSpeed);
			}

		} else {
			if(isPressed(KeyEvent.TSK_DOWN_ARROW)) {
				orange.setOffsetZ(+offsetBall);
			} else if(isPressed(KeyEvent.TSK_UP_ARROW)) {
				orange.setOffsetZ(-offsetBall);
			}

			if(isPressed(KeyEvent.TSK_LEFT_ARROW)) {
				orange.setOffsetX(-offsetBall);
			} else if(isPressed(KeyEvent.TSK_RIGHT_ARROW)) {
				orange.setOffsetX(+offsetBall);
			}

			//Blue Commands
			if(isPressed(KeyEvent.TSK_S)) {
				blue.setOffsetZ(+offsetBall);
			} else if(isPressed(KeyEvent.TSK_W)) {
				blue.setOffsetZ(-offsetBall);
			}

			if(isPressed(KeyEvent.TSK_A)) {
				blue.setOffsetX(-offsetBall);
			} else if(isPressed(KeyEvent.TSK_D)) {
				blue.setOffsetX(+offsetBall);
			}

			if(isPressed(KeyEvent.TSK_Q)) {
				blue.setOffsetY(-offsetBall);
			} else if(isPressed(KeyEvent.TSK_E)) {
				blue.setOffsetY(+offsetBall);
			}
		}
	}

	@Override
	protected BufferedImage generateMarkerImage(int w, int h) {

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

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
	public GUIEvent updateKeyboard(KeyEvent event) {

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

		for(Integer key: registeredKeys) {
			if(event.isKeyDown(key)) {
				keyStates.put(key, true);
			} else if(event.isKeyUp(key)) {
				keyStates.put(key, false);
			}
		}

		return GUIEvent.NONE;
	}

	protected void registerKey(int keyCode) {
		registeredKeys.add(keyCode);
		keyStates.put(keyCode, false);
	}

	protected boolean isPressed(int keyCode) {
		return keyStates.get(keyCode);
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
			cameraGL.setTarget(orange);
		}

		return GUIEvent.NONE;
	}

	@Override
	public void display(Graphics3D drawable) {

		GL2 gl = drawable.getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

		/*gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glDepthMask(true);*/

		//Transform by Camera
		drawable.updateCamera(cameraGL);

		gl.glPushMatrix();

		scene.updateScene(gl);

		orange.draw(drawable);
		blue.draw(drawable);
		
		drawTrail(drawable, orangeTrail);
		drawTrail(drawable, blueTrail);

		drawFloor(gl);

		drawable.drawCamera(cameraGL);

		gl.glPopMatrix();

		//gl.glFlush();

		updatePipCamera();

		if(needReset) {
			reset(pipCamera.getBuffer());
		}
	}

	private void drawTrail(Graphics3D g, TripleAxisTrail trail) {
		for(int i = 0; i<trail.getSize(); i++) {
			Point3D point = trail.getPoint(i);
			g.drawSphere(0.01, point.getX(), point.getY(), point.getZ(), 16, Color.RED);
		}
	}

	private void reset(BufferedImage buffer) {

		Component screen = new Component(0,0,buffer.getWidth(), buffer.getHeight());

		filter.setColor(orangeColor);
		orangeFeature = filter.filterFirst(buffer, screen);

		filter.setColor(blueColor);
		blueFeature = filter.filterFirst(buffer, screen);

	}

	@Override
	public void draw(Graphic g) {

		drawPipCamera(g);

		g.setColor(Color.BLACK);
		drawFeature(g, orangeFeature);
		drawFeature(g, blueFeature);
		
		//Draw Info
		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);
		g.drawShadow(20,20, "Scene");
		g.drawShadow(20,40, "AngleX: "+(scene.getAngleX()-5));
		g.drawShadow(20,60, "AngleY: "+(scene.getAngleY()));		

		g.drawShadow(20,100, "DistanceX: "+(orange.getX()));
		g.drawShadow(20,120, "DistanceY: "+(cameraGL.getY()+scene.getY()));
		g.drawShadow(20,140, "DistanceZ: "+(cameraGL.getZ()+scene.getZ()));

		drawCoordinates(g, cameraGL);
	}

	private int calculateRadius(Component component) {
		return component.getPointCount();
	}

	private void drawRadius(Graphic g, Component component) {
		int radius = calculateRadius(component);
		g.drawStringShadow(component.getX(),component.getY()-windowHeight, component.getW(), component.getH(), Integer.toString(radius));
	}

	protected void drawFeature(Graphic g, Component component) {
		if(component == null) {
			return;
		}

		g.drawOval(component.getX(), component.getY()-windowHeight, component.getW(), component.getH());
		
		drawRadius(g, component);
	}

	private void drawCoordinates(Graphic g, Point3D point) {
		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);

		g.drawShadow(500,20, "X: "+(point.getX()));
		g.drawShadow(500,40, "Y: "+(point.getY()));
		g.drawShadow(500,60, "Z: "+(point.getZ()));
	}

}
