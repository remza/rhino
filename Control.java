/**
 * < Control is part of our maze solving robotics system Rhino >
 *
 * @author David Hollands and Darren Paskell
 * @version $0.85a$ 
 */
public class Control implements Runnable {

	private State state;
	private Output output;
        private static int T = 100; //250
        private static double DESIRED_READING = 300;
        private static double UPPER_BOUND = 900;
	private static double K_P = 0.133667;
	private static double K_D = 89.6667;
	private static double K_I = 0.001;
	private double previous_error = 0, previous_right_error = 0, previous_left_error = 0;
	private double error = 0, left_error = 0, right_error = 0;
	private boolean debug = false;
	
	// {{{ Control constructors
	/**
	 *  Currently we only have one. 
	 */
	public Control(State state, Output output,
		int desired_reading, double k_p, double k_d, double k_i, int time_interval, boolean debug) {
		this.state = state;
		this.output = output;
		this.DESIRED_READING = desired_reading;
		this.K_P = k_p;
		this.K_D = k_d;
		this.K_I = k_i;
		this.T = time_interval;
		this.debug = debug;
	}
	// End of Control constructors }}}

	public void run() {
		try {
			double previous_tacho_count = 0, current_tacho_count = 0, distance_travelled = 0;
			double previous_wall_reading = 0, current_wall_reading = 0, wall_reading_delta = 0;
			double current_angle = 0;
			double sine_theta = 0;
			double input = 0, E = 0; // total errors
			boolean left = true;
			
			// initialise for the sake of E?
			// previous_wall_reading = state.getLeftReading();
			
			while(true) {
				while(state.isCharging()) {		
					// The arrows <-- indicate code *not* used by the current PID control model
					// work out distance travelled since last loop
					previous_tacho_count = current_tacho_count; // <--
					current_tacho_count = state.getTachoCount(); // <--
					distance_travelled = current_tacho_count - previous_tacho_count; // <--
					
					// work out the range reading difference (delta)
					previous_wall_reading = current_wall_reading; // <--
					wall_reading_delta = previous_wall_reading - current_wall_reading; // <--
					
					// calculate sine theta (opposite / hypotenuse)	
					sine_theta = wall_reading_delta / distance_travelled; // <--
					current_angle = Math.toDegrees(Math.asin(sine_theta)); // <--
					state.setAngle((float) current_angle); // <--
					
					// save previous errors
					previous_left_error = left_error;
                                        previous_right_error = right_error;
					previous_error = error;

					// get current errors
					left_error = DESIRED_READING - state.getLeftReading();
					right_error = DESIRED_READING - state.getRightReading();

					// select left or right or none? currently left has precedence
					if(state.getLeftReading() <= UPPER_BOUND) {
						left = true;
						previous_error = previous_left_error;
						error = left_error;
					
					} else if(state.getRightReading() <= UPPER_BOUND) {
						left = false;
						previous_error = previous_right_error;
						error = right_error;
					
					} else {
						this.setAllErrorsToZero();
					}
					
					// calculate input using PID control 
					E = E + error;
					input = ((K_P * error) + (K_D * ((error - previous_error) / T)) + K_I * E);
					if(debug) System.out.println("Input: " + input);

					// set new input
					output.setInput(input, left, E);
					
					// wait 250 ms
					Thread.sleep(T); 	
				}
				while(!state.isCharging()) {
					this.setAllErrorsToZero();
				}
			}
		} catch(InterruptedException ex) { }
	}

	public void setAllErrorsToZero() {
		previous_error = 0; 
		error = 0;
		previous_left_error = 0;
		left_error = 0;
		previous_right_error = 0;
		right_error = 0;
	}	

	public void explicitlySetError(double error) {
		this.error = error;
	}	
}
