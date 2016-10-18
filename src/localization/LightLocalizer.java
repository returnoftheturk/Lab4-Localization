package localization;

import lejos.hardware.Sound;
import lejos.hardware.Sounds;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.port.Port;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.SensorModes;
import lejos.robotics.SampleProvider;

public class LightLocalizer {

	final int FAST = 100, SLOW = 50, DISTANCE = 16;

	private Odometer odo;
	private Navigation nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	// private static double colorLevel;

	// set variables
	private boolean inPosition;
	private final double black = 0.2;
	private double wheelRadius;
	private final double sensorDistance = 13.0;
	private double[] angles;
	private int angleIndex;

	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public LightLocalizer(Odometer odo, Navigation nav, SampleProvider colorValue, float[] colorData) {
		this.odo = odo;
		this.nav = nav;
		this.colorSensor = colorValue;
		this.colorData = colorData;
		// get the motors
		EV3LargeRegulatedMotor[] motors = odo.getMotors();
		this.leftMotor = motors[0];
		this.rightMotor = motors[1];
		this.wheelRadius = odo.getLeftRadius();
		// initialize arrays
		angles = new double[4];
		angleIndex = 0;
		this.leftMotor.setAcceleration(200);
		this.rightMotor.setAcceleration(200);

	}

	public void doLocalization() {

		// colorLevel = getColorData();

		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println();
		System.out.println(getColorData());
		// assume the robot is not at the right position to measure data for
		// the lab
		// drive to location listed in tutorial
		inPosition = false;
		while (inPosition == false) {
			inPosition = getToRightPosition();
		}

		// now the robot is in position
		if (inPosition == true) {
			Sound.beepSequence();
			// rotate(counter clockwise) 360 degrees and read all 4 lines
			nav.turnTo(360, true);
			// leftMotor.setSpeed(SLOW);
			// rightMotor.setSpeed(SLOW);
			// leftMotor.backward();
			// rightMotor.forward();

			while (angleIndex < 4) {
				// for every black line, beep & record data
				if (getColorData() < black) {
					Sound.beepSequenceUp();
					angles[angleIndex] = odo.getAng();
					angleIndex++;
				}
			}

			// do trig to compute (0,0) and 0 degrees
			double thetaX = angles[2] - angles[0]; // angle sensed at y axis
			double thetaY = angles[3] - angles[1]; // angle sensed at x axis

			// compute x and y
			double x = (-1) * sensorDistance * Math.cos(Math.PI * thetaY / (2 * 180));
			double y = (-1) * sensorDistance * Math.cos(Math.PI * thetaX / (2 * 180));
			// double thetaYNeg = angles[0];
			// double deltaTheta = 270 + thetaY/2 - thetaYMinus;

			// set position based on the computation
			odo.setPosition(new double[] { x, y, 90 }, new boolean[] { true, true, true });
			// when done travel to (0,0) and turn to 90 degrees (initial position)
			nav.travelTo(0, 0);
			nav.turnTo(90, true);
		}

	}

	// drive the robot to a position where it can read 4 lines
	public boolean getToRightPosition() {
		// go forward until the robot reach a black line
		nav.setSpeeds(SLOW, SLOW);
		leftMotor.forward();
		rightMotor.forward();

		// reached black line, stop motors
		if (getColorData() < black) {
			Sound.beep();
			leftMotor.stop(true);
			rightMotor.stop(true);
		}

		// back up a bit
		leftMotor.rotate(-convertDistance(wheelRadius, DISTANCE), true);
		rightMotor.rotate(-convertDistance(wheelRadius, DISTANCE), false);

		// turn 90 degrees (facing x axis), go forward to find another black
		// line
		nav.turnTo(-90, true);
		nav.setSpeeds(SLOW, SLOW);
		leftMotor.forward();
		rightMotor.forward();

		// reached black line, stop motors
		if (getColorData() < black) {
			Sound.beep();
			leftMotor.stop(true);
			rightMotor.stop(true);
		}

		// back up a bit and turn 90 (facing y axis)
		leftMotor.rotate(-convertDistance(wheelRadius, DISTANCE), true);
		rightMotor.rotate(-convertDistance(wheelRadius, DISTANCE), false);
		nav.turnTo(90, true);

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

	private double getColorData() {
		colorSensor.fetchSample(colorData, 0);
		double colorLevel = colorData[0];
		return colorLevel;
	}

}
