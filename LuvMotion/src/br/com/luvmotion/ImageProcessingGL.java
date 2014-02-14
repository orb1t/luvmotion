package br.com.luvmotion;

import java.awt.Color;
import java.awt.image.BufferedImage;

import br.com.etyllica.core.event.GUIEvent;
import br.com.etyllica.core.event.KeyEvent;
import br.com.etyllica.core.video.Graphic;
import br.com.etyllica.linear.Point2D;
import br.com.etyllica.motion.features.BoundingComponent;
import br.com.etyllica.motion.features.Component;
import br.com.etyllica.motion.filter.color.ColorStrategy;
import br.com.etyllica.motion.filter.modifier.AugmentedMarkerModifier;
import br.com.etyllica.motion.filter.search.FloodFillSearch;

public class ImageProcessingGL extends LuvMotionReality{

	//Image Processing Stuff	
	protected FloodFillSearch cornerFilter;

	protected ColorStrategy colorStrategy;

	protected AugmentedMarkerModifier modifier;

	protected boolean hide = true;
	protected boolean pixels = true;

	protected int xOffset = 512;
	protected int yOffset = 0;

	protected Component feature;

	public ImageProcessingGL(int w, int h) {
		super(w, h);
	}

	@Override
	public void load(){
		super.load();

		loadingPhrase = "Configuring Filter";

		int width = pipCamera.getWidth();

		int height = pipCamera.getHeight();

		loading = 40;

		colorStrategy = new ColorStrategy(borderColor);
		colorStrategy.setTolerance(0x30);

		modifier = new AugmentedMarkerModifier();

		cornerFilter = new FloodFillSearch(width, height);

		cornerFilter.setBorder(20);
		cornerFilter.setStep(1);

		cornerFilter.setColorStrategy(colorStrategy);

		cornerFilter.setComponentModifierStrategy(modifier);

		feature = new BoundingComponent(w, h);

	}

	@Override
	public GUIEvent updateKeyboard(KeyEvent event) {
		super.updateKeyboard(event);

		if(event.isKeyDown(KeyEvent.TSK_SETA_DIREITA)){

			hide = false;

		}

		return GUIEvent.NONE;

	}

	private void reset(BufferedImage b){
		
		loading = 60;

		loadingPhrase = "Start Filter";

		feature = cornerFilter.filterFirst(b, new BoundingComponent(b.getWidth(), b.getHeight()));
		
		loading = 65;
		loadingPhrase = "Show Result";

		loading = 70;
		loadingPhrase = "Show Angle";
	}

	@Override
	public void draw(Graphic g) {
		super.draw(g);
		
		if(!hide){
			
			reset(pipCamera);

			g.drawImage(pipCamera, xOffset, yOffset);

			if(feature!=null){
				
				g.setColor(Color.BLUE);

				for(Point2D ponto: feature.getPoints()){
					g.fillCircle(xOffset+(int)ponto.getX(), yOffset+(int)ponto.getY(), 5);
				}

				int textHeight = 25;

				if(feature.getPoints().size()>3){

					drawBox(g, feature);

					g.drawString("Points = "+feature.getPoints().size(), 50, textHeight+25);

					g.drawString("AngleX = "+modifier.getAngleX(), 50, textHeight+50);

					g.drawString("AngleY = "+modifier.getAngleY(), 50, textHeight+75);

				}
			}
		}

	}

	private void drawBox(Graphic g, Component box){

		g.setColor(Color.RED);

		Point2D a = box.getPoints().get(0);
		Point2D b = box.getPoints().get(1);
		Point2D c = box.getPoints().get(2);
		Point2D d = box.getPoints().get(3);

		Point2D ac = new Point2D((a.getX()+c.getX())/2, (a.getY()+c.getY())/2);
		Point2D ab = new Point2D((a.getX()+b.getX())/2, (a.getY()+b.getY())/2);

		Point2D bd = new Point2D((b.getX()+d.getX())/2, (b.getY()+d.getY())/2);
		Point2D cd = new Point2D((c.getX()+d.getX())/2, (c.getY()+d.getY())/2);

		drawLine(g, a, b);
		drawLine(g, a, c);

		drawLine(g, b, d);
		drawLine(g, c, d);

		drawPoint(g, a);
		drawPoint(g, b);
		drawPoint(g, c);
		drawPoint(g, d);

		g.setColor(Color.YELLOW);
		drawLine(g, ab, cd);
		drawPoint(g, ab);
		drawPoint(g, cd);

		g.setColor(Color.GREEN);
		drawLine(g, ac, bd);

		drawPoint(g, ac);
		drawPoint(g, bd);

		g.setColor(Color.ORANGE);
		drawPoint(g, box.getCenter());

		g.setColor(Color.BLACK);
		g.drawString("A", xOffset+(int)a.getX()-20, yOffset+(int)a.getY()-10);
		g.drawString("B", xOffset+(int)b.getX()+15, yOffset+(int)b.getY()-10);

		g.drawString("C", xOffset+(int)c.getX()-20, yOffset+(int)c.getY()+10);
		g.drawString("D", xOffset+(int)d.getX()+15, yOffset+(int)d.getY()+10);

	}

	private void drawLine(Graphic g, Point2D a, Point2D b){		
		g.drawLine(xOffset+(int)a.getX(), yOffset+(int)a.getY(), xOffset+(int)b.getX(), yOffset+(int)b.getY());		
	}

	private void drawPoint(Graphic g, Point2D point){
		g.fillCircle(xOffset+(int)point.getX(), yOffset+(int)point.getY(), 3);
	}

}
