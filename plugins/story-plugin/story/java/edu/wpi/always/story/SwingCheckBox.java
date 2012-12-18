package edu.wpi.always.story;

import java.awt.*;
import java.awt.event.*;

import javax.swing.*;

public class SwingCheckBox extends JPanel implements ItemListener {
	private static final long serialVersionUID = 6134347517304387577L;
	JCheckBox nodButton;
	JCheckBox eyebrowsUpButton;
	JCheckBox concern;
	JCheckBox blink;
//	JCheckBox stopBackchannel;

	public static boolean choices[];
	JLabel pictureLabel;

	public SwingCheckBox() {
		super(new BorderLayout());

		nodButton = new JCheckBox("Nod");
		nodButton.setMnemonic(KeyEvent.VK_C);
		nodButton.setSelected(true);

		eyebrowsUpButton = new JCheckBox("EyebrowsUp");
		eyebrowsUpButton.setMnemonic(KeyEvent.VK_G);
		eyebrowsUpButton.setSelected(false);

		concern = new JCheckBox("Concern");
		concern.setMnemonic(KeyEvent.VK_H);
		concern.setSelected(false);

		blink = new JCheckBox("Blink");
		blink.setMnemonic(KeyEvent.VK_T);
		blink.setSelected(false);

//		stopBackchannel = new JCheckBox("Stop backchanneling");
//		stopBackchannel.setMnemonic(KeyEvent.VK_T);
//		stopBackchannel.setSelected(false);

		nodButton.addItemListener(this);
		eyebrowsUpButton.addItemListener(this);
		concern.addItemListener(this);
		blink.addItemListener(this);
//		stopBackchannel.addItemListener(this);
		
		choices = new boolean[4]; 
		choices[0] = true;
		choices[1] = false;
		choices[2] = false;
		choices[3] = false;
//		choices[4] = false;
		
		getSelection();

		JPanel checkPanel = new JPanel(new GridLayout(0, 1));
		checkPanel.add(nodButton);
		checkPanel.add(eyebrowsUpButton);
		checkPanel.add(concern);
		checkPanel.add(blink);
//		checkPanel.add(stopBackchannel);

		add(checkPanel, BorderLayout.LINE_START);
		setBorder(BorderFactory.createEmptyBorder(20,20,20,20));
	}

	@Override
   public void itemStateChanged(ItemEvent e) {

		Object source = e.getItemSelectable();
		if (e.getStateChange() == ItemEvent.SELECTED) {
			if (source == nodButton) {
				choices[0] = true;
			} else if (source == eyebrowsUpButton) {
				choices[1] = true;
			} else if (source == concern) {
				choices[2] = true;
			} else if (source == blink) {
				choices[3] = true;
			}
//			if(source == stopBackchannel)
//				choices[4] = true;
		}
		
		if (e.getStateChange() == ItemEvent.DESELECTED) {
			if (source == nodButton) {
				choices[0] = false;
			} else if (source == eyebrowsUpButton) {
				choices[1] = false;
			} else if (source == concern) {
				choices[2] = false;
			} else if (source == blink) {
				choices[3] = false;
			}
//			if(source == stopBackchannel)
//				choices[4] = false;
		}
		getSelection();
	}

	public void getSelection() {
		System.out.println("\nselection received\n");
		System.out.print(">>>>>>>>");
		for(int i = 0; i <4; i++)
			System.out.print("\t"+choices[i]);
		System.out.print("\n");
	}

	public  void createAndShowGUI() {
		JFrame.setDefaultLookAndFeelDecorated(true);
		JFrame frame = new JFrame("Choose Back-Channel Behavior");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JComponent newContentPane = new SwingCheckBox();
		newContentPane.setOpaque(true);
		frame.setContentPane(newContentPane);
		frame.pack();
		frame.setVisible(true);
	}


}