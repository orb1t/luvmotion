package br.com.luvmotion.model;

import javax.media.opengl.GL2;

public class RealityScene {
	
	public double angleX;
	public double angleY;
	public double angleZ;
	public double offsetX;
	public double offsetY;
	public double offsetZ;

	public RealityScene() { }
	
	public RealityScene(double angleX, double angleY, double angleZ,
			double offsetX, double offsetY, double offsetZ) {
		this.angleX = angleX;
		this.angleY = angleY;
		this.angleZ = angleZ;
		this.offsetX = offsetX;
		this.offsetY = offsetY;
		this.offsetZ = offsetZ;
	}
	
	public void updateScene(GL2 gl) {
		
		gl.glTranslated(offsetX, offsetY, offsetZ);
		
		gl.glRotated(angleX, 1, 0, 0);
		gl.glRotated(angleY, 0, 1, 0);
		gl.glRotated(angleZ, 0, 0, 1);
	}
}