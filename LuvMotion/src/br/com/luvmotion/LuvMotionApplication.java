package br.com.luvmotion;

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
	
		final float radius = 1.378f;
	
		drawSphere(gl, radius, 0, radius, 0);
	}

	protected void drawSphere(GL2 gl, double radius, double x,
			double y, double z) {
			
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
	
		gl.glColor3f(0.3f, 0.5f, 1f);
	
		gl.glPushMatrix();
	
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
