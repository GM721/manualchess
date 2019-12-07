package CommonClasses;

import java.io.Serializable;
import java.util.Date;
import java.time.LocalDate;
import java.time.ZoneId;

public class Calendar implements Serializable {
	private static final long serialVersionUID = 13;
	public int year;
	int month;
	int day;
	int hour;
	int minute;
	int second;
	public static final int YEAR = 1;
	public static final int MONTH = 2;
	public static final int DAY_OF_MONTH = 3;
	public static final int HOUR = 4;
	public static final int MINUTE = 5;
	public static final int SECOND = 6;

	public static Calendar getInstance(){
		Calendar calendar = new Calendar();
		calendar.year=0;
		calendar.month=0;
		calendar.day=0;
		calendar.hour=0;
		calendar.minute=0;
		calendar.second=0;
		return calendar;
	}
	public String toString(){
		String year = this.get(Calendar.YEAR)<1000 ? "0" + this.get(Calendar.YEAR) : Integer.toString(this.get(Calendar.YEAR));
		String month = this.get(Calendar.MONTH)<10 ? "0"+this.get(Calendar.MONTH) : Integer.toString(this.get(Calendar.MONTH));
		String day = this.get(Calendar.DAY_OF_MONTH)<10 ? "0"+this.get(Calendar.DAY_OF_MONTH) : Integer.toString(this.get(Calendar.DAY_OF_MONTH));
		String hour = this.get(Calendar.HOUR)<10 ? "0"+this.get(Calendar.HOUR) : Integer.toString(this.get(Calendar.HOUR));
		String minute = this.get(Calendar.MINUTE)<10 ? "0"+this.get(Calendar.MINUTE) : Integer.toString(this.get(Calendar.MINUTE));
		String second = this.get(Calendar.SECOND)<10 ? "0"+this.get(Calendar.SECOND) : Integer.toString(this.get(Calendar.SECOND));
		return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;
	}
    public static Calendar fromDate(java.util.Date date)
    {
	    LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
	    Calendar res = Calendar.getInstance();
	    res.set(localDate.getYear(), localDate.getMonthValue(), localDate.getDayOfMonth(), date.getHours(), date.getMinutes(), date.getSeconds());
	    return res;
    }
    public int get(int type){
        switch (type){
            case 1:
                return year;
            case 2:
                return month;
            case 3:
                return day;
            case 4:
                return hour;
            case 5:
                return minute;
            case 6:
                return second;
             default:
                 return -1;
        }
    }
    public void set(int year,int month,int day,int hour,int minute,int second){
        this.year = year;
        this.month = month;
        this.day = day;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }
    public Date toDate()
    {
	    Date result = new Date(year - 1899, month, day, hour, minute, second);
	    return result;
    }
}
