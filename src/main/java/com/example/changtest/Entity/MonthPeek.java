package com.example.changtest.Entity;

public class MonthPeek {
    //实例Id
    public String instanceId;
    //日峰值
    public Double monthPeek;
    //取样时间精确到月
    public String sampleTime;

    public MonthPeek(String instanceId, Double monthPeek, String sampleTime) {
        this.instanceId = instanceId;
        this.monthPeek = monthPeek;
        this.sampleTime = sampleTime;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }

    public Double getMonthPeek() {
        return monthPeek;
    }

    public void setMonthPeek(Double monthPeek) {
        this.monthPeek = monthPeek;
    }

    public String getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(String sampleTime) {
        this.sampleTime = sampleTime;
    }
}
