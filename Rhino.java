import lejos.nxt.MotorPort;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.EOPD;
import lejos.robotics.navigation.Pilot;
import lejos.robotics.navigation.TachoPilot;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * << Rhino is the name of our maze solving robotics system >>
 *
 * @author Darren Paskell and David Hollands
 * @version $0.1a$ 
 */
public final class Rhino {
	
	private State state;
	private Control control;
	private Behaviour behaviour;
	private Output output;
	
	// {{{ Rhino constructor
	/**
	 * Set up state, threads, filter and output.
	 */
	public Rhino(TachoPilot navigator, EOPD left, EOPD centre, EOPD right) {
		
		state = new State(navigator, left, centre, right);
		output = new Output(navigator);
		
		Runnable control = new Control(state, output);
		Thread control_thread = new Thread(control);
 		control_thread.start();
		
		Runnable behaviour = new Behaviour(state, output);
		Thread behaviour_thread = new Thread(behaviour);
		behaviour_thread.start();
	}
	// End of Rhino constructor }}}
	
	// {{{ Main method
	/**
	 * Configure navigator and sensors.
	 */
	public static void main(String[] args) {
			
		Rhino rhino = new Rhino(
			new TachoPilot(43.2f, 97.25f, new Motor(MotorPort.A), new Motor(MotorPort.C), false), // 56.0f, 95.25f :: 81.6f, 90.25f
			new EOPD(SensorPort.S1, true), 
			new EOPD(SensorPort.S2, true),
			new EOPD(SensorPort.S3, true)
			);
		
	}
	// End of main method }}}
}
