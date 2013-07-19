package edu.wpi.always.user.people;

import java.util.regex.Pattern;

public class ValidPhoneAgeBirthday {

   static final String regex = "\\d{3}-\\d{3}-\\d{4}";

   public static boolean isPhoneNumberValid(String phoneNumber) {
      return Pattern.matches(regex, phoneNumber);
   }

   public static boolean isInteger(String s) {
      boolean result = true;
      try { 
         int age = Integer.parseInt(s); 
         if(0 > age || age > 110){
            result = false;
         }
      } catch(NumberFormatException e) { 
         result = false; 
      }
      return result;
   }

   public static boolean isValidDayOfMonth(int month, int day){
      boolean result = true;
      if(1 > day || day > 31){
         result = false;
      }
      else{
         if(month == 2){
            if(day > 29) {
               result = false;
            }
         }
         else if(month == 4 || month == 6 || month == 9 || month == 11) {
            if(day == 31){
               result = false;
            }
         }
         else{
            result = true;
         }
      }
      return result;
   }
}
