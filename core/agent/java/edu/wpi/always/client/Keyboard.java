package edu.wpi.always.client;

import java.io.IOException;

public interface Keyboard {

   void showKeyboard (String prompt, boolean isNumeric);
   
   void hideKeyboard ();
   
   String getInputSoFar ();
}
