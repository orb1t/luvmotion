package br.com.luvmotion;

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
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import br.com.abby.util.CameraGL;
import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.graphics.Graphic;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.luvia.core.ApplicationGL;
import br.com.luvia.loader.TextureLoader;

import com.jogamp.opengl.util.awt.Screenshot;
import com.jogamp.opengl.util.texture.Texture;

public class LuvMotionReality extends ApplicationGL {

	//Scene Stuff
	private Texture marker;

	protected CameraGL cameraGL;

	protected float mx = 0;

	protected float my = 0;

	protected boolean click = false;

	protected double angleX = 0;

	protected double angleY = 0;

	protected double angleZ = 0;

	protected double offsetX = 0;

	protected double offsetY = 0;

	protected double offsetZ = 0;

	protected BufferedImage pipCamera;

	protected Color markerColor = Color.BLACK;

	public LuvMotionReality(int w, int h) {
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

		cameraGL = new CameraGL(0, 5, 0.0001);

		BufferedImage image = generateMarkerImage();

		pipCamera = image;

		marker = TextureLoader.getInstance().loadTexture(image);

	}

	private BufferedImage generateMarkerImage() {

		BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();

		pipCamera = image;

		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 200, 200);

		g.setColor(Color.BLUE);
		g.fillOval(50, 50, 100, 100);

		g.setColor(markerColor);

		int strokeSize = 16;

		g.setStroke(new BasicStroke(strokeSize));
		g.drawRect(strokeSize, strokeSize, 200-strokeSize*2, 200-strokeSize*2);

		return image;

	}

	protected void drawFloor(GL2 gl) {

		gl.glColor3d(1,1,1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);

		drawGrid(gl,200,120);		

	}

	private void drawGrid(GL2 gl, double x, double y) {

		double tileSize = 1;

		marker.enable(gl);
		marker.bind(gl);

		drawMarker(gl, tileSize);		

		marker.disable(gl);
	}

	private void drawMarker(GL2 gl, double tileSize) {
		
		gl.glBegin(GL2.GL_QUADS);

		//(0,0)
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(-tileSize/2, 0, -tileSize/2);

		//(1,0)
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(+tileSize/2, 0, -tileSize/2);

		//(1,1)
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(+tileSize/2, 0, +tileSize/2);

		//(0,1)
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(-tileSize/2, 0, +tileSize/2);

		gl.glEnd();
		
	}
	
	private void drawTile(GL2 gl, double x, double y, double tileSize) {

		gl.glBegin(GL2.GL_QUADS);

		//(0,0)
		gl.glTexCoord2d(0, 0);
		gl.glVertex3d(x*tileSize, 0, y*tileSize);

		//(1,0)
		gl.glTexCoord2d(1, 0);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize);

		//(1,1)
		gl.glTexCoord2d(1, 1);
		gl.glVertex3d(x*tileSize+tileSize, 0, y*tileSize+tileSize);

		//(0,1)
		gl.glTexCoord2d(0, 1);
		gl.glVertex3d(x*tileSize, 0, y*tileSize+tileSize);

		gl.glEnd();
	}

	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glViewport (x, y, width, height);

		gl.glMatrixMode(GL2.GL_PROJECTION);

		gl.glLoadIdentity();

		double left = -10;
		double right = +10;
		double bottom = -10;
		double top = +10;

		float aspect = (float)width / (float)height; 

		//gl.glOrtho(left*aspect, right*aspect, bottom, top, 0.1, 500);
		glu.gluPerspective(60,aspect,1,100);

		gl.glMatrixMode(GL2.GL_MODELVIEW);

		//gl.glLoadIdentity();

	}	

	private double offset = 0.5; 

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {

		if(event.isKeyDown(KeyEvent.TSK_D)) {
			offsetX += offset;

		} else if(event.isKeyDown(KeyEvent.TSK_A)) {

			offsetX -= offset;
		}

		if(event.isKeyDown(KeyEvent.TSK_W)) {
			offsetY += offset;

		} else if(event.isKeyDown(KeyEvent.TSK_S)) {

			offsetY -= offset;
		}

		if(event.isKeyDown(KeyEvent.TSK_Q)) {
			offsetZ += offset;

		} else if(event.isKeyDown(KeyEvent.TSK_E)) {

			offsetZ -= offset;
		}


		if(event.isKeyDown(KeyEvent.TSK_UP_ARROW)) {

			angleX += 5;

		}

		else if(event.isKeyDown(KeyEvent.TSK_DOWN_ARROW)) {

			angleX -= 5;

		}

		if(event.isKeyDown(KeyEvent.TSK_LEFT_ARROW)) {

			angleY += 5;

		}
		else if(event.isKeyDown(KeyEvent.TSK_RIGHT_ARROW)) {

			angleY -= 5;

		}

		if(event.isKeyDown(KeyEvent.TSK_M)) {

			angleZ -= 5;

		}
		else if(event.isKeyDown(KeyEvent.TSK_N)) {

			angleZ += 5;

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
	public void display(GLAutoDrawable drawable) {

		GL2 gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		gl.glClearColor(1f, 1f, 1f, 1);

		//Transform by Camera
		updateCamera(gl, cameraGL);

		gl.glTranslated(offsetX, offsetY, offsetZ);

		gl.glRotated(angleX, 1, 0, 0);
		gl.glRotated(angleY, 0, 1, 0);
		gl.glRotated(angleZ, 0, 0, 1);

		//Draw Scene

		drawFloor(gl);

		//gl.glFlush();

		pipCamera = Screenshot.readToBufferedImage(w, h, false);

		//Erasing Window Title Black Rectangle
		pipCamera.getGraphics().setColor(Color.WHITE);

		pipCamera.getGraphics().fillRect(0, 0, w, 50);

	}

	protected void drawSphere(GL2 gl) {

		final float radius = 1.378f;

		drawSphere(gl, radius, 0, radius, 0);
	}

	protected void drawSphere(GL2 gl, double radius, double x, double y, double z) {

		final int slices = 16;
		final int stacks = 16;

		gl.glPushMatrix();

		// Draw sphere (possible styles: FILL, LINE, POINT).
		gl.glColor3f(0.3f, 0.5f, 1f);

		gl.glTranslated(x, y, z);

		GLUquadric earth = glu.gluNewQuadric();

		glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
		glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);

		glu.gluSphere(earth, radius, slices, stacks);

		glu.gluDeleteQuadric(earth);

		gl.glPopMatrix();
	}

	protected void drawCube(GL2 gl) {

		float x = 0;

		float y = 0;

		float z = 0;

		gl.glColor3f(0.3f, 0.5f, 1f);

		gl.glPushMatrix();

		gl.glTranslated(0, 1, 0);

		gl.glTranslated(x, y, z);

		gl.glPushMatrix();
		drawSquare(gl);        // front face is red
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(180,0,1,0); // rotate square to back face
		drawSquare(gl);        // back face is cyan
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(-90,0,1,0); // rotate square to left face
		drawSquare(gl);       // left face is green
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(90,0,1,0); // rotate square to right face
		drawSquare(gl);       // right face is magenta
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(-90,1,0,0); // rotate square to top face
		drawSquare(gl);
		gl.glPopMatrix();

		gl.glPushMatrix();
		gl.glRotatef(90,1,0,0); // rotate square to bottom face
		drawSquare(gl);
		gl.glPopMatrix();

		gl.glPopMatrix();

	}

	//Drawing Nehe Pyramid
	protected void drawPyramid(GL2 gl) {

		float size = 1.0f;
		
		gl.glPushMatrix();
		//gl.glScaled(1.8, 1.8, 1.8);
		//gl.glScaled(1, 1, 1);

		//gl.glTranslated(0, 1, 0);

		gl.glBegin(GL.GL_TRIANGLES);        // Drawing Using Triangles
		gl.glColor3f(size, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, size, 0.0f);    // Top Of Triangle (Front)
		gl.glColor3f(0.0f, size, 0.0f);     // Green
		gl.glVertex3f(-size, -size, size);  // Left Of Triangle (Front)
		gl.glColor3f(0.0f, 0.0f, size);     // Blue
		gl.glVertex3f(size, -size, size);   // Right Of Triangle (Front)
		gl.glColor3f(size, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, size, 0.0f);    // Top Of Triangle (Right)
		gl.glColor3f(0.0f, 0.0f, size);     // Blue
		gl.glVertex3f(size, -size, size);   // Left Of Triangle (Right)
		gl.glColor3f(0.0f, size, 0.0f);     // Green
		gl.glVertex3f(size, -size, -size);  // Right Of Triangle (Right)
		gl.glColor3f(size, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, size, 0.0f);    // Top Of Triangle (Back)
		gl.glColor3f(0.0f, size, 0.0f);     // Green
		gl.glVertex3f(size, -size, -size);  // Left Of Triangle (Back)
		gl.glColor3f(0.0f, 0.0f, size);     // Blue
		gl.glVertex3f(-size, -size, -size); // Right Of Triangle (Back)
		gl.glColor3f(size, 0.0f, 0.0f);     // Red
		gl.glVertex3f(0.0f, size, 0.0f);    // Top Of Triangle (Left)
		gl.glColor3f(0.0f, 0.0f, size);     // Blue
		gl.glVertex3f(-size, -size, -size); // Left Of Triangle (Left)
		gl.glColor3f(0.0f, size, 0.0f);     // Green
		gl.glVertex3f(-size, -size, size);  // Right Of Triangle (Left)
		gl.glEnd();                         // Finished Drawing The Triangle

		gl.glPopMatrix();

	}

	private void drawSquare(GL2 gl) {

		float size = 1;

		gl.glTranslatef(0,0,size);

		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2f(-size,-size);    // Draw the square (before the
		gl.glVertex2f(size,-size);     //   the translation is applied)
		gl.glVertex2f(size,size);      //   on the xy-plane, with its
		gl.glVertex2f(-size,size);     //   at (0,0,0).
		gl.glEnd();

	}

	@Override
	public void draw(Graphic g) {

		int size = 100;

		//Draw Gui
		g.setColor(Color.WHITE);
		g.drawShadow(20,20, "Scene",Color.BLACK);

		g.drawShadow(20,40, "AngleX: "+(angleX-5),Color.BLACK);

		g.drawShadow(20,60, "AngleY: "+(angleY),Color.BLACK);

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

		BufferedImage camera = op.filter(pipCamera, null);

		g.drawImage(camera, 0, 0);

	}

}
