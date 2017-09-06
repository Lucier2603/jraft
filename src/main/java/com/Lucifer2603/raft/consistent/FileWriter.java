package com.Lucifer2603.raft.consistent;

import org.apache.commons.io.IOUtils;

import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author zhangchen20
 */
public class FileWriter {

    private FileOutputStream os;

    void write(String input) {
        write(input, "UTF-8");
    }

    void write(String input, String encoding) {

        try {

            IOUtils.write(input, os, encoding);

        } catch (IOException e) {
            throw new RuntimeException("");
        }
    }
}
