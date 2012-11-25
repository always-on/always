package edu.wpi.always.cm.disco;

import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.concurrent.*;

import javax.swing.*;

import edu.wpi.cetask.*;
import edu.wpi.cetask.Shell.Reader;

public class DiscoConsolePanel extends JPanel implements ActionListener{
	private static final long serialVersionUID = 8348744575416613331L;
	private final JTextArea outputPanel;
	private final JTextField inputField;
	private final OutputStream outputStream;
	private final BlockingQueue<String> inputQueue;
	
	public DiscoConsolePanel(){
		setLayout(new BorderLayout());
		add(new JScrollPane(outputPanel = new JTextArea()));
		outputPanel.setEditable(false);
		add(inputField = new JTextField(), BorderLayout.SOUTH);
		inputField.addActionListener(this);
		inputField.requestFocusInWindow();
		outputStream = new OutputStream(){
			@Override
			public void write(int b) throws IOException {
				if(b>0){
					appendOutput(Character.toString((char)b));
				}
			}
			@Override
			public void write(byte[] b, int off, int len) {
				appendOutput(new String(b, off, len));
			}
		};
		inputQueue = new LinkedBlockingQueue<String>();
	}
	public void appendOutput(String text){
		outputPanel.append(text);
		outputPanel.setCaretPosition(outputPanel.getText().length());
	}
	private class InputReader implements Reader{

		@Override
		public String readLine() throws IOException {
			String input = null;
			while(input==null){
				try {
					input = inputQueue.take();
				} catch (InterruptedException e) {
				}
			}
			return input;
		}
		
	}

	public PrintStream getOutput(){
		return new PrintStream(outputStream);
	}
	public Shell.Reader getInput(){
		return new InputReader();
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		appendOutput("> "+inputField.getText()+"\n");
		inputQueue.offer(inputField.getText());
		inputField.setText("");
	}
}
