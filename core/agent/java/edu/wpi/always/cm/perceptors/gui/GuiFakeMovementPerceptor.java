package edu.wpi.always.cm.perceptors.gui;

import java.awt.*;

import javax.swing.*;

import org.joda.time.*;

import edu.wpi.always.cm.perceptors.*;

public class GuiFakeMovementPerceptor implements MovementPerceptor {
	private final JTextField txtX;
	private final JTextField txtY;
	private volatile MovementPerception latest;

	public GuiFakeMovementPerceptor(JTextField txtX, JTextField txtY) {
		this.txtX = txtX;
		this.txtY = txtY;
	}

	@Override
	public void run() {
		Point p = tryParsePoint();
		if (p == null)
			latest = null;
		else
			latest = new MovementPerceptionImpl(DateTime.now(), p);
	}

	private Point tryParsePoint() {
		return tryParsePoint(txtX.getText(), txtY.getText());
	}

	public static Point tryParsePoint(String xText, String yText) {
		int x, y;
		try {
			x = Integer.parseInt(xText);
		} catch (NumberFormatException ex) {
			return null;
		}

		try {
			y = Integer.parseInt(yText);
		} catch (NumberFormatException ex) {
			return null;
		}

		return new Point(x, y);
	}

	@Override
	public MovementPerception getLatest() {
		return latest;
	}

}
