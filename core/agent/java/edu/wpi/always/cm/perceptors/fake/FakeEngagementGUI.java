package edu.wpi.always.cm.perceptors.fake;

import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import edu.wpi.always.Always;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.disco.rt.Registry;

public class FakeEngagementGUI extends JFrame {
   
   private final JRadioButton near, far, none;
   private final JCheckBox motion;
   
   public MovementPerceptor createMovementPerceptor () {
      return new FakeMovementPerceptor(motion);
   }

   public FacePerceptor createFacePerceptor () {
      return new FakeFacePerceptor(near, far, none);
   }

   public static void main (String[] args) {
      Always always = Always.make(args, null, null);
      List<Registry> cmRegistries = always.getCMRegistries();
      Iterator<Registry> i = cmRegistries.iterator();
      while (i.hasNext())
         if ( i.next() instanceof EngagementRegistry ) i.remove();
      cmRegistries.add(new FakeEngagementRegistry());
      always.start();
      EngagementPerceptor engagementPerceptor = 
            always.getCM().getContainer().getComponent(EngagementPerceptor.class);
      while (true) {
         try { Thread.sleep(500); }
         catch (InterruptedException e) {}
         EngagementPerception p = engagementPerceptor.getLatest();
         if ( p != null) THIS.setState(p.getState().toString());
      }
   }

   private final JLabel state;
   
   private void setState (String state) {
      this.state.setText(state);
      repaint();
   }
   
   private static FakeEngagementGUI THIS;
   
   public FakeEngagementGUI () {
      THIS = this;
      setTitle("Fake Engagment");
      setLayout(new BoxLayout(getContentPane(), BoxLayout.Y_AXIS));
      state = new JLabel();
      add(state);
      near = new JRadioButton("Near");
      far = new JRadioButton("Far");
      none = new JRadioButton("None", true);
      ButtonGroup group = new ButtonGroup();
      group.add(near);
      group.add(far);
      group.add(none);
      JPanel radioPanel = new JPanel(new GridLayout(0, 1));
      radioPanel.add(near);
      radioPanel.add(far);
      radioPanel.add(none);
      add(radioPanel, BorderLayout.LINE_START);
      motion = new JCheckBox("Motion");
      add(motion);
      pack();
      setSize(100, 200);
      setAlwaysOnTop(true);
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
   }

}
