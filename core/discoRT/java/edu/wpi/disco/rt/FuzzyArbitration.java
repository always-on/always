package edu.wpi.disco.rt;

import edu.wpi.disco.rt.behavior.BehaviorMetadata;
import net.sourceforge.jFuzzyLogic.*;
import net.sourceforge.jFuzzyLogic.rule.*;
import org.antlr.runtime.RecognitionException;
import java.lang.reflect.Field;

public class FuzzyArbitration {

   private final String fclDefinition;
   private final BehaviorMetadata focus;
   private final MetadataToFuzzyVariableTranslator variableTranslate = new MetadataToFuzzyVariableTranslator();
   private boolean debug = false;

   public FuzzyArbitration (String fclDefinition, BehaviorMetadata focus) {
      if ( focus == null )
         throw new IllegalArgumentException("focus");
      this.fclDefinition = fclDefinition;
      this.focus = focus;
   }

   public double shouldSwitch (BehaviorMetadata other) {
      FIS fis;
      try {
         fis = FIS.createFromString(fclDefinition, true);
      } catch (RecognitionException e) {
         e.printStackTrace();
         return 0;
      }
      for (Field fld : BehaviorMetadata.class.getDeclaredFields()) {
         transferFieldValue(fis, other, fld, "o_");
         transferFieldValue(fis, focus, fld, "f_");
      }
      fis.evaluate();
      if ( isDebug() ) {
         printDiagnosticsInfo(fis);
      }
      return fis.getVariable("switch").getLatestDefuzzifiedValue();
   }

   private void printDiagnosticsInfo (FIS fis) {
      fis.chart();
      for (FunctionBlock fb : fis) {
         System.out.println("Function Block: " + fb.getName());
         for (RuleBlock rb : fb) {
            System.out.println("  Rule Block: " + rb.getName());
            for (Rule r : rb.getRules()) {
               System.out.println("    Rule: " + r.getName() + ", support:"
                  + r.getDegreeOfSupport());
            }
         }
      }
      fis.getVariable("switch").chartDefuzzifier(true);
   }

   private void transferFieldValue (FIS fis, BehaviorMetadata other, Field fld,
         String prefix) {
      String translated = variableTranslate.translate(fld.getName());
      String varName = prefix + translated;
      fld.setAccessible(true);
      try {
         fis.setVariable(varName, fld.getDouble(other));
      } catch (IllegalArgumentException e) {
         throw new FuzzyException("problem setting " + fld.getName()
            + " as fuzzy variable " + varName, e);
      } catch (IllegalAccessException e) {
         throw new FuzzyException("problem setting " + fld.getName()
            + " as fuzzy variable " + varName, e);
      }
   }

   public void setDebug (boolean debug) {
      this.debug = debug;
   }

   public boolean isDebug () {
      return debug;
   }
}
