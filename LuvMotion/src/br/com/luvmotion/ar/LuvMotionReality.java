package br.com.luvmotion.ar;

import static javax.media.opengl.GL.GL_LINEAR;
import static javax.media.opengl.GL.GL_TEXTURE_2D;
import static javax.media.opengl.GL.GL_TEXTURE_MAG_FILTER;
import static javax.media.opengl.GL.GL_TEXTURE_MIN_FILTER;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;

import br.com.abby.util.CameraGL;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvia.core.video.Graphics3D;
import br.com.luvia.loader.TextureLoader;
import br.com.luvmotion.capture.PipCamera;
import br.com.luvmotion.model.RealityScene;

import com.jogamp.opengl.util.awt.Screenshot;
import com.jogamp.opengl.util.texture.Texture;

public class LuvMotionReality extends ApplicationGL {

	//Scene Stuff
	private Texture marker;

	protected CameraGL cameraGL;

	protected float mx = 0;

	protected float my = 0;

	protected boolean click = false;

	protected RealityScene scene = new RealityScene();

	protected PipCamera pipCamera;

	protected Color markerColor = Color.BLACK;
	
	private double markerY = -4;

	public LuvMotionReality(int w, int h) {
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

		cameraGL = new CameraGL(0, 0.5, 0.0001);
		
		BufferedImage image = generateMarkerImage(200, 200);

		pipCamera = new PipCamera(image);

		marker = TextureLoader.getInstance().loadTexture(image);
	}

	protected BufferedImage generateMarkerImage(int w, int h) {

		BufferedImage image = new BufferedImage(w, h, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, w, h);

		g.setColor(Color.BLUE);
		g.fillOval(50, 50, w/2, h/2);

		g.setColor(markerColor);

		int strokeSize = 16;

		g.setStroke(new BasicStroke(strokeSize));
		g.drawRect(strokeSize, strokeSize, w-strokeSize*2, h-strokeSize*2);

		return image;
	}

	protected void drawFloor(GL2 gl) {

		gl.glColor3d(1,1,1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		double tileSize = 1f;

		marker.enable(gl);
		marker.bind(gl);

		drawMarker(gl, 0, tileSize);

		marker.disable(gl);
	}

	protected void drawMarker(GL2 gl, double y, double tileSize) {
		
		gl.glBegin(GL2.GL_QUADS);

		//(0,0)
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-tileSize/2, y, -tileSize/2);

		//(1,0)
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(+tileSize/2, y, -tileSize/2);

		//(1,1)
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(+tileSize/2, y, +tileSize/2);

		//(0,1)
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(-tileSize/2, y, +tileSize/2);

		gl.glEnd();
		
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

	private double offset = 0.5;
	private double offsetTurn = 0.5;
	
	private boolean turnUp = false;
	private boolean turnDown = false;
	private boolean turnLeft = false;
	private boolean turnRight = false;
	
	@Override
	public void update(long now) {
		if(turnUp) {
			scene.offsetAngleX(+offsetTurn);
		}
		if(turnDown) {
			scene.offsetAngleX(-offsetTurn);
		}
		if(turnLeft) {
			scene.offsetAngleY(+offsetTurn);
		}
		if(turnRight) {
			scene.offsetAngleY(-offsetTurn);
		}
	}
	
	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		if(event.isKeyDown(KeyEvent.TSK_D)) {
			scene.offsetX(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_A)) {
			scene.offsetX(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_W)) {
			scene.offsetY(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_S)) {
			scene.offsetY(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_Q)) {
			scene.offsetZ(+offset);
		} else if(event.isKeyDown(KeyEvent.TSK_E)) {
			scene.offsetZ(-offset);
		}

		if(event.isKeyDown(KeyEvent.TSK_UP_ARROW)) {
			turnUp = true;
		} else if(event.isKeyUp(KeyEvent.TSK_UP_ARROW)) {
			turnUp = false;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_DOWN_ARROW)) {
			turnDown = true;
		} else if(event.isKeyUp(KeyEvent.TSK_DOWN_ARROW)) {
			turnDown = false;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_LEFT_ARROW)) {
			turnLeft = true;
		} else if(event.isKeyUp(KeyEvent.TSK_LEFT_ARROW)) {
			turnLeft = false;
		}
		
		if(event.isKeyDown(KeyEvent.TSK_RIGHT_ARROW)) {
			turnRight = true;
		} else if(event.isKeyUp(KeyEvent.TSK_RIGHT_ARROW)) {
			turnRight = false;
		}

		if(event.isKeyDown(KeyEvent.TSK_M)) {
			scene.offsetAngleZ(-5);
		} else if(event.isKeyDown(KeyEvent.TSK_N)) {
			scene.offsetAngleZ(+5);
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

		return GUIEvent.NONE;
	}

	@Override
	public void display(Graphics3D drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

		/*gl.glEnable(GL.GL_DEPTH_TEST);
		gl.glDepthFunc(GL.GL_LESS);
		gl.glDepthMask(true);*/
	
		//Transform by Camera
		drawable.updateCamera(cameraGL);		

		gl.glPushMatrix();
		
		gl.glTranslated(0, markerY, 0);
		
		scene.updateScene(gl);		
		
		drawFloor(gl);
		
		gl.glPopMatrix();

		//Draw Scene


		//gl.glFlush();

		updatePipCamera();

	}

	protected void updatePipCamera() {
		pipCamera.setBuffer(Screenshot.readToBufferedImage(w, h, false));

		//Erasing Window Title Black Rectangle
		pipCamera.getGraphics().setColor(Color.WHITE);
		pipCamera.getGraphics().fillRect(0, 0, w, 50);
	}

	@Override
	public void draw(Graphic g) {

		//Draw Gui
		g.setColor(Color.WHITE);
		g.drawShadow(20,20, "Scene",Color.BLACK);

		g.drawShadow(20,40, "AngleX: "+(scene.getAngleX()-5),Color.BLACK);

		g.drawShadow(20,60, "AngleY: "+(scene.getAngleY()),Color.BLACK);

		//drawPipCamera(g);

		//g.escreve(20,20,"Scene");
		//System.out.println("w = "+w);
		//System.out.println("h = "+h);
		//g.drawLine(w/2, h/2, w/2+mx, h/2+my);

	}

	protected void drawPipCamera(Graphic g) {

		//AffineTransform transform = AffineTransform.getScaleInstance(640/w, 480/h);
		AffineTransform transform = AffineTransform.getScaleInstance(0.2, 0.2);

		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		BufferedImage camera = op.filter(pipCamera.getBuffer(), null);

		g.drawImage(camera, 0, 0);
	}

}
