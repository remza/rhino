import lejos.nxt.Motor;
import lejos.robotics.navigation.TachoPilot;
import lejos.nxt.UltrasonicSensor;
import lejos.robotics.navigation.Pilot;
import lejos.util.Timer;
import lejos.util.TimerListener;


/**
 * << Output is part of our maze solving robotics system Rhino >>
 *
 * @author Darren Paskell and David Hollands
 * @version $0.1a$ 
 */
public class Output {
	private TachoPilot navigator;
	private Pilot pilot;
	private Motor left_motor;
	private Motor right_motor;
	
	private double left_motor_speed;
	private double right_motor_speed;
	private double prop;
	private boolean moving_towards_wall;
	private double error = 0;
	private double previous_error = 0;
	private boolean left_wall = true;
	private int count_since_change = 0;
	private double input = 0;
	private double E = 0;
		
	private static final int DESIRED_SPEED = 100; //100
	private static final int DESIRED_TURN_SPEED = 200; //900
	private static final int BASE_SPEED = 50; //90
	private static final int BLOCK_SIZE = 180; //175
    
	// {{{ Output constructor
  /**
   * 
   */
  public Output(TachoPilot my_navi) {
	 	navigator = my_navi;
		navigator.setSpeed(DESIRED_SPEED);
		pilot = navigator;
		left_motor = (Motor) ((TachoPilot) pilot).getLeft();
		right_motor = (Motor) ((TachoPilot) pilot).getRight();
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
					//System.out.println((int)left_motor_speed + " : " + (int)right_motor_speed);
					
					if(left_motor_speed > 0) left_motor.setSpeed((int)left_motor_speed);
					else left_motor.setSpeed(0);
					
					if(right_motor_speed > 0) right_motor.setSpeed((int)right_motor_speed);
					else right_motor.setSpeed(0);
					
					left_motor.forward();
					right_motor.forward();
					tacho = (left_motor.getTachoCount() + right_motor.getTachoCount()) / 2;
					//System.out.println("t:" + tacho + "d: " + degPerDistance * BLOCK_SIZE);
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
	
	public void setError(double e, boolean left, double E) {
// 			previous_error = error;
			if(left_wall != left) resetCount(); 
			left_wall = left;
			this.E = E;
			
			
// 		if(error > 100 && error < -100) {
//			System.out.println("dirty e:" + e);
//			error = 0;
//	 	}	else {
//			System.out.println("e: " + e);
			input = e;
//			E = E + error;
//			System.out.println("E: " + E);
//			input = ((K_P * error) + (K_D * ((error - previous_error) / 250)));
//		}
	}
	
	/*		
					if((int)((left_motor.getTachoCount()+right_motor.getTachoCount())/2) < (degPerDistance*BLOCK_SIZE)/4) {
							left_motor.setSpeed(BASE_SPEED);
							right_motor.setSpeed(BASE_SPEED);
							left_motor.forward();
							right_motor.forward();
						} else {
		//					System.out.println("l: " + ((left_motor.getTachoCount()+right_motor.getTachoCount())/2));
	
	
	
	while(navigator.isMoving()) {	

					prop = Math.abs(error) / 360;

					if(error < 0) {
						moving_towards_wall = true;
					} else {
						moving_towards_wall = false;
					}

					if (prop <= 1.0 && prop > 0) {
						if(moving_towards_wall) {
							left_motor_speed = prop * DESIRED_SPEED;
							right_motor_speed = (1 - prop) * DESIRED_SPEED;
						} else {
							left_motor_speed = prop * DESIRED_SPEED;
							right_motor_speed = (1 - prop) * DESIRED_SPEED;
						}
					} else {
						left_motor_speed = DESIRED_SPEED / 2;
						right_motor_speed = DESIRED_SPEED / 2;
					}

				left_motor.setSpeed((int)(BASE_SPEED + left_motor_speed));
				right_motor.setSpeed((int)(BASE_SPEED + right_motor_speed));

			}
	*/
	
		
/*		int current_tacho = right_motor.getTachoCount();
		while(right_motor.isMoving()) Thread.yield();
		
		while(right_motor.getTachoCount() < current_tacho + 150) {
			right_motor.forward();
			left_motor.backward();
		}
		
		right_motor.stop();
		left_motor.stop();
		
		if(right_motor.getTachoCount() % 150 != 0) {
			System.out.println("" + (right_motor.getTachoCount() % 150));
		}
*/		
		
		

		
/*		right_motor.resetTachoCount();
		
		while(right_motor.getTachoCount() >= -80) {
			System.out.println("" + left_motor.getTachoCount());
			right_motor.backward();
		}
		
		left_motor.stop();
		right_motor.stop();
		*/
		
/*
		left_motor.setSpeed(100);
		right_motor.setSpeed(100);

		
		right_motor.rotateTo(-100, true);
		left_motor.rotateTo(-100);
		
		left_motor.rotateTo(60);
		right_motor.rotateTo(60);
		
		left_motor.resetTachoCount();
		right_motor.resetTachoCount();
		
		right_motor.rotateTo(150, true);
		left_motor.rotateTo(-150);*/
		
	
	
	
}