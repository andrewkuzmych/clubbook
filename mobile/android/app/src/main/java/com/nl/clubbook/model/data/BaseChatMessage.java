package com.nl.clubbook.model.data;

/**
 * Created by Volodymyr on 23.08.2014.
 */
public class BaseChatMessage {

    protected boolean isDateObject;
    protected long timeWithoutHours;

    public long getTimeWithoutHours() {
        return timeWithoutHours;
    }

    public void setTimeWithoutHours(long timeWithoutHours) {
        this.timeWithoutHours = timeWithoutHours;
    }

    public boolean isDateObject() {
        return isDateObject;
    }

    public void setDateObject(boolean isDateObject) {
        this.isDateObject = isDateObject;
    }
}
