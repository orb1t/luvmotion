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

import com.badlogic.gdx.math.Vector3;

import br.com.etyllica.awt.SVGColor;
import br.com.etyllica.core.context.UpdateIntervalListener;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.MouseButton;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphics;
import br.com.etyllica.core.linear.Point3D;
import br.com.etyllica.motion.feature.Component;
import br.com.etyllica.motion.feature.trail.TripleAxisTrail;
import br.com.etyllica.motion.feature.trail.TripleAxisTrailListener;
import br.com.etyllica.motion.filter.ColorFilter;
import br.com.etyllica.motion.model.RangeFlag;
import br.com.luvia.core.graphics.Graphics3D;
import br.com.luvia.graphics.Sphere;
import br.com.luvmotion.ar.LuvMotionReality;
import br.com.luvmotion.model.Gesture;

public class DualMotionSpheres extends LuvMotionReality implements UpdateIntervalListener, TripleAxisTrailListener {

	int windowHeight = 40;

	private ColorFilter filter;
	protected Component orangeFeature;
	protected Component blueFeature;

	protected Color orangeColor = SVGColor.ORANGE;
	protected Color blueColor = SVGColor.SKY_BLUE;

	private Sphere orange;
	private Sphere blue;

	private float offset = 0.05f;
	private float turnSpeed = 2f;

	private float offsetBall = 0.06f;

	private boolean needReset = false;

	private Set<Integer> registeredKeys;
	private Map<Integer, Boolean> keyStates;

	private TripleAxisTrail orangeTrail;
	private TripleAxisTrail blueTrail;
	
	private int trailSize = 50;
	
	private Gesture gesture = Gesture.NONE;

	public DualMotionSpheres(int w, int h) {
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

		cameraGL.setPosition(0, 1.5f, 3); //Distance in Meters

		orange = new Sphere(MotionSphere.BALL_RADIUS_TABLE_TENNIS);
		orange.setX(0);
		orange.setY(1);
		orange.setZ(1);
		orange.setColor(orangeColor);

		blue = new Sphere(MotionSphere.BALL_RADIUS_TABLE_TENNIS);
		blue.setY(0.5f);
		blue.setX(-0.64f);
		blue.setColor(blueColor);

		//Load Color Filter based on PipCamera attributes
		filter = new ColorFilter(w, h);
		filter.setTolerance(0x10);

		orangeFeature = new Component(w, h);
		blueFeature = new Component(w, h);

		orangeTrail = new TripleAxisTrail(trailSize, this);
		orangeTrail.setDeltaMin(0.10);
				
		blueTrail = new TripleAxisTrail(trailSize);

		registeredKeys = new HashSet<Integer>();
		keyStates = new HashMap<Integer, Boolean>();

		registerKey(KeyEvent.VK_W);
		registerKey(KeyEvent.VK_S);
		registerKey(KeyEvent.VK_D);
		registerKey(KeyEvent.VK_A);
		registerKey(KeyEvent.VK_Q);
		registerKey(KeyEvent.VK_E);
		registerKey(KeyEvent.VK_M);
		registerKey(KeyEvent.VK_N);
		registerKey(KeyEvent.VK_CTRL_LEFT);
		registerKey(KeyEvent.VK_CTRL_RIGHT);
		registerKey(KeyEvent.VK_UP_ARROW);
		registerKey(KeyEvent.VK_DOWN_ARROW);
		registerKey(KeyEvent.VK_LEFT_ARROW);
		registerKey(KeyEvent.VK_RIGHT_ARROW);

		updateAtFixedRate(50, this);
	}

	@Override
	public void timeUpdate(long now) {
		needReset = true;

		orangeTrail.add(orange.position);
		blueTrail.add(blue.position);

		//Move Camera
		if(isPressed(KeyEvent.VK_CTRL_RIGHT)||isPressed(KeyEvent.VK_CTRL_LEFT)) {

			if(isPressed(KeyEvent.VK_D)) {
				scene.x += offset;
			} else if(isPressed(KeyEvent.VK_A)) {
				scene.x -= offset;
			}

			if(isPressed(KeyEvent.VK_W)) {
				scene.z += offset;
			} else if(isPressed(KeyEvent.VK_S)) {
				scene.z -= offset;
			}

			if(isPressed(KeyEvent.VK_Q)) {
				scene.y += offset;
			} else if(isPressed(KeyEvent.VK_E)) {
				scene.y -= offset;
			}

			if(isPressed(KeyEvent.VK_UP_ARROW)) {
				scene.offsetAngleX(+turnSpeed);
			} else if(isPressed(KeyEvent.VK_DOWN_ARROW)) {
				scene.offsetAngleX(-turnSpeed);
			}

			if(isPressed(KeyEvent.VK_LEFT_ARROW)) {
				scene.offsetAngleY(+turnSpeed);
			} else if(isPressed(KeyEvent.VK_RIGHT_ARROW)) {
				scene.offsetAngleY(-turnSpeed);
			}

			if(isPressed(KeyEvent.VK_M)) {
				scene.offsetAngleZ(-turnSpeed);
			} else if(isPressed(KeyEvent.VK_N)) {
				scene.offsetAngleZ(+turnSpeed);
			}

		} else {
			if(isPressed(KeyEvent.VK_DOWN_ARROW)) {
				orange.offsetZ(+offsetBall);
			} else if(isPressed(KeyEvent.VK_UP_ARROW)) {
				orange.offsetZ(-offsetBall);
			}

			if(isPressed(KeyEvent.VK_LEFT_ARROW)) {
				orange.offsetX(-offsetBall);
			} else if(isPressed(KeyEvent.VK_RIGHT_ARROW)) {
				orange.offsetX(+offsetBall);
			}

			//Blue Commands
			if(isPressed(KeyEvent.VK_S)) {
				blue.offsetZ(+offsetBall);
			} else if(isPressed(KeyEvent.VK_W)) {
				blue.offsetZ(-offsetBall);
			}

			if(isPressed(KeyEvent.VK_A)) {
				blue.offsetX(-offsetBall);
			} else if(isPressed(KeyEvent.VK_D)) {
				blue.offsetX(+offsetBall);
			}

			if(isPressed(KeyEvent.VK_Q)) {
				blue.offsetY(-offsetBall);
			} else if(isPressed(KeyEvent.VK_E)) {
				blue.offsetY(+offsetBall);
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
	public void updateKeyboard(KeyEvent event) {

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

		for(Integer key: registeredKeys) {
			if(event.isKeyDown(key)) {
				keyStates.put(key, true);
			} else if(event.isKeyUp(key)) {
				keyStates.put(key, false);
			}
		}
	}

	protected void registerKey(int keyCode) {
		registeredKeys.add(keyCode);
		keyStates.put(keyCode, false);
	}

	protected boolean isPressed(int keyCode) {
		return keyStates.get(keyCode);
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
			cameraGL.setTarget(orange.position);
		}
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
			
			Color color = Color.RED;
			
			if(trail.getAxisX().getDeltaMod()>0.3) {
				color = SVGColor.BLUE;
			} else if(trail.getAxisY().getDeltaMod()>0.3) {
				color = SVGColor.GREEN;
			}
			
			g.drawSphere(0.01, point.getX(), point.getY(), point.getZ(), 16, color);
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
	public void draw(Graphics g) {

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
		g.drawShadow(20,120, "DistanceY: "+(cameraGL.getY()+scene.y));
		g.drawShadow(20,140, "DistanceZ: "+(cameraGL.getZ()+scene.z));

		drawCoordinates(g, cameraGL.position);
		
		if(gesture!=Gesture.NONE) {
			g.drawShadow(200,200, gesture.toString());	
		}
		
	}

	private int calculateRadius(Component component) {
		return component.getPointCount();
	}

	private void drawRadius(Graphics g, Component component) {
		int radius = calculateRadius(component);
		g.drawStringShadow(component.getX(),component.getY()-windowHeight, component.getW(), component.getH(), Integer.toString(radius));
	}

	protected void drawFeature(Graphics g, Component component) {
		if(component == null) {
			return;
		}

		g.drawOval(component.getX(), component.getY()-windowHeight, component.getW(), component.getH());
		
		drawRadius(g, component);
	}

	private void drawCoordinates(Graphics g, Vector3 point) {
		g.setColor(Color.WHITE);
		g.setShadowColor(Color.BLACK);

		g.drawShadow(500, 20, "X: "+(point.x));
		g.drawShadow(500, 40, "Y: "+(point.y));
		g.drawShadow(500, 60, "Z: "+(point.z));
	}

	@Override
	public void listenTrail(double deltaX, double deltaY, double deltaZ) {
		
		RangeFlag x1 = orangeTrail.evaluateDeltaX();
		RangeFlag y1 = orangeTrail.evaluateDeltaY();
		RangeFlag z1 = orangeTrail.evaluateDeltaZ();
		
		RangeFlag x2 = blueTrail.evaluateDeltaX();
		RangeFlag y2 = blueTrail.evaluateDeltaY();
		RangeFlag z2 = blueTrail.evaluateDeltaZ();
		
		gesture = evaluateGesture(x1, y1, z1, x2, y2, z2);
	}	
	
	private Gesture evaluateGesture(RangeFlag x1, RangeFlag y1, RangeFlag z1, RangeFlag x2, RangeFlag y2, RangeFlag z2) {
		if(x1 == x2 && y1 == y2 && z1 == z2) {
			return Gesture.MOVE;
		}
		
		if(z1.isOpposite(z2)) {
			return Gesture.ROTATE;
		}
		
		if(x1.isOpposite(x2)) {
			return Gesture.SCALE;
		}
		
		return Gesture.NONE;		
	}

}
