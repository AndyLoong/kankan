package com.android.calendar.cardview;

import com.android.calendar.cardview.AgendaUtils.AgendaItem;

import java.util.ArrayList;

/**
 * Created by xiaozhilong on 2/21/17.
 */

public class CalendarData {

    //weather data
    private String weatherContent;

    //week data
    private String[] headerDayNumbers;
    private String[] headerDayLunars;
    private int todayIndex = -1;

    //agenda data
    private String almanacFit;
    private long agendaRefreshTime;
    private ArrayList<AgendaItem> agendaItems;

    public void setWeatherContent(String weatherContent) {
        this.weatherContent = weatherContent;
    }

    public String getWeatherContent() {
        return weatherContent;
    }

    public void setHeaderDayNumbers(String[] headerDayNumbers) {
        this.headerDayNumbers = headerDayNumbers;
    }

    public String[] getHeadDayNumbers() {
        return headerDayNumbers;
    }

    public void setHeaderDayLunars(String[] headerDayLunars) {
        this.headerDayLunars = headerDayLunars;
    }

    public String[] getHeaderDayLunars() {
        return headerDayLunars;
    }

    public void setTodayIndex(int index) {
        this.todayIndex = index;
    }

    public int getTodayIndex() {
        return todayIndex;
    }

    public void setAlmanacFit(String almanacFit) {
        this.almanacFit = almanacFit;
    }

    public String getAlmanacFit() {
        return almanacFit;
    }

    public void setAgendaRefreshTime(long time) {
        agendaRefreshTime = time;
    }

    public long getAgendaRefreshTime() {
        return agendaRefreshTime;
    }

    public void setAgendaData(ArrayList<AgendaItem> agendaItems) {
        this.agendaItems = agendaItems;
    }

    public ArrayList<AgendaItem> getAgendaData() {
        return agendaItems;
    }

}
