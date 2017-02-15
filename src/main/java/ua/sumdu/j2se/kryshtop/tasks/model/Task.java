package ua.sumdu.j2se.kryshtop.tasks.model;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import ua.sumdu.j2se.kryshtop.tasks.model.exceptions.MinusException;

/**
 * This class describes task.
 * @version 1.0
 * @author Kryshtop Andrii
 **/
public class Task implements Cloneable, Serializable{
    private String title;
    private Date time;
    private Date start;
    private Date end;
    private int interval;
    private boolean active;
    private boolean repeat;
//TODO: remake data types into Properties


    /**
     * To initialize an inactive task that is performed at the set time,
     * without repeating and with the established name.
     * @param title title of the task
     * @param time a time when the task performed
     */
    public Task(String title, Date time) {
        this.title = title;
        this.time = time;
    }

    /**
     * To initialize an inactive task that is performed in set distance of time,
     * with the established interval and established title.
     * @param title title of the task
     * @param start start of the distance
     * @param end end of the distance
     * @param interval interval of task performance
     */
    public Task(String title, Date start, Date end, int interval) {
        this.title = title;
        this.start = start;
        this.end = end;
        this.interval = interval;
        this.repeat = true;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * To get time of performance for tasks, that do not repeat.
     * @return time of performance (if task repeats - return start time of repetition)
     */
    public Date getTime() {
        if (repeat) return start;
        else return time;
    }

    /**
     * To set time for not repeatable tasks.
     * If task is repeatable - will make it not repeatable.
     * @param time time of performance
     * @throws MinusException if time is minus
     */
    public void setTime(Date time){
        this.time = time;

        if (repeat) {
            repeat = false;
        }
    }

    /**
     * To get start time.
     * @return start time (if task is not repeatable - return time of performance)
     */
    public Date getStartTime() {
        if (!repeat) return time;
        else return start;
    }

    /**
     * To get end time.
     * @return end time (if task is not repeatable - return time of performance)
     */
    public Date getEndTime() {
        if (!repeat) return time;
        else return end;
    }

    /**
     * To get repeat interval.
     * @return interval (if task is not repeatable - return 0)
     */
    public int getRepeatInterval(){
        return interval;
    }

    /**
     * To set time for repeatable tasks.
     * If task is not repeatable - will make it repeatable.
     * @param start start time
     * @param end start time
     * @param interval repeat interval
     * @throws MinusException if start, end, or interval is invalid
     */
    public void setTime(Date start, Date end, int interval) throws MinusException{
        if((end.compareTo(start) <= 0)
                || (interval == 0))
            throw new MinusException("You have entered a wrong start, end or interval");

        if (!repeat) repeat = true;

        this.start = start;
        this.end = end;
        this.interval = interval;
    }

    public boolean isRepeated() {
        return repeat;
    }

    /**
     * To get next time of task performance after established time.
     * @param current current time
     * @return nest time of task performance
     * (if task is not performed after established time - return -1)
     */
    public Date nextTimeAfter(Date current){
        if (!active) {
            return null;
        }

        if (!repeat) {
            if (time.compareTo(current) != 1) {
                return null;
            } else return time;
        }
        else {
            if (end.compareTo(current) == -1) {
                return null;
            }
            else {
                Date buffer = (Date)start.clone();
                while (buffer.compareTo(current) != 1) {
                    buffer.setTime(buffer.getTime() + (interval * 1000L));
                }
                if (buffer.compareTo(end) == 1)  return null; 
                return buffer;
            }

        }

    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Task task = (Task) o;

        if (active != task.active) return false;
        if (repeat != task.repeat) return false;

        if (repeat) {
            if (!start.equals(task.start)) return false;
            if (!end.equals(task.end)) return false;
            if (interval != task.interval) return false;
        }
        else {
            if (!time.equals(task.time)) return false;
        }

        return title.equals(task.title);
    }

    @Override
    public int hashCode() {
        int result = (title == null? 0 : title.hashCode());
        result = 31 * result + (active ? 1 : 0);
        result = 31 * result + (repeat ? 1 : 0);
        if(repeat){
            result = 31 * result + (start == null? 0 : start.hashCode());
            result = 31 * result + (end == null? 0 : end.hashCode());
            result = 37 * result + interval;
        }
        else {
            result = 31 * result + (time == null? 0 : time.hashCode());
        }
        return result;
    }

    @Override
    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
        String task;

        String title = getTitle();
        String time = "";
        String inactive = "";

        if(isRepeated() == true){
            String intervalString;
            int interval = getRepeatInterval();

            String days = "";
            int day = 86400;

            if((interval / day) >= 1){
                days += (interval / day) + " day";
                if((interval / day) > 1) days += "s";
                else days += " ";
                interval = interval % day;
            }

            String hours = "";
            int hour = 3600;
            if((interval / hour) >= 1){
                hours += (interval / hour) + " hour";
                if((interval / hour) > 1) hours += "s";
                interval = interval % hour;
            }

            String minutes = "";
            int minute = 60;
            if((interval / minute) >= 1){
                days += "" + (interval / minute) + " minute";
                if((interval / minute) > 1) minutes += "s";
            }

            String seconds = "";
            if (interval != 0){
                seconds = " " + interval + "second";
                if(interval > 1) seconds += "s";
            }

            intervalString = days + hours + minutes + seconds;

            time += "from [" + dateFormat.format(getTime()) +
                    "] to [" + dateFormat.format(getEndTime()) +
                    "] every [" + intervalString + "]";
        }
        else{
            time += "at [" + dateFormat.format(getTime()) + "]";
        }

        if(!isActive()) {
            inactive = " inactive";
        }

        task = "\"" + title + "\" " + time + inactive;

        return task;
    }

    @Override
    public Task clone() {
        Task outputTask;
        if(this.repeat) {
            outputTask = new Task(title, (Date)start.clone(), (Date)end.clone(), interval);
            outputTask.active = active;
        }
        else {
            outputTask = new Task(title, (Date)time.clone());
            outputTask.active = active;
        }
        return outputTask;
    }
}
