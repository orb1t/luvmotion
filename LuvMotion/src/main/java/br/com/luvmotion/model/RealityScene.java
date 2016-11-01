package br.com.luvmotion.model;

import javax.media.opengl.GL2;

import br.com.abby.linear.AimPoint;

public class RealityScene extends AimPoint {
	
	public RealityScene() { }
	
	public RealityScene(double angleX, double angleY, double angleZ,
			float offsetX, float offsetY, float offsetZ) {
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
		this.x = offsetX;
		this.y = offsetY;
		this.z = offsetZ;
	}
	
	public void updateScene(GL2 gl) {
		
		gl.glTranslated(x, y, z);
		
		gl.glRotated(angleX, 1, 0, 0);
		gl.glRotated(angleY, 0, 1, 0);
		gl.glRotated(angleZ, 0, 0, 1);
	}
}