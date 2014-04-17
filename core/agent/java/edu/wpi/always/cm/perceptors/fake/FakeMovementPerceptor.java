package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.perceptor.PerceptorBase;
import org.joda.time.DateTime;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

public class FakeMovementPerceptor extends PerceptorBase<MovementPerception>
             implements MovementPerceptor, ItemListener {

   private final JTextField txtX;
   private final JTextField txtY;

   public FakeMovementPerceptor (JTextField txtX, JTextField txtY) {
      this.txtX = txtX;
      this.txtY = txtY;
   }

   public FakeMovementPerceptor (JCheckBox box) {
      this(null, null);
      box.addItemListener(this);
   }
   
   private boolean motion;
   
   @Override
   public void itemStateChanged(ItemEvent e) {
      if (e.getStateChange() == ItemEvent.DESELECTED) motion = false;
      else motion = true;
   }
   
   @Override
   public void run () {
      if ( txtX == null )
         latest = new MovementPerception(DateTime.now(), motion, null);
      else {
         Point p = tryParsePoint();
         if ( p == null )
            latest = null;
         else
            latest = new MovementPerception(DateTime.now(), true, p);
      }
   }

   private Point tryParsePoint () {
      return tryParsePoint(txtX.getText(), txtY.getText());
   }

   public static Point tryParsePoint (String xText, String yText) {
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
}
