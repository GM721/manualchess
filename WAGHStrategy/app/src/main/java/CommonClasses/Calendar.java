package CommonClasses;

import java.io.Serializable;



public class Calendar implements Serializable {
    private static final long serialVersionUID = 13;
    int year;
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
    public static Calendar getInstance(String date){
        Calendar calendar = new Calendar();
        calendar.set(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),
                Integer.parseInt(date.substring(8,10)), Integer.parseInt(date.substring(11,13)),
                Integer.parseInt(date.substring(14,16)),Integer.parseInt(date.substring(17,19)));
        return calendar;
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
    public boolean before(Calendar calendar){
        if(this.year<calendar.year) return true;
        if(this.month<calendar.month) return true;
        if(this.day<calendar.day) return true;
        if(this.hour<calendar.hour) return true;
        if(this.minute<calendar.minute) return true;
        if(this.second<calendar.second) return true;
        return false;
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
}
