package localization;

import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {

	final int FAST = 100, SLOW = 50, DISTANCE = 16;

	private Odometer odo;
	private Navigation nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	private static double colorLevel;
	
	private boolean inPosition = false;
	private final double black = 0.2;
	private double wheelRadius;
	private final double sensorDistance = 13.0;
	
	private double[] angles;
	private int angleIndex;
	
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public LightLocalizer(Odometer odo, Navigation nav, SampleProvider colorSensor, float[] colorData) {
		this.odo = odo;
		this.nav = nav;
		this.colorSensor = colorSensor;
		this.colorData = colorData;	
		// get the motors
		EV3LargeRegulatedMotor[] motors = odo.getMotors();
		this.leftMotor = motors[0];		
		this.rightMotor = motors[1];
		this.wheelRadius = odo.getLeftRadius();
		// initialize arrays
		angles = new double[4];
		angleIndex = 0;
		this.leftMotor.setAcceleration(Navigation.ACCELERATION);
		this.rightMotor.setAcceleration(Navigation.ACCELERATION);
	}

	public void doLocalization() {
		// get color data and store in colorLevel
		colorSensor.fetchSample(colorData, 0);
		colorLevel = colorData[0];
		
		// assume the robot is not in right position to measure data for the lab
		// drive to location listed in tutorial
		while (this.inPosition == false) {
			getToRightPosition(colorLevel);
		}

		// start rotating  360 degrees and read all 4 lines
		leftMotor.setSpeed(SLOW);
		rightMotor.setSpeed(SLOW);
		leftMotor.backward();
		rightMotor.forward();

		while (angleIndex < 4) {
			// for every black line, beep & record data
			if (colorLevel < black) {
				angles[angleIndex] = odo.getAng();
				angleIndex++;
			}
		}

		// do trig to compute (0,0) and 0 degrees
		double thetaX = angles[2] - angles[0];
		double thetaY = angles[3] - angles[1];
		double x = (-1) * sensorDistance * Math.cos(Math.PI * thetaY / (2 * 180));
		double y = (-1) * sensorDistance * Math.cos(Math.PI * thetaX / (2 * 180));
		double thetaYNeg = angles[0];
		// double deltaTheta = 270 + thetaY/2 - thetaYMinus;

		// set position based on the computation
		odo.setPosition(new double[] { x, y, 0 }, new boolean[] { true, true, true });
		// when done travel to (0,0) and turn to 0 degrees
		nav.travelTo(0, 0);
		nav.turnTo(0, true);

	}

	// drive the robot to a position where it can read 4 lines
	public boolean getToRightPosition(double colorLevel) {
		// go forward until the robot reach a black line
		nav.setSpeeds(SLOW, SLOW);
		leftMotor.forward();
		rightMotor.forward();

		// reached black line, stop motors
		if (colorLevel < black) {
			Sound.beep();
			leftMotor.stop(true);
			rightMotor.stop(true);
		}

		// back up a bit
		leftMotor.rotate(-convertDistance(wheelRadius, DISTANCE), true);
		rightMotor.rotate(-convertDistance(wheelRadius, DISTANCE), false);

		// turn 90 degrees, go forward to find another black line
		nav.turnTo(90, true);
		nav.setSpeeds(SLOW, SLOW);
		leftMotor.forward();
		rightMotor.forward();

		// reached black line, stop motors
		if (colorLevel < black) {
			Sound.beep();
			leftMotor.stop(true);
			rightMotor.stop(true);
		}

		// back up a bit
		leftMotor.rotate(-convertDistance(wheelRadius, DISTANCE), true);
		rightMotor.rotate(-convertDistance(wheelRadius, DISTANCE), false);

		// in the right position, set boolean to true
		this.inPosition = true;
		
		return inPosition;

	}

	private static int convertAngle(double radius, double width, double angle) {
		return convertDistance(radius, Math.PI * width * angle / 360.0);
	}

	private static int convertDistance(double radius, double distance) {
		return (int) ((180.0 * distance) / (Math.PI * radius));
	}

	// return color reading in string
	public static String getColor() {
		String colorString = String.valueOf(colorLevel);
		return colorString;
	}

}
