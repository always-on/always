package edu.wpi.always.cm.perceptors.fake;

import java.awt.*;
import javax.swing.*;
import edu.wpi.always.*;
import edu.wpi.always.client.ClientRegistry;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.schemas.StartupSchemas;
import edu.wpi.cetask.Utils;

public class FakeEngagementGUI {

   private static FacePerception facePerception;

   public static void main (String[] args) {
      Always always = Always.make(args, null, null);
      // adapted from Always.start()
      always.getContainer().start(); 
      CollaborationManager cm = always.getCM();
      cm.addRegistry(new ClientRegistry());
      cm.addRegistry(new StartupSchemas(false)); // false = do not start SessionSchema yet
      try { // preload GreetingsPlugin for SessionSchema
         cm.start((Class<? extends Plugin>) Class.forName("edu.wpi.always.greetings.GreetingsPlugin"), null);
      } catch (ClassNotFoundException e) { Utils.rethrow(e); }
      JFrame frame = new JFrame("Face Location");
      frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
      JLabel locationLabel = new JLabel();
      locationLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      frame.add(locationLabel);
      JLabel sizeLabel = new JLabel();
      sizeLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      frame.add(sizeLabel);
      JPanel panel = new JPanel() {

         private static final long serialVersionUID = 6254014952377622108L;

         @Override
         public void paintComponent (Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, 320, 240);
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, 320, 240);
            if ( facePerception != null )
               g.fillRect(facePerception.getLeft() ,
                     facePerception.getTop(), facePerception.getRight()
                        - facePerception.getLeft(), facePerception.getBottom()
                        - facePerception.getTop());
         }
      };
      frame.add(panel);
      JRadioButton near = new JRadioButton("Near");
      JRadioButton far = new JRadioButton("Far");
      JRadioButton none = new JRadioButton("None");
      cm.getContainer().addComponent(new FakeFacePerceptor(near, far, none));
      ButtonGroup group = new ButtonGroup();
      group.add(near);
      group.add(far);
      group.add(none);
      JPanel radioPanel = new JPanel(new GridLayout(0, 1));
      radioPanel.add(near);
      radioPanel.add(far);
      radioPanel.add(none);
      frame.add(radioPanel, BorderLayout.LINE_START);
      JCheckBox motion = new JCheckBox("Motion");
      cm.getContainer().addComponent(new FakeMovementPerceptor(motion));
      frame.add(motion);
      JLabel stateLabel = new JLabel();
      stateLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      frame.add(stateLabel);
      frame.pack();
      frame.setSize(500, 500);
      frame.setVisible(true);
      frame.setAlwaysOnTop(true);
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      FacePerceptor facePerceptor = always.getContainer()
            .getComponent(CollaborationManager.class).getContainer()
            .getComponent(FacePerceptor.class);
      EngagementPerceptor engagementPerceptor = always.getContainer()
            .getComponent(CollaborationManager.class).getContainer()
            .getComponent(EngagementPerceptor.class);
      while (true) {
         try {
            Thread.sleep(500);
         } catch (InterruptedException e) {
         }
         facePerception = facePerceptor.getLatest();
         if ( facePerception != null ) {
            locationLabel.setText("Location: " + facePerception.getPoint());
            sizeLabel.setText("Size: "
               + (facePerception.getRight() - facePerception.getLeft()) + " x "
               + (facePerception.getBottom() - facePerception.getTop())
               + (facePerception.isNear() ? " is near" : " is far"));
         }
         EngagementPerception engagementPerception = engagementPerceptor
               .getLatest();
         if ( engagementPerception != null ) {
            stateLabel.setText("State: " + engagementPerception.getState());
         }
         panel.repaint();
      }
   }

}
