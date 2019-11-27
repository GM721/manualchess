package CommonClasses;

import java.util.Calendar;

public class Message {
    public String sender;
    public String receiver;
    public String message;
    public Calendar date;

    public Message(MessageAnswer messageAnswer){
        sender = messageAnswer.sender;
        receiver = messageAnswer.receiver;
        message = messageAnswer.message;
        date = messageAnswer.date;
    }

    public Message(String sender,String receiver,String message,String date){
        this.sender = sender;
        this.receiver = receiver;
        this.message = message;
        this.date = Calendar.getInstance();
        this.date.set(Integer.parseInt(date.substring(0,4)),Integer.parseInt(date.substring(5,7)),
                Integer.parseInt(date.substring(8,10)), Integer.parseInt(date.substring(11,13)),
                Integer.parseInt(date.substring(14,16)),Integer.parseInt(date.substring(17,19)));
    }

    public String getDate(){
        String year = Integer.toString(date.get(Calendar.YEAR));
        String month = date.get(Calendar.MONTH)<10 ? "0"+date.get(Calendar.MONTH) : Integer.toString(date.get(Calendar.MONTH));
        String day = date.get(Calendar.DAY_OF_MONTH)<10 ? "0"+date.get(Calendar.DAY_OF_MONTH) : Integer.toString(date.get(Calendar.DAY_OF_MONTH));
        String hour = date.get(Calendar.HOUR)<10 ? "0"+date.get(Calendar.HOUR) : Integer.toString(date.get(Calendar.HOUR));
        String minute = date.get(Calendar.MINUTE)<10 ? "0"+date.get(Calendar.MINUTE) : Integer.toString(date.get(Calendar.MINUTE));
        String second = date.get(Calendar.SECOND)<10 ? "0"+date.get(Calendar.SECOND) : Integer.toString(date.get(Calendar.SECOND));
        return year+"-"+month+"-"+day+" "+hour+":"+minute+":"+second;

    }
}
