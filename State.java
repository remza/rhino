import lejos.nxt.Motor;
import lejos.nxt.LightSensor;
import lejos.nxt.addon.EOPD;
import lejos.robotics.TachoMotor;
import lejos.robotics.navigation.Pilot;
import lejos.robotics.navigation.TachoPilot;
import lejos.util.Timer;
import lejos.util.TimerListener;

/**
 * << State is part of our maze solving robotics system Rhino >>
 *
 * @author Darren Paskell, Caroline Drinkwater and David Hollands
 * @version $0.1a$ 
 */
public class State {	
	private Timer timer;
	private int clock = 0;
	
	private TachoPilot navigator;
	private Pilot pilot;
	private TachoMotor left_motor, right_motor;
	
	private int left_motor_power, right_motor_power, left_motor_speed, right_motor_speed;
	
	private float tacho;
	private float angle;
	
	private EOPD left, right, centre;
	
	private int left_dist, centre_dist, right_dist;
	
	private boolean is_charging = true;
	private boolean is_turning = false; 
	
	// {{{ State constructor
	/**
	 * Set up timer, pilot and sensors.
	 */
	public State(TachoPilot my_navi, EOPD my_left, EOPD my_centre, EOPD my_right) {
		
		// start timer
		timer = new Timer(10, new Tick());
		timer.start();
		
		// get nav, pilot, motors
		navigator = my_navi;
		pilot = navigator;
		left_motor = ((TachoPilot) pilot).getLeft();
		right_motor = ((TachoPilot) pilot).getRight();
		
		// assign sensors
		left = my_left;
		centre = my_centre;
		right = my_right;
	}
	// }}}
	
	public int getTime() {
		return clock;
	}
	
	public float getTachoCount() {
		return tacho;		
	}

	public int getLeftReading() {
		return left_dist;
	}
	
	public int getCentreReading() {
		return centre_dist;
	}
	
	public int getRightReading() {
		return right_dist;
	}
	
	public boolean isTurning() {
		return is_turning;
	}
	
	public boolean isCharging() {
		return is_charging;
	}
	
	public float getAngle() {
		return angle;
	}
	
	public void setAngle(float error) {
		angle = error;
//		System.out.println("angle: " + angle);
	}
	
	public void charge() {
		is_charging = true;
		is_turning = false;
	}
	
	public void turn() {
		is_turning = true;
		is_charging = false;
	}
	
	public void readTacho() {
	 	tacho = pilot.getTravelDistance();
	}
	
	public void readEOPDs() {
		left_dist = left.readRawValue();
		right_dist = right.readRawValue();
		centre_dist = centre.readRawValue();
		
	}
	
	public void readSonic() {
//		System.out.println("c: " + centre_dist);
	}

	public void readMotors() {
		left_motor_power = ((Motor) left_motor).getPower();
		right_motor_power = ((Motor) right_motor).getPower();
		left_motor_speed = left_motor.getSpeed();
		right_motor_speed = right_motor.getSpeed();
	}
	
	/**
	 * << Tick is an inner class for use with the Timer of our maze solving robotics system Rhino >>
	 *
	 * @author Darren Paskell and David Hollands
	 * @version $0.1a$ 
	 */
	class Tick implements TimerListener {
		public Tick() {}
		public void timedOut() {
			clock++;
			readMotors();
			readTacho();
			readEOPDs();
			if(clock % 5 == 0) { // every 20ms
				//System.out.println(left_dist + ":" + centre_dist + ":" + right_dist);
			}
		}
	}
}


