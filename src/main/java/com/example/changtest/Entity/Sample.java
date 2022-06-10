package com.example.changtest.Entity;

public class Sample {
    //时间戳
    public Long timestamp;
    //精确到分钟的时间
    public String sampleTime;
    //精确到天的时间
    public String dayTime;
    //精确到月的时间
    public String monthTime;
    //5分钟内入方向带宽平均值
    public Double inputBandwidth;
    //5分钟内出方向带宽平均值
    public Double outputBandwidth;
    //采样点带宽值
    public Double sampleBandwidth;
    //实例Id
    public String instanceId;

    public Sample(Long timestamp, Double inputBandwidth, Double outputBandwidth, String instanceId) {
        this.timestamp = timestamp;
        this.inputBandwidth = inputBandwidth;
        this.outputBandwidth = outputBandwidth;
        this.instanceId = instanceId;
    }

    public Sample() {
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getSampleTime() {
        return sampleTime;
    }

    public void setSampleTime(String sampleTime) {
        this.sampleTime = sampleTime;
    }

    public String getDayTime() {
        return dayTime;
    }

    public void setDayTime(String dayTime) {
        this.dayTime = dayTime;
    }

    public String getMonthTime() {
        return monthTime;
    }

    public void setMonthTime(String monthTime) {
        this.monthTime = monthTime;
    }


    public Double getInputBandwidth() {
        return inputBandwidth;
    }

    public void setInputBandwidth(Double inputBandwidth) {
        this.inputBandwidth = inputBandwidth;
    }

    public Double getOutputBandwidth() {
        return outputBandwidth;
    }

    public void setOutputBandwidth(Double outputBandwidth) {
        this.outputBandwidth = outputBandwidth;
    }

    public Double getSampleBandwidth() {
        return sampleBandwidth;
    }

    public void setSampleBandwidth(Double sampleBandwidth) {
        this.sampleBandwidth = sampleBandwidth;
    }

    public String getInstanceId() {
        return instanceId;
    }

    public void setInstanceId(String instanceId) {
        this.instanceId = instanceId;
    }
}
