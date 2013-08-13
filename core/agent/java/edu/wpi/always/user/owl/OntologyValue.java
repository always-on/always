package edu.wpi.always.user.owl;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.MonthDay;
import org.joda.time.Period;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.joda.time.format.ISOPeriodFormat;
import org.joda.time.format.PeriodFormatter;
import org.semanticweb.owlapi.model.OWLLiteral;

public class OntologyValue {

   private final OWLLiteral literal;

   public OntologyValue (OWLLiteral literal) {
      this.literal = literal;
   }

   public OWLLiteral getOWLLiteral () {
      return literal;
   }

   public boolean isNull () {
      return literal == null;
   }

   public String asString () {
      if ( isNull() ) return null;
      return literal.getLiteral();
   }

   public double asDouble () {
      if ( isNull() ) return 0;
      return literal.parseDouble();
   }

   public boolean asBoolean () {
      if ( isNull() ) return false;
      return literal.parseBoolean();
   }

   public int asInteger () {
      if ( isNull() ) return 0;
      return literal.parseInteger();
   }

   public long asLong () {
      if ( isNull() ) return 0;
      return Long.parseLong(literal.getLiteral());
   }
   
   static final DateTimeFormatter XML_GMonthDay_FORMAT = DateTimeFormat
         .forPattern("--MM-dd");

   public MonthDay asMonthDay () {
      if ( isNull() ) return null;
      LocalDate date = XML_GMonthDay_FORMAT.parseLocalDate(asString());
      return new MonthDay(date.getMonthOfYear(), date.getDayOfMonth());
   }

   static final DateTimeFormatter XML_DATE_TIME_FORMAT = ISODateTimeFormat
         .dateTimeNoMillis();

   public DateTime asDateTime () {
      if ( isNull() ) return null;
      return XML_DATE_TIME_FORMAT.parseDateTime(asString());
   }

   static final DateTimeFormatter XML_DATE_FORMAT = DateTimeFormat
         .forPattern("yyyy-MM-dd");

   public LocalDate asDate () {
      if ( isNull() ) return null;
      return XML_DATE_FORMAT.parseLocalDate(asString());
   }

   static final DateTimeFormatter XML_TIME_FORMAT = DateTimeFormat
         .forPattern("HH:mm:ss");

   public LocalTime asTime () {
      if ( isNull() ) return null;
      return XML_TIME_FORMAT.parseLocalTime(asString());
   }

   static final PeriodFormatter XML_DURATION_FORMAT = ISOPeriodFormat
         .standard();

   public Period asDuration () {
      if ( isNull() ) return null;
      return XML_DURATION_FORMAT.parsePeriod(asString());
   }
}
