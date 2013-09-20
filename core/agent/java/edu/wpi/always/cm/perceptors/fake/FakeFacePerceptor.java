package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.cm.perceptors.*;
import org.joda.time.DateTime;
import java.awt.Point;
import javax.swing.JTextField;

public class FakeFacePerceptor implements FacePerceptor {

   private final JTextField txtX;
   private final JTextField txtY;
   private volatile FacePerception latest;

   public FakeFacePerceptor (JTextField txtX, JTextField txtY) {
      this.txtX = txtX;
      this.txtY = txtY;
   }

   @Override
   public void run () {
      Point p = tryParsePoint();
      if ( p == null )
         latest = null;
      else
         latest = new FacePerception(DateTime.now(), p.x, p.x, p.y, p.y, 0, 0, 0);
   }

   private Point tryParsePoint () {
      return FakeMovementPerceptor.tryParsePoint(txtX.getText(),
            txtY.getText());
   }

   @Override
   public FacePerception getLatest () {
      return latest;
   }
}
