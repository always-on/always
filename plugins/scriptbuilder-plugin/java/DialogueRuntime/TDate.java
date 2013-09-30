
//Readable string date representation (mm/dd/y)
//Compute days between two dates
//other stuff
package DialogueRuntime;

import java.text.SimpleDateFormat;
import java.util.*;

public class TDate {
    private Date idate;
    private Calendar myCal = Calendar.getInstance();
    
    public  final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss";
    SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
    public static String DAYS_OF_WEEK[]={"SUNDAY","MONDAY","TUESDAY","WEDNESDAY","THURSDAY","FRIDAY","SATURDAY"};  
    
    public TDate(long t) { idate=new Date(t); }
    public TDate() { idate=new Date(); }
    public TDate(Date in) { idate= (Date)in.clone(); }
    public TDate(String s) { 
	//allow for DB strings of form yyyy-mm-dd
	if(s.indexOf('-')>0) 
	    s=s.substring(5,7)+"/"+s.substring(8,10)+"/"+s.substring(0,4);
	idate=new Date(s); 
	
    }
    
    public TDate(Calendar c) { idate=c.getTime(); }
    public String toString() { return ""+(idate.getMonth()+1)+"/"+idate.getDate()+"/0"+(idate.getYear()-100); }
    public String toString2() { return ""+(idate.getMonth()+1)+"_"+idate.getDate()+"_"+(idate.getYear()-100); }
    public String toDBString() { return ""+(1900+idate.getYear())+"-"+zeropad(idate.getMonth()+1,2)+"-"+zeropad(idate.getDate(),2); }
    public String getDay() { return ""+ DAYS_OF_WEEK[myCal.get(Calendar.DAY_OF_WEEK)-1]; }

    public static boolean isDefaultDBTime(String s) {
	return s.indexOf('-')>0 && s.charAt(0)=='0';
    }

    public static String zeropad(int x,int cols) {
	String res=""+x;
	while(res.length()<cols)
	    res="0"+res;
	return res;
    }

    public static int daysBetween(TDate from,TDate to) {
	GregorianCalendar fromGC=new GregorianCalendar(from.getYear(),from.getMonth(),from.getDate());
	GregorianCalendar toGC=new GregorianCalendar(to.getYear(),to.getMonth(),to.getDate());
	
	int days=0;
	while(toGC.after(fromGC)) {
	    fromGC.add(fromGC.DAY_OF_MONTH,1);
	    days++;
	};
	return days;
    }
    
    public static TDate daysAgo(TDate start,int offsetDays) {
	long starttime= start.idate.getTime();
	return new TDate(starttime-(long)offsetDays*24*60*60*1000);
    }
    
    public void addDays(int days) {
	idate=new Date(idate.getTime()+days*1000*60*60*24);
    }
    
    public int getYear() { return idate.getYear(); }
    public int getMonth() { return idate.getMonth(); }
    public int getDate() { return idate.getDate(); }
    public int getHour() { return idate.getHours(); }

    public static void main(String[] args) {
	/* TDate now=new TDate();

	String snow=""+now;
	System.out.println(now);

	TDate then=new TDate(snow);
	System.out.println(then);

	TDate later=new TDate("6/15/04");
	System.out.println("time to "+later+" = "+daysBetween(now,later)); */

	/*
	Calendar c=Calendar.getInstance();
	TDate td=new TDate(c);
	System.out.println("td="+td); */

	/*
	System.out.println("hrs = "+new TDate().getHour());

	System.out.println("zero days ago="+daysAgo(new TDate(),0));
	System.out.println("one days ago="+daysAgo(new TDate(),1));
	System.out.println("two days ago="+daysAgo(new TDate(),2));
	System.out.println("18 days ago="+daysAgo(new TDate(),18));
	System.out.println("19 days ago="+daysAgo(new TDate(),19));
	System.out.println("20 days ago="+daysAgo(new TDate(),20));
	System.out.println("25 days ago="+daysAgo(new TDate(),25));
	*/

	/*
	TDate now=new TDate(); //now
	System.out.println("now="+now);
	now.addDays(1);	System.out.println("+1d="+now);
	now.addDays(1);	System.out.println("+1d="+now);
	now.addDays(2);	System.out.println("+1d="+now);
	now.addDays(1);	System.out.println("+1d="+now);
	*/
	String db="2008-08-02";
	TDate tdb=new TDate(db);
	System.out.println("TDate of "+db+" = "+tdb);
	System.out.println("toDBString="+tdb.toDBString());
    }

}
