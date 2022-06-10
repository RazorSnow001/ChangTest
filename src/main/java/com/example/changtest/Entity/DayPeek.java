package com.example.changtest.Entity;

public class DayPeek {
    //实例Id
    public String instanceId;
    //日峰值
    public Double dayPeek;
    //取样日期精确到天
    public String simpleTime;

    public DayPeek(String instanceId, Double dayPeek, String simpleTime) {
        this.instanceId = instanceId;
        this.dayPeek = dayPeek;
        this.simpleTime = simpleTime;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Double getDayPeek() {
        return dayPeek;
    }

    public void setDayPeek(Double dayPeek) {
        this.dayPeek = dayPeek;
    }

    public String getSimpleTime() {
        return simpleTime;
    }

    public void setSimpleTime(String simpleTime) {
        this.simpleTime = simpleTime;
    }
}
