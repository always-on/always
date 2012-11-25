package edu.wpi.always;


import java.awt.*;

import javax.swing.*;

import org.picocontainer.*;

import edu.wpi.always.cm.*;
import edu.wpi.always.cm.engagement.*;
import edu.wpi.always.cm.perceptors.*;
import edu.wpi.always.cm.perceptors.physical.*;
import edu.wpi.always.cm.ragclient.*;
import edu.wpi.always.*;
import edu.wpi.always.user.*;
import edu.wpi.always.user.owl.*;

public class EngagementProgram {


	public static void main(String[] args) {
		final ProgramBootstrapper program = new ProgramBootstrapper(false);
		
		program.addRegistry(new PicoRegistry(){
			@Override
			public void register(MutablePicoContainer container) {
				container.as(Characteristics.CACHE).addComponent(IRelationshipManager.class, FakeRelationshipManager.class);
				container.as(Characteristics.CACHE).addComponent(ICollaborationManager.class, edu.wpi.always.cm.Bootstrapper.class);
			}
		});
		program.addRegistry(new OntologyUserRegistry("Test User"));

		program.addCMRegistry(new PhysicalPerceptorsRegistry());
		program.addCMRegistry(new EngagementRegistry());
		program.addCMRegistry(new RagClientRegistry());
		
		program.start();
		

		program.getContainer().getComponent(UserModel.class).load();
		
		JFrame frame = new JFrame("Face Location");

		frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
		JLabel locationLabel = new JLabel();
		locationLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		frame.add(locationLabel);
		JLabel sizeLabel = new JLabel();
		sizeLabel.setFont(new Font(Font.DIALOG, Font.PLAIN, 20));
		frame.add(sizeLabel);
		JPanel panel = new JPanel(){
			/**
			 * 
			 */
			private static final long serialVersionUID = 6254014952377622108L;

			public void paintComponent(Graphics g){
				super.paintComponent(g);
				g.setColor(Color.WHITE);
				g.fillRect(0, 0, 320, 240);
				g.setColor(Color.BLACK);
				g.drawRect(0, 0, 320, 240);
				if(facePerception!=null)
					g.fillRect(facePerception.getLeft()+160, facePerception.getTop()+120,
							facePerception.getRight()-facePerception.getLeft(),
							facePerception.getBottom()-facePerception.getTop());
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
		
		EmotiveFacePerceptor facePerceptor = program.getContainer().getComponent(Bootstrapper.class).getContainer().getComponent(EmotiveFacePerceptor.class);
		GeneralEngagementPerceptor engagementPerceptor = program.getContainer().getComponent(Bootstrapper.class).getContainer().getComponent(GeneralEngagementPerceptor.class);
		while(true){
			try {Thread.sleep(200);} catch (InterruptedException e) {}
			facePerception = facePerceptor.getLatest();
			if(facePerception!=null){
				locationLabel.setText("Location: "+facePerception.getLocation());
				sizeLabel.setText("Size: "+(facePerception.getRight()-facePerception.getLeft())+" x "+(facePerception.getBottom()-facePerception.getTop())+(facePerception.isNear()?" is near":" is far"));
			}
			GeneralEngagementPerception engagementPerception = engagementPerceptor.getLatest();
			if(engagementPerception!=null){
				stateLabel.setText("State: "+engagementPerception.getState());
			}
			panel.repaint();
		}
	}
	private static EmotiveFacePerception facePerception;
}
