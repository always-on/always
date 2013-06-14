package edu.wpi.always.client;

import java.io.IOException;

public interface Keyboard {

   void showKeyboard (String prompt);
   
   void hideKeyboard ();
   
   String getInputSoFar ();
}
