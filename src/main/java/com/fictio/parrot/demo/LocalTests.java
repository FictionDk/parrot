package com.fictio.parrot.demo;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;
import java.util.stream.Collectors;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.junit.Test;

import lombok.AllArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalTests {
    final String sql = "INSERT INTO blood_label (\"tid\",\"don_code\",\"flag\",\"remark\",\"vendor\",\"created_at\") "
            + "VALUES (TID, SID, FLAG, MARK, VENDOR, TIME) ON CONFLICT (\"tid\") DO NOTHING;";

    final String timePattern = "yyyy-MM-dd HH:mm:ss";

    private List<String> readFile(String path) {
        List<String> result = new ArrayList<String>();
        BufferedReader reader = null;
        try {
            InputStreamReader in = new InputStreamReader(new FileInputStream(path),"utf-8");
            reader = new BufferedReader(in);
            String s = null;
            while((s=reader.readLine()) != null) {
                result.add(s);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(reader != null)
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        return result;
    }

    private String buildSql(Label label) {
        String now = LocalDateTime.now().format(DateTimeFormatter.ofPattern(timePattern));
        now = "2021-05-14 15:40:00";
        if(label == null) return null;
        else return sql.replace("TID", label.tid).replace("SID", label.sid).replace("FLAG", label.flag)
            .replace("VENDOR", "\'SOAP\'").replace("MARK", label.remark)
            .replace("TIME", "\'"+now+"\'");
    }

    private void writeFile(String path, Set<Label> labels) {
        BufferedWriter writer = null;
        try {
            OutputStreamWriter out = new OutputStreamWriter(new FileOutputStream(path), "utf-8");
            writer = new BufferedWriter(out);
            for(Label label : labels) {
                String sql = buildSql(label);
                if(sql == null) continue;
                writer.write(buildSql(label));
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @AllArgsConstructor
    @ToString
    private class Label {
        String tid;
        String sid;
        String flag;
        String remark;
    }

    private Set<Label> buildLabels(List<String> lines){
        return lines.stream().map(line->{
            String[] rows = line.split(",");
            if("true".equals(rows[12])) return new Label(
                    doubleToSingle(rows[0]),
                    doubleToSingle(rows[1].substring(0, 14)+"\'"),
                    doubleToSingle(rows[8]),
                    doubleToSingle(rows[9]));
            else return null;
        }).collect(Collectors.toSet());
    }

    private String doubleToSingle(String in) {
        return in.replace("\"", "\'");
    }

    @Test
    public void dateTests() {
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTimeInMillis(System.currentTimeMillis());
        cal.setTimeZone(TimeZone.getTimeZone("CTT"));
        try {
            XMLGregorianCalendar calendar  = DatatypeFactory.newInstance().newXMLGregorianCalendar(cal);
            log.debug("{}",calendar.toXMLFormat());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
        }
    }


    @Test
    public void test() {
        List<String> lines = readFile("E:\\home\\dingtalk\\50-常州中心血站12.txt");
        System.out.println(lines.size());
        Set<Label> labels = buildLabels(lines);
        System.out.println(labels.size());
        writeFile("E:\\download\\labels.sql",labels);
    }

    @Test
    public void readOnlyTest(){
        File file = new File("E:\\resouces\\Nextcloud\\智慧浆云\\智能制造\\TEMP\\rows.txt");
        file.setReadOnly();
    }
}
