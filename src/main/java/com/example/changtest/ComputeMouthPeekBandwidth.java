package com.example.changtest;

import com.example.changtest.Entity.DayPeek;
import com.example.changtest.Entity.MonthPeek;
import com.example.changtest.Entity.Sample;
import org.apache.commons.collections4.CollectionUtils;

import java.io.*;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

public class ComputeMouthPeekBandwidth {
    public static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm");
    private static SimpleDateFormat dayFormat = new SimpleDateFormat("yyyy-MM-dd");

    private static SimpleDateFormat monthFormat = new SimpleDateFormat("yyyy-MM");

    //输入csv文件并进行java对象转化
    public static List<Sample> inputCSV(File file) {
        List<String> dataList = new ArrayList<String>();
        List<Sample> sampleList = new ArrayList<>();
        BufferedReader br = null;

        try {
            br = new BufferedReader(new FileReader(file));
            String line = "";
            while ((line = br.readLine()) != null) {
                dataList.add(line);
            }
            for (String data : dataList) {
                String[] columns = data.split(",");
                Sample sample = new Sample();
                sample.setTimestamp(Long.parseLong(columns[0]));
                sample.setInputBandwidth(Double.parseDouble(columns[1]));
                sample.setOutputBandwidth(Double.parseDouble(columns[2]));
                sample.setInstanceId(columns[3]);
                sampleList.add(sample);
            }
        } catch (Exception e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                    br = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sampleList;
    }

    //转化timestamp为对应的精确到天的时间，以及精确到月的时间
    public static List<Sample> transformTimeStamp(List<Sample> sampleList) {
        List<Sample> filterSampleList = sampleList.stream().filter(sample -> {
            return Objects.nonNull(sample) && Objects.nonNull(sample.getTimestamp())
                    && Objects.nonNull(sample.getInstanceId())
                    && Objects.nonNull(sample.getInputBandwidth())
                    && Objects.nonNull(sample.getOutputBandwidth());
        }).collect(Collectors.toList());

        //时间转化计算以及采样点带宽值
        filterSampleList.stream().forEach(sample -> {
                    long temp = sample.getTimestamp() * 1000;
                    Timestamp timestamp = new Timestamp(temp);
                    String sampleTime = dateFormat.format(timestamp);
                    String dayDate = dayFormat.format(timestamp);
                    String monthDate = monthFormat.format(timestamp);
                    sample.setSampleTime(sampleTime);
                    sample.setDayTime(dayDate);
                    sample.setMonthTime(monthDate);

                    Double inputBandwidth = sample.getInputBandwidth();
                    Double outputBandwidth = sample.getOutputBandwidth();
                    Double sampleBandwidth = (inputBandwidth >= outputBandwidth) ? inputBandwidth : outputBandwidth;
                    sample.setSampleBandwidth(sampleBandwidth);
                }
        );
        return filterSampleList;
    }

    public static List<MonthPeek> computePeek(List<Sample> sampleList) {
        //根据实例id，月份，天进行list的数据分组
        Map<String, Map<String, Map<String, List<Sample>>>> sampleMap =
                sampleList.stream().sorted(Comparator.comparing(Sample::getTimestamp)).collect(
                        Collectors.groupingBy(Sample::getInstanceId, Collectors.groupingBy(Sample::getMonthTime, Collectors.groupingBy(Sample::getDayTime)))
                );

        List<MonthPeek> monthPeekList = new ArrayList<>();
        //计算每个实例的月峰值
        sampleMap.forEach((instanceId, monthMap) -> {
                    monthMap.forEach((month, dayMap) -> {
                                //一个月的30天的日峰值链表
                                List<DayPeek> dayPeekList = new ArrayList<>();

                                //一天的288个每隔5分钟的采样 samples
                                dayMap.forEach((day, samples) -> {

                                    //逆序，选择第五个作为当天的日峰值
                                    if (CollectionUtils.size(samples) >= 5) {
                                        Sample sample = samples.stream()
                                                .sorted(Comparator.comparing(Sample::getSampleBandwidth).reversed())
                                                .limit(5).collect(Collectors.toList()).get(4);
                                        Double dayBandwidth = sample.getSampleBandwidth();
                                        //日峰值
                                        DayPeek dayPeek = new DayPeek(instanceId, dayBandwidth, day);
                                        dayPeekList.add(dayPeek);
                                    }
                                });

                                //计算出逆序前5个平均值得到这个月的月峰值
                                if (CollectionUtils.size(dayPeekList) >= 5) {
                                   List<DayPeek> fiveBiggest = dayPeekList.stream()
                                            .sorted(Comparator.comparing(DayPeek::getDayPeek).reversed()).limit(5).collect(Collectors.toList());
                                   Double monthPeek =   (fiveBiggest.stream().mapToDouble(DayPeek::getDayPeek).sum())/5;
                                    monthPeekList.add(new MonthPeek(instanceId, monthPeek, month));
                                }
                            }
                    );
                }
        );
        return monthPeekList;
    }

    //输出文件
    public static void outputMonthPeeks(List<MonthPeek> monthPeekList) throws IOException, IllegalAccessException {
        File file = new File("D:/output.csv");
        OutputStreamWriter ow = new OutputStreamWriter(Files.newOutputStream(file.toPath()), "gbk");
        String[] titles = new String[]{"instanceId", "month", "monthPeek"};
        String[] properties = new String[]{"instanceId", "sampleTime", "monthPeek"};
        //写文件头
        for (int i = 0; i <titles.length ; i++) {
            ow.write(titles[i]);
            if(i!=titles.length-1){
                ow.write(",");
            }
        }
        //写完文件头换行
        ow.write("\n");

        //写内容
        if (CollectionUtils.isNotEmpty(monthPeekList)) {
            //自定义排序，先根据实例id排序后根据日期排序
            monthPeekList = monthPeekList.stream().sorted((month1, month2) -> {
                String id1 = month1.getInstanceId();
                String id2 = month2.getInstanceId();
                if (id1 == id2) {
                    try {
                        Date date1 = monthFormat.parse(month1.getSampleTime());
                        Date date2 = monthFormat.parse(month2.getSampleTime());
                        return date1.compareTo(date2);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                return id1.compareTo(id2);
            }).collect(Collectors.toList());


            //利用反射写文件
            for (Object obj : monthPeekList) {
                Field[] fields = obj.getClass().getDeclaredFields();
                for (String property : properties) {
                    for (Field field : fields) {
                        field.setAccessible(true);
                        if (property.equals(field.getName())) {
                            ow.write(field.get(obj).toString());
                            ow.write(",");
                            continue;
                        }
                    }
                }
                ow.write("\n");
            }
        }
        ow.flush();
        ow.close();
    }

    public static void main(String[] args) throws IOException, IllegalAccessException {
        File inputFile = new File("D:/input.csv");
        List<Sample> sampleList = inputCSV(inputFile);
        List<Sample> filterSampleList = transformTimeStamp(sampleList);
        List<MonthPeek> monthPeekList = computePeek(filterSampleList);
        outputMonthPeeks(monthPeekList);
    }
}
