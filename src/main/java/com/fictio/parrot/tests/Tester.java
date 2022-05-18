package com.fictio.parrot.tests;

import com.google.common.base.Strings;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;


@Slf4j
public class Tester {

    protected static class BloodBag implements Comparable<BloodBag> {
        String serialId;  //献血序列码
        String flag; //献血标签
        String productCode; //产品码
        String aboGroup; //abo血型码
        LocalDateTime expiredTime; //过期时间
        public static BloodBag readFromText(String line){
            if(Strings.isNullOrEmpty(line)) return null;
            String[] arr = line.split(",");
            if(arr.length < 4) throw new RuntimeException("Split Err");
            BloodBag bag = new BloodBag();
            bag.serialId = arr[0];
            bag.flag = arr[1];
            bag.productCode = arr[2];
            bag.aboGroup = arr[3];
            bag.expiredTime = LocalDateTime.parse(arr[4], DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            return bag;
        }
        @Override
        public boolean equals(Object obj){
            if(obj instanceof BloodBag){
                BloodBag b = (BloodBag) obj;
                return (this.serialId +this.flag).equals(b.serialId +b.flag) ||
                        (this.serialId +this.productCode).equals(b.serialId +b.productCode);
            }else{
                return false;
            }
        }
        @Override
        public int hashCode(){
            return serialId.hashCode();
        }
        @Override
        public int compareTo(BloodBag o) {
            int compared = expiredTime.compareTo(o.expiredTime);
            if(compared == 0) compared = aboGroup.compareTo(o.aboGroup);
            if(compared == 0) compared = serialId.compareTo(o.serialId);
            if(compared == 0) compared = productCode.compareTo(o.productCode);
            if(compared == 0) compared = flag.compareTo(o.flag);
            return -compared;
        }
        @Override
        public String toString(){
            return new Gson().toJson(this);
        }
        public String toLine(){
            return serialId + "," + flag + "," + productCode + "," + aboGroup + ","
                    + expiredTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        }
    }

    final static String INPUT_1 = "D:\\Resource\\tests\\01.txt";
    final static String INPUT_2 = "D:\\Resource\\tests\\02.txt";
    final static String OUTPUT = "D:\\Resource\\tests\\04.txt";

    @Test public void tests(){
        Set<BloodBag> data1 = getData(INPUT_1);
        Set<BloodBag> data2 = getData(INPUT_2);
        Set<BloodBag> result = new HashSet<>();
        result.addAll(data1);
        result.addAll(data2);
        Set<BloodBag> data3 = data1.stream().filter(data2::contains).collect(Collectors.toSet());
        saveToFile(data3);
        log.info("d1={},d2={},conflict={},merge={}",data1.size(), data2.size(),result.size(),data3.size());
    }

    private void saveToFile(Collection<BloodBag> bags){
        Set<BloodBag> bagSet = new TreeSet<>(bags);
        try(BufferedWriter writer = new BufferedWriter(new FileWriter(OUTPUT))) {
            for(BloodBag bag : bagSet){
                writer.write(bag.toLine());
                writer.newLine();
            }
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Set<BloodBag> getData(String uri) {
        Set<BloodBag> bags;
        List<BloodBag> bagList = new ArrayList<>();
        try (BufferedReader bufferedReader = new BufferedReader(new FileReader(uri))) {
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                BloodBag bag = BloodBag.readFromText(line);
                if (bag != null) bagList.add(bag);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        bags = new HashSet<>(bagList);
        log.info("size1={},size2={}",bagList.size(), bags.size());
        return bags;
    }
}