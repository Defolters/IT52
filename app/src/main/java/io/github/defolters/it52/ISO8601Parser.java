package io.github.defolters.it52;

// Singleton – eager initialization
// "2018-09-22T11:00:00.000+03:00"

public class ISO8601Parser {
    private static ISO8601Parser parser = new ISO8601Parser();
    private int year;
    private int month;
    private int day;
    private int hour;
    private int minute;
    private int second;

    private ISO8601Parser() {}

    public static ISO8601Parser getISO8601Parser(){
        return parser;
    }

    public ISO8601Parser setDate(String dateString){
        //"2018-09-22T11:00:00.000+03:00"
        String date = dateString.split("T")[0];
        String time = dateString.split("T")[1];

        year = Integer.parseInt(date.split("-")[0]);
        month = Integer.parseInt(date.split("-")[1]);
        day = Integer.parseInt(date.split("-")[2]);
        hour = Integer.parseInt(time.split(":")[0]);
        minute = Integer.parseInt(time.split(":")[1]);
        second = Integer.parseInt(time.split(":")[2].substring(0,1));

        //check if error
        return this;
    }

    public int getYear() {
        return year;
    }

    public int getMonth() {
        return month;
    }

    public int getDay() {
        return day;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}
