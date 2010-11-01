import lejos.nxt.MotorPort;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.addon.EOPD;
import lejos.robotics.navigation.Pilot;
import lejos.robotics.navigation.TachoPilot;

/**
 * << Rhino is the name of our maze solving robotics system >>
 * Freely distributable, please send any improvements to one of the authors.
 * Emailing d.s.hollands@cs.rhul.ac.uk is probably your best bet.
 *
 * @author Caroline Drinkwater, Darren Paskell and David Hollands
 * @version $0.85$ (c) 2008-2010
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
	public Rhino(TachoPilot navigator, EOPD left, EOPD centre, EOPD right, 
	             int desired_val, double p_k, double p_d, double p_i, int time_interval,
                     int base_speed) {

		// Maybe think about more fine grained debugging system?
		state = new State(navigator, left, centre, right, true);
		output = new Output(navigator, base_speed, false);
		
		Runnable control = new Control(state, output, desired_val, p_k, p_d, p_i, time_interval, false);
		Thread control_thread = new Thread(control);
 		control_thread.start();
		
		Runnable behaviour = new Behaviour(state, output, false);
		Thread behaviour_thread = new Thread(behaviour);
		behaviour_thread.start();
	}
	// End of Rhino constructor }}}
	
	// {{{ Main method
	/**
	 * Configure navigator and sensors.
	 */
	public static void main(String[] args) {
		// 43.2f, 97.25f :: 56.0f, 95.25f :: 81.6f, 90.25f 
		// (Lego** wheel diameters and Rhino axle widths)
		Rhino rhino = new Rhino(
		  new TachoPilot(43.2f, 97.25f, new Motor(MotorPort.A), new Motor(MotorPort.C), false), 
		  new EOPD(SensorPort.S1, true), 
		  new EOPD(SensorPort.S2, true),
		  new EOPD(SensorPort.S3, true),
		  200, // desired value output
		  0.3f, // P_K
		  90.0f, // P_D
		  0.001f, // P_I
		  50, // T (interval)
		  450 // BASE_SPEED
		);
	}
	// End of main method }}}
}
// **Lego is a trademark of Lego etc.
