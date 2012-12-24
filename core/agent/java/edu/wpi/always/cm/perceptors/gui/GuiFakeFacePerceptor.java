package edu.wpi.always.cm.perceptors.gui;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;
import javax.swing.JTextField;

public class GuiFakeFacePerceptor implements FacePerceptor {

   private final JTextField txtX;
   private final JTextField txtY;
   private volatile FacePerception latest;

   public GuiFakeFacePerceptor (JTextField txtX, JTextField txtY) {
      this.txtX = txtX;
      this.txtY = txtY;
   }

   @Override
   public void run () {
      Point p = tryParsePoint();
      if ( p == null )
         latest = null;
      else
         latest = new FacePerceptionImpl(DateTime.now(), p);
   }

   private Point tryParsePoint () {
      return GuiFakeMovementPerceptor.tryParsePoint(txtX.getText(),
            txtY.getText());
   }

   @Override
   public FacePerception getLatest () {
      return latest;
   }
}
