package edu.wpi.always.cm.perceptors.fake;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import edu.wpi.always.Always;
import edu.wpi.always.cm.CollaborationManager;
import edu.wpi.always.cm.perceptors.EngagementPerception;
import edu.wpi.always.cm.perceptors.EngagementPerceptor;
import edu.wpi.always.cm.perceptors.FacePerception;
import edu.wpi.always.cm.perceptors.FacePerceptor;
import edu.wpi.always.user.UserModel;

public class FakeEngagementGUI {

   private static FacePerception facePerception;

   public static void main (String[] args) {
      Always always = Always.make(null, null, null);
      always.start();
      always.getContainer().getComponent(UserModel.class).load();
      JFrame frame = new JFrame("Face Location");
      frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
      JLabel locationLabel = new JLabel();
      locationLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      frame.add(locationLabel);
      JLabel sizeLabel = new JLabel();
      sizeLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
      frame.add(sizeLabel);
      JPanel panel = new JPanel() {

         /**
			 * 
			 */
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
            Thread.sleep(200);
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
