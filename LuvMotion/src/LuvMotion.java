import br.com.luvia.Luvia;
import examples.AugmentedReality;


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
	public void startGame() {
		
		setMainApplication(new AugmentedReality(w, h));
		
	}

}
