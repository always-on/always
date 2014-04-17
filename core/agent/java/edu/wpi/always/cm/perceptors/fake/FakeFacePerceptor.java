package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.perceptor.PerceptorBase;
import org.joda.time.DateTime;
import java.awt.Point;
import java.awt.event.*;
import javax.swing.*;

public class FakeFacePerceptor extends PerceptorBase<FacePerception>
             implements FacePerceptor, ActionListener {

   private final JTextField txtX;
   private final JTextField txtY;

   public FakeFacePerceptor (JTextField txtX, JTextField txtY) {
      this.txtX = txtX;
      this.txtY = txtY;
   }

   public FakeFacePerceptor (JRadioButton near, JRadioButton far, JRadioButton none) {
      this(null, null);
      near.setActionCommand("near");
      far.setActionCommand("far");
      none.setActionCommand("none");
      near.addActionListener(this);
      far.addActionListener(this);
      none.addActionListener(this);
   }
   
   private int area;
   
   @Override
   public void actionPerformed (ActionEvent e) {
      switch (e.getActionCommand()) {
         case "near":
            area = FacePerception.FACE_NEAR_AREA_THRESHOLD+1;
            break;
         case "far":
            area = FacePerception.FACE_NEAR_AREA_THRESHOLD-1;
            break;
         case "none":
            area = 0;
            break;
      }
  }
   
   @Override
   public void run () {
      if ( txtX == null) {
         latest = new FacePerception(DateTime.now(), 0, 0, area-1, 0, area, 0, 0);
      } else {
         Point p = tryParsePoint();
         if ( p == null )
            latest = null;
         else
            latest = new FacePerception(DateTime.now(), p.x, p.x, p.y, p.y, 0, 0, 0);
      }
   }

   private Point tryParsePoint () {
      return FakeMovementPerceptor.tryParsePoint(txtX.getText(), txtY.getText());
   }
}
