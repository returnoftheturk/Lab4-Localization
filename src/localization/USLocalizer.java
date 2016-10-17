package localization;

import lejos.hardware.Sound;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class USLocalizer {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static int ROTATION_SPEED = 30;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;


	private Odometer odo;
	private Navigation nav;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private final int d = 40;
	private final int k = 5;
	private int wallDistance;
	private final int TOLERANCE = 10;
	
	public USLocalizer(Odometer odo, Navigation nav, SampleProvider usSensor, float[] usData, LocalizationType locType,
			EV3LargeRegulatedMotor rightMotor, EV3LargeRegulatedMotor leftMotor) {
		this.odo = odo;
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
		this.rightMotor = rightMotor;
		this.leftMotor = leftMotor;
		
		this.leftMotor.setAcceleration(nav.ACCELERATION);
		this.rightMotor.setAcceleration(nav.ACCELERATION);
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {

			while (getFilteredData()<d+k){
				nav.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
			}
			nav.stopMotors();
			
			while(getFilteredData()>d-k - TOLERANCE){
				nav.setSpeeds(ROTATION_SPEED, -ROTATION_SPEED);
				
			}
			nav.stopMotors();
			angleA = this.odo.getAng();
			while(getFilteredData()<d+k){
				nav.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);	
			}
			nav.stopMotors();
			while(getFilteredData()>d-k){
				nav.setSpeeds(-ROTATION_SPEED, ROTATION_SPEED);
			}
			nav.stopMotors();
			angleB = this.odo.getAng();
						
			
			// rotate the robot until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// switch direction and wait until it sees no wall
			
			// keep rotating until the robot sees a wall, then latch the angle
			
			// angleA is clockwise from angleB, so assume the average of the
			// angles to the right of angleB is 45 degrees past 'north'
			
			// update the odometer position (example to follow:)
			odo.setPosition(new double [] {0.0, 0.0, 0.0}, new boolean [] {true, true, true});
		} else {
			/*
			 * The robot should turn until it sees the wall, then look for the
			 * "rising edges:" the points where it no longer sees the wall.
			 * This is very similar to the FALLING_EDGE routine, but the robot
			 * will face toward the wall for most of it.
			 */
			
			//
			// FILL THIS IN
			//
		}
	}
	
	private float getFilteredData() {
		usSensor.fetchSample(usData, 0);
		float distance = usData[0]*100;
		if (distance>255)
			distance = 255;
				
		return distance;
	}
	
	public void turnTo1(double angle, boolean stop, int distance) {

		double error = angle - this.odo.getAng();

		while (Math.abs(error) > nav.DEG_ERR && getFilteredData()>distance) {

			error = angle - this.odo.getAng();
			
			nav.setSpeeds(-nav.SLOW, nav.SLOW);
			
		}

		if (stop) {
			nav.setSpeeds(0, 0);
		}
	}


}
