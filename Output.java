import lejos.nxt.Motor;
import lejos.robotics.navigation.TachoPilot;
import lejos.robotics.navigation.Pilot;

/**
 * << Output is part of our maze solving robotics system Rhino >>
 *
 * @author Darren Paskell and David Hollands
 * @version $0.85a$ 
 */
public class Output {

	private TachoPilot navigator;
	private Pilot pilot;
	private Motor left_motor, right_motor;
	
	private double left_motor_speed, right_motor_speed;
	private boolean debug, moving_towards_wall;
	private boolean left_wall = true;
	private int count_since_change = 0;
	private double input = 0, E = 0;
		
	private static final int DESIRED_SPEED = 560; 
	private static final int DESIRED_TURN_SPEED = 480; //900
	private int BASE_SPEED = 480; //90//100 - must be lower than 900 for control theory to work
	private static final int BLOCK_SIZE = 230; //175

  	// {{{ Output constructor
	/**
    	 *  The params are of an obvious nature... 
   	 */
	public Output(TachoPilot my_navi, int base_speed, boolean debug) {
	 	navigator = my_navi;
		navigator.setSpeed(DESIRED_SPEED);
		pilot = navigator;
		left_motor = (Motor) ((TachoPilot) pilot).getLeft();
		right_motor = (Motor) ((TachoPilot) pilot).getRight();
		this.BASE_SPEED = base_speed;
		this.debug = debug;
  	}
	// }}}

	public void moveForwardOneBlock(boolean control) {
		if(control) {
				left_motor.resetTachoCount();
				right_motor.resetTachoCount();

				float degPerDistance = 360 / ((float) Math.PI * 56);
				double tacho = (left_motor.getTachoCount() + right_motor.getTachoCount()) / 2;
			
				while(tacho < degPerDistance * BLOCK_SIZE) {
					count_since_change++;

					if(left_wall) {
						left_motor_speed = BASE_SPEED + input;
						right_motor_speed = BASE_SPEED - input;
					} else {
						left_motor_speed = BASE_SPEED - input;
						right_motor_speed = BASE_SPEED + input;
					}
					
					if(left_motor_speed > 0) left_motor.setSpeed((int)left_motor_speed);
					else left_motor.setSpeed(0);
					
					if(right_motor_speed > 0) right_motor.setSpeed((int)right_motor_speed);
					else right_motor.setSpeed(0);
					
					left_motor.forward();
					right_motor.forward();
					tacho = (left_motor.getTachoCount() + right_motor.getTachoCount()) / 2;

					if(debug) {
						System.out.println("t:" + tacho + "d: " + degPerDistance * BLOCK_SIZE);
						System.out.println((int)left_motor_speed + " : " + (int)right_motor_speed);
					}
				}

				left_motor.stop();
				right_motor.stop();
		} else {
			pilot.setSpeed(DESIRED_SPEED);
			pilot.travel(180.0f);
		}
	}
	
	public void turnLeft() {
		pilot.setSpeed(DESIRED_TURN_SPEED); //85
		pilot.steer(200, 90);
	}
	
	public void turnRight() {
		pilot.setSpeed(DESIRED_TURN_SPEED); //85
		pilot.steer(-200, 90);
	}
	
	public void aboutTurn() {
		pilot.setSpeed(DESIRED_TURN_SPEED); //85
		pilot.steer(-200, 180);
	}
	
	public void resetCount() {
		count_since_change = 0;
	}
	
	public void setInput(double input, boolean left, double E) {
			if(left_wall != left) resetCount(); 
			left_wall = left;
			this.E = E;
			this.input = input;
	}
}
