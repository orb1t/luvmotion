package br.com.luvmotion;

import java.awt.Color;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.glu.GLU;
import javax.media.opengl.glu.GLUquadric;

import br.com.luvia.core.ApplicationGL;

public abstract class LuvMotionApplication extends ApplicationGL {

	public LuvMotionApplication(int w, int h) {
		super(w, h);
	}

	protected void drawSphere(GL2 gl) {

		final float radius = 1;

		drawSphere(gl, radius, 0, radius, 0);
	}

	protected void drawSphere(GL2 gl, double radius, double x,
			double y, double z, int resolution, Color color) {
		
		final int slices = resolution;
		final int stacks = resolution;

		gl.glPushMatrix();
	
		setColor(gl, color);

		gl.glTranslated(x, y, z);

		GLUquadric sphere = generateSphereQuadric();

		glu.gluSphere(sphere, radius, slices, stacks);

		glu.gluDeleteQuadric(sphere);

		gl.glPopMatrix();		
	}

	public void setColor(GL2 gl, Color color) {
		float red = ((float)color.getRed()/255);
		float green = ((float)color.getGreen()/255);
		float blue = ((float)color.getBlue()/255);
				
		gl.glColor3f(red, green, blue);
	}
	
	protected void drawSphere(GL2 gl, double radius, double x,
			double y, double z, int resolution) {
		
		final int slices = resolution;
		final int stacks = resolution;

		gl.glPushMatrix();

		// Draw sphere (possible styles: FILL, LINE, POINT).
		gl.glColor3f(0.3f, 0.5f, 1f);

		gl.glTranslated(x, y, z);

		GLUquadric sphere = generateSphereQuadric();

		glu.gluSphere(sphere, radius, slices, stacks);

		glu.gluDeleteQuadric(sphere);

		gl.glPopMatrix();
		
	}
	
	private GLUquadric generateSphereQuadric() {
		GLUquadric sphere = glu.gluNewQuadric();

		// Draw sphere (possible styles: FILL, LINE, POINT)
		glu.gluQuadricDrawStyle(sphere, GLU.GLU_FILL);
		glu.gluQuadricNormals(sphere, GLU.GLU_FLAT);
		glu.gluQuadricOrientation(sphere, GLU.GLU_OUTSIDE);
		
		return sphere;
	}
	
	protected void drawSphere(GL2 gl, double radius, double x,
			double y, double z) {
		
		drawSphere(gl, radius, x, y, z, 16);
	}

	protected void drawCube(GL2 gl) {

		gl.glPushMatrix();
		
		gl.glColor3f(0.3f, 0.5f, 1f);

		gl.glTranslated(0, 0.5, 0);

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

	protected void drawPyramid(GL2 gl) {

		float size = 1.0f/2;

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

		float size = 1.0f/2;

		gl.glTranslatef(0,0,size);

		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2f(-size,-size);    // Draw the square (before the
		gl.glVertex2f(size,-size);     //   the translation is applied)
		gl.glVertex2f(size,size);      //   on the xy-plane, with its
		gl.glVertex2f(-size,size);     //   at (0,0,0).
		gl.glEnd();

	}

	public void drawTile(GL2 gl, double x, double y, double tileSize) {

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


}
