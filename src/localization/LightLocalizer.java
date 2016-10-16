package localization;

import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.robotics.SampleProvider;

public class LightLocalizer {
	private Odometer odo;
	private Navigation nav;
	private SampleProvider colorSensor;
	private float[] colorData;
	private EV3LargeRegulatedMotor leftMotor, rightMotor;

	public LightLocalizer(Odometer odo, Navigation nav, SampleProvider colorSensor, float[] colorData,
			EV3LargeRegulatedMotor rightMotor, EV3LargeRegulatedMotor leftMotor) {
		this.odo = odo;
		this.colorSensor = colorSensor;
		this.colorData = colorData;
		this.nav = nav;
		this.leftMotor = leftMotor;
		this.rightMotor = rightMotor;
		
		this.leftMotor.setAcceleration(nav.ACCELERATION);
		this.rightMotor.setAcceleration(nav.ACCELERATION);
	}

	public void doLocalization() {
		// drive to location listed in tutorial
		// start rotating and clock all 4 gridlines
		// do trig to compute (0,0) and 0 degrees
		// when done travel to (0,0) and turn to 0 degrees
	}

}
