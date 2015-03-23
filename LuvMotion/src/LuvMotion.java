import br.com.luvia.Luvia;
import br.com.luvia.core.context.ApplicationGL;
import br.com.luvmotion.ar.ImageProcessingGL;
import br.com.luvmotion.ar.LuvMotionReality;
import br.com.luvmotion.ar.PositExample;
import br.com.luvmotion.ar.PositProcessingGL;
import br.com.luvmotion.motion.DualSpheres;
import br.com.luvmotion.motion.MotionSphere;


public class LuvMotion extends Luvia {

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
		
		//Augmented Reality Examples
		//return new LuvMotionReality(w, h);
		//return new ImageProcessingGL(w, h);
		//return new PositProcessingGL(w, h);
		//return new PositExample(w, h);

		//Motion Examples
		//return new MotionSphere(w, h);
		return new DualSpheres(w, h);
		
	}

}
