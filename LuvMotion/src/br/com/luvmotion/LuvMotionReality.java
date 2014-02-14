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

import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.event.PointerEvent;
import br.com.etyllica.core.input.mouse.MouseButton;
import br.com.etyllica.core.video.Graphic;
import br.com.luvia.core.ApplicationGL;
import br.com.luvia.loader.TextureLoader;
import br.com.luvia.util.CameraGL;

import com.jogamp.opengl.util.awt.Screenshot;
import com.jogamp.opengl.util.texture.Texture;

public class LuvMotionReality extends ApplicationGL {
	
	//Scene Stuff
	private Texture marker;

	private CameraGL camera;
	
	protected float mx = 0;
	
	protected float my = 0;

	protected boolean click = false;
	
	protected double angleX = 0;
	
	protected double angleY = 0;
	
	protected BufferedImage pipCamera;
	
	protected Color borderColor = Color.RED;
	
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
	
		camera = new CameraGL(0,15,1);
		
		BufferedImage image = new BufferedImage(200, 200, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = image.createGraphics();
		
		pipCamera = image;
				
		g.setColor(Color.WHITE);
		g.fillRect(0, 0, 200, 200);
		
		g.setColor(Color.BLUE);
		g.fillOval(50, 50, 100, 100);
		
		g.setColor(borderColor);
		g.setStroke(new BasicStroke(5f));
		g.drawRect(5, 5, 190, 190);
		
		marker = TextureLoader.getInstance().loadTexture(image);
		
	}
	
	protected void lookCamera(GL2 gl) {
		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		double targetx = 0;
		double targety = 0;
		double targetz = 0;
		
		glu.gluLookAt( camera.getX(), camera.getY(), camera.getZ(), targetx, targety, targetz, 0, 1, 0 );

	}
	
	protected void drawFloor(GL2 gl) {

		gl.glColor3d(1,1,1);

		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
		gl.glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
		
		drawGrid(gl,200,120);		

	}

	private void drawGrid(GL2 gl, double x, double y) {

		double tileSize = 5;

		marker.enable(gl);
		marker.bind(gl);

		drawTile(gl, -.5, -.5, tileSize);
		
		marker.disable(gl);
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

		gl.glLoadIdentity();

	}	

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		
		if(event.isKeyDown(KeyEvent.TSK_UP_ARROW)){
			
			angleX += 5;
			
		}
		else if(event.isKeyDown(KeyEvent.TSK_DOWN_ARROW)){
			
			angleX -= 5;
			
		}
		
		if(event.isKeyDown(KeyEvent.TSK_LEFT_ARROW)){
			
			angleY += 5;
			
		}
		else if(event.isKeyDown(KeyEvent.TSK_RIGHT_ARROW)){
			
			angleY -= 5;
			
		}
		
		return GUIEvent.NONE;
	}
	
	public GUIEvent updateMouse(PointerEvent event) {

		mx = event.getX();
		my = event.getY();

		if(event.onButtonDown(MouseButton.MOUSE_BUTTON_LEFT)){
			camera.setZ(camera.getZ()+0.1f);
			click = true;
		}

		if(event.onButtonUp(MouseButton.MOUSE_BUTTON_LEFT)){
			camera.setZ(camera.getZ()-0.1f);
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
		lookCamera(drawable.getGL().getGL2());
		
		gl.glRotated(angleX, 1, 0, 0);
		gl.glRotated(angleY, 0, 1, 0);

		//Draw Scene

		drawFloor(gl);
		
		//gl.glFlush();
			
		pipCamera = Screenshot.readToBufferedImage(w, h, false);
						
	}
	
	protected void drawSphere(GLAutoDrawable drawable){
		
		GL2 gl = drawable.getGL().getGL2();
		
        final float radius = 1.378f;
        final int slices = 16;
        final int stacks = 16;
		
        gl.glPushMatrix();
        
		// Draw sphere (possible styles: FILL, LINE, POINT).
        gl.glColor3f(0.3f, 0.5f, 1f);
        gl.glTranslated(0, radius, 0);
        
        GLUquadric earth = glu.gluNewQuadric();
        
        glu.gluQuadricDrawStyle(earth, GLU.GLU_FILL);
        glu.gluQuadricNormals(earth, GLU.GLU_FLAT);
        glu.gluQuadricOrientation(earth, GLU.GLU_OUTSIDE);
                
        glu.gluSphere(earth, radius, slices, stacks);
        
        glu.gluDeleteQuadric(earth);
        
        gl.glPopMatrix();
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
	
	private void drawPipCamera(Graphic g) {
		
		//AffineTransform transform = AffineTransform.getScaleInstance(640/w, 480/h);
		AffineTransform transform = AffineTransform.getScaleInstance(0.2, 0.2);

		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_NEAREST_NEIGHBOR);

		BufferedImage camera = op.filter(pipCamera, null);

		g.drawImage(camera, 0, 0);
		
	}
	
}