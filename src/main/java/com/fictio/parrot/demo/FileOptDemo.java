package com.fictio.parrot.demo;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.junit.Test;

public class FileOptDemo {

    private File writeFile = new File("output.txt");

    @Test
    public void test() throws IOException {
        writeFile.createNewFile();
        writeFile("test1");
        writeFile("test2");
    }

    private void writeFile(String line) throws IOException {
        try (FileWriter writer = new FileWriter(writeFile, true); BufferedWriter out = new BufferedWriter(writer)) {
            out.write(line + "\r\n");
            out.flush();
        }

    }
}
