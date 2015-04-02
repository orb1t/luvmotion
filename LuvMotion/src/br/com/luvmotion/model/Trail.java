package br.com.luvmotion.model;

import java.util.ArrayList;
import java.util.List;

import br.com.abby.linear.Point3D;

public class Trail {

	private int size = 10;
	private int currentPoint = 0;
	
	private List<Point3D> trail;
	private List<Point3D> points;
	
	public Trail() {
		this(10);
	}
	
	public Trail(int size) {
		super();
		
		this.size = size;
		trail = new ArrayList<Point3D>(size);
		points = new ArrayList<Point3D>(size);
		
		reset(size);
	}

	protected void reset(int size) {
		trail.clear();
		points.clear();
		
		for(int i=0;i<size;i++) {
			points.add(new Point3D());
		}
	}
	
	public void add(Point3D point) {
		if(trail.size()>size) {
			trail.remove(0);
		}
		
		Point3D copy = points.get(currentPoint);
		copy.setCoordinates(point.getX(), point.getY(), point.getZ());
		
		trail.add(copy);
		currentPoint++;
		currentPoint%=size;		
	}
	
}
