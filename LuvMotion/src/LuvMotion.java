import br.com.luvia.Luvia;
import br.com.luvia.core.ApplicationGL;
import br.com.luvmotion.ImageProcessingGL;
import br.com.luvmotion.LuvMotionReality;


public class LuvMotion extends Luvia{

	public LuvMotion() {
		super(1024,576);
	}

	// Main program
	public static void main(String[] args) {

		LuvMotion luvmotion = new LuvMotion();
		
		luvmotion.init();

	}
	
	@Override
	public ApplicationGL startApplication() {
		
		//return new LuvMotionReality(w, h);
		
		return new ImageProcessingGL(w, h);
		
	}

}
