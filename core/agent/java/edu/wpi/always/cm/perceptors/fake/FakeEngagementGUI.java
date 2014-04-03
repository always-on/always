package edu.wpi.always.cm.perceptors.fake;

import java.awt.*;
import javax.swing.*;
import edu.wpi.always.*;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.disco.rt.util.Utils;

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
      Always always = Always.make(null, null, null);
      // adapted from Always.start()
      always.getContainer().start(); 
      Utils.lnprint(System.out, "Always running...");
      CollaborationManager cm = always.getCM();
      cm.addRegistry(new ClientRegistry());
      cm.addRegistry(new FakeEngagementRegistry());
      cm.addRegistry(new StartupSchemas(false)); // false = do not start SessionSchema yet
      try { // preload GreetingsPlugin for SessionSchema
         cm.start((Class<? extends Plugin>) Class.forName("edu.wpi.always.greetings.GreetingsPlugin"), null);
      } catch (ClassNotFoundException e) {
         edu.wpi.cetask.Utils.rethrow(e); }
      EngagementPerceptor engagementPerceptor = cm.getContainer().getComponent(EngagementPerceptor.class);
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
