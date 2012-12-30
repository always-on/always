package edu.wpi.always.cm.perceptors.fake;

import edu.wpi.always.cm.perceptors.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public class FakeGUI extends JFrame {

   private static final long serialVersionUID = 4262187476237471244L;
   private JPanel contentPane;
   private JTextField txtMoveX;
   private JTextField txtMoveY;
   private JTextField txtFaceX;
   private JTextField txtFaceY;
   private JTextField txtAquariumTrip;

   /**
    * Create the frame.
    */
   public FakeGUI () {
      setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
      setBounds(100, 100, 450, 300);
      contentPane = new JPanel();
      contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
      setContentPane(contentPane);
      contentPane.setLayout(null);
      JLabel lblMovement = new JLabel("Movement:");
      lblMovement.setBounds(34, 55, 83, 14);
      contentPane.add(lblMovement);
      txtMoveX = new JTextField();
      txtMoveX.setBounds(98, 52, 86, 20);
      contentPane.add(txtMoveX);
      txtMoveX.setColumns(10);
      txtMoveY = new JTextField();
      txtMoveY.setBounds(190, 52, 86, 20);
      contentPane.add(txtMoveY);
      txtMoveY.setColumns(10);
      JLabel lblFace = new JLabel("Face:");
      lblFace.setBounds(34, 131, 46, 14);
      contentPane.add(lblFace);
      txtFaceX = new JTextField();
      txtFaceX.setBounds(98, 128, 86, 20);
      contentPane.add(txtFaceX);
      txtFaceX.setColumns(10);
      txtFaceY = new JTextField();
      txtFaceY.setBounds(190, 128, 86, 20);
      contentPane.add(txtFaceY);
      txtFaceY.setColumns(10);
      txtFaceX.setText("3");
      txtFaceY.setText("3");
      JLabel lblAquariumTripAt = new JLabel("Aquarium Trip At:");
      lblAquariumTripAt.setBounds(34, 177, 94, 14);
      contentPane.add(lblAquariumTripAt);
      txtAquariumTrip = new JTextField();
      txtAquariumTrip.setBounds(138, 174, 86, 20);
      contentPane.add(txtAquariumTrip);
      txtAquariumTrip.setColumns(10);
   }

   public MovementPerceptor createMovementPerceptor () {
      return new FakeMovementPerceptor(txtMoveX, txtMoveY);
   }

   public FacePerceptor createFacePerceptor () {
      return new FakeFacePerceptor(txtFaceX, txtFaceY);
   }

   public void setTxtAquariumTrip (JTextField txtAquariumTrip) {
      this.txtAquariumTrip = txtAquariumTrip;
   }

   public JTextField getTxtAquariumTrip () {
      return txtAquariumTrip;
   }
}
