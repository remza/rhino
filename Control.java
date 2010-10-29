/**
 * << Control is part of our maze solving robotics system Rhino >>
 *
 * @author David Hollands and Darren Paskell
 * @version $0.1a$ 
 */
public class Control implements Runnable {
	private State state;
	private Output output;
        private static final int T = 100; //250
        private static final double DESIRED_READING = 190;
        private static final double UPPER_BOUND = 900;
	private static final double K_P = 0.233667;
	private static final double K_D = 99.6667;
	private static final double K_I = 0.001;
	private static final boolean use_angle = false;
	private static final boolean use_old_method = false;
	private static final boolean use_left_only = false;
	private double error = 0;
        private double left_error = 0;
        private double right_error = 0;
	
	// {{{ Control constructors
	/**
	 * 
	 */
	public Control(State my_state, Output my_output) {
		state = my_state;
		output = my_output;
	}

	public void run() {
		try {
			double previous_error = 0, previous_right_error = 0, previous_left_error = 0;
			double previous_tacho_count, current_tacho_count = 0, distance_travelled;
			double previous_wall_reading, current_wall_reading = 0, wall_reading_delta;
			double current_angle;
			double sine_theta;
			double input, E = 0; // total errors
			boolean left = true;
			
			// initialise for the sake of E
			previous_wall_reading = state.getLeftReading();
			
			while(true) {
				while(state.isCharging()) {		
					
					// work out distance travelled since last loop
					previous_tacho_count = current_tacho_count;
					current_tacho_count = state.getTachoCount();
					distance_travelled = current_tacho_count - previous_tacho_count;
					
					// work out the range reading difference (delta)
					previous_wall_reading = current_wall_reading;
					
					// save previous error
					previous_left_error = left_error;
                                        previous_right_error = right_error;
					previous_error = error;

					//
					left_error = DESIRED_READING - state.getLeftReading();
					right_error = DESIRED_READING - state.getRightReading();


					if(state.getLeftReading() <= UPPER_BOUND) {
					//	if(state.getLeftReading() < state.getRightReading()) {
							left = true;
							
				//			current_wall_reading = state.getLeftReading();
				//		} else {
				//			left = false;
				//			current_wall_reading = state.getRightReading();
				//		}

						previous_error = previous_left_error;
						error = left_error;
					
					} else if(state.getRightReading() <= UPPER_BOUND) {
						left = false;
						previous_error = previous_right_error;
						error = right_error;
					
					} else {
				           	previous_error = 0; 
						error = 0;
						previous_left_error = 0;
						left_error = 0;
						previous_right_error = 0;
						right_error = 0;
					}
					wall_reading_delta = previous_wall_reading - current_wall_reading;
				
					// calculate sine theta (opposite / hypotenuse)	
					sine_theta = wall_reading_delta / distance_travelled;
					current_angle = Math.toDegrees(Math.asin(sine_theta));
					state.setAngle((float)current_angle);
					
					// 
					

					// choose PID control modeA 
/**
					if(use_angle) {
						error = current_angle;
					} else {
                                                if(use_old_method)
							error = wall_reading_delta;
						else if(use_left_only) {
							error = DESIRED_READING - current_wall_reading;
						} else {
							if (left) {
								previous_error = previous_left_error;
								error = left_error;
							} else {
								previous_error = previous_right_error;
								error = right_error;
							}	
						}
					}			
**/					
					// create error
					E = E + error;
					System.out.println("E: " + E);
					input = ((K_P * error) + (K_D * ((error - previous_error) / T)) + K_I * E);
					
					// set input
					output.setError(input, left, E);
					
					// wait 250 ms
					Thread.sleep(T); 	
				}
//				while(!state.isCharging()) {
//					error = 0;
//					previous_error = 0;
//					current_wall_reading = previous_wall_reading;
//				}
			}
		} catch(InterruptedException ex) { }
	}

	public void explicitlySetError(double error) {
		this.error = error;
	}	
}


				//		wallDeltasFIFO.add(wall_reading_delta);
						
						
						
						
						
						
						
						
						
						//wallDeltasFIFO.mean()
						
						
						

/*					
					// get time
					previous_time = current_time;
					current_time = state.getTime();
					
					// calculate errors
					input = 
						(K_p * current_error) + 
						(K_d * (current_error - previous_error / (current_time - previous_time)));// +
						//(K_i * E); E = E + current_error;

//						System.out.println("input: " + input);
						output.controlCharge(input);

	//			}
*/
/*
 class NumberCircle extends Vector
{


  // constructor

  public NumberCircle (int intendedSize)
  {
    super(intendedSize);
  } // end NumberCircle


  public void add(int newNumber)
  {
    Integer newElement = new Integer(newNumber);
    if (size() >= elementData.length)
      {
      removeElementAt(capacityIncrement);
    } // end if size() > elementData.length
    addElement(newElement);
  } // end add


  public double mean()
  {
    int sum = 0;
    double dMean;
    for (int currentIndex = capacityIncrement; currentIndex < size(); currentIndex++)
      {
      Integer oIndex = (Integer) elementAt(currentIndex);
      sum += Integer.parseInt(oIndex.toString());
      } // end iteration
    dMean = sum / size();
    return (dMean);
  } // end mean



} // class NumberCircle

*/

