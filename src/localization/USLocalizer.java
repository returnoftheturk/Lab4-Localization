package localization;

import lejos.hardware.Sound;
import lejos.robotics.SampleProvider;

public class USLocalizer implements UltrasonicController {
	public enum LocalizationType { FALLING_EDGE, RISING_EDGE };
	public static double ROTATION_SPEED = 30;

	private Odometer odo;
	private Navigation nav;
	private SampleProvider usSensor;
	private float[] usData;
	private LocalizationType locType;
	private final int d = 40;
	private final int k = 5;
	private int wallDistance;
	
	public USLocalizer(Odometer odo, Navigation nav, SampleProvider usSensor, float[] usData, LocalizationType locType) {
		this.odo = odo;
		this.nav = nav;
		this.usSensor = usSensor;
		this.usData = usData;
		this.locType = locType;
	}
	
	public void doLocalization() {
		double [] pos = new double [3];
		double angleA, angleB;
		
		if (locType == LocalizationType.FALLING_EDGE) {
			//nav.turnTo1(360, true);
			
			nav.turnTo1(odo.getAng() - 4, true);
			
			if (this.readUSDistance()>100){
				Sound.buzz();
			}
			
			nav.stopMotors();
			
			
			
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
		float distance = usData[0];
				
		return distance;
	}

	@Override
	public void processUSData(int distance) {
		 this.wallDistance = distance;
		
	}

	@Override
	public int readUSDistance() {
		// TODO Auto-generated method stub
		return wallDistance;
	}

}
