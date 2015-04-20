/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package javacore;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import org.apache.commons.configuration2.XMLConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;

/**
 *
 * @author dell123
 */
public class Day3 {

    private XMLConfiguration config;
    private int index = 0;
    DateFormat df = new SimpleDateFormat("yyyyMMddhhmmss");

    public Day3() {
    }

    private XMLConfiguration loadConfig(String filePath) {
        XMLConfiguration conf = null;
        try {
            Parameters params = new Parameters();

            FileBasedConfigurationBuilder<XMLConfiguration> builder
                    = new FileBasedConfigurationBuilder<XMLConfiguration>(XMLConfiguration.class)
                    .configure(params.xml().setFileName(filePath));
            conf = builder.getConfiguration();

        } catch (Exception ex) {
            System.out.print(ex.getMessage());
        }
        return conf;
    }

    private List<String> getAllFilename(String inputDir) {
        List<String> results = new ArrayList<>();
        //input_17_04_2015_10_5.txt
        File folder = new File(inputDir);
        if (folder.isDirectory()) {
            DateFormat df = new SimpleDateFormat("dd_MM_yyyy_HH_mm");
            List<Date> resultsDate = new ArrayList<>();
            File[] files = folder.listFiles();
            //add file into result
            for (int i = 0; i < files.length; i++) {
                if (files[i].isFile()) {
                    String filename = files[i].getName();
                    try {
                        filename = filename.replace("input_", "").replace(".txt", "");
                        resultsDate.add(df.parse(filename));
                    } catch (ParseException ex) {
                        System.out.println("ERROR" + filename + ":" + ex.getMessage());
                    }
                }
            }
            Collections.sort(resultsDate);//sort
            //make the filename
            for (int i = 0; i < resultsDate.size(); i++) {
                results.add(inputDir + "\\" + "input_"
                        + df.format(resultsDate.get(i).getTime()) + ".txt");
            }
        } else {
            System.out.println("inputDir is not correct");
        }
        return results;
    }

    private void showLog(Object obj) {
        System.out.println(obj);
    }

    private String processData(String input) {
        int transType = Integer.valueOf(input.substring(input.lastIndexOf("|") + 1));
        if (transType == 1) {
            return input + "|" + config.getString(ConfigApp.TRANS_VALUE);
        } else if (transType == 2) {
            return input + "|0";
        } else {
            return null;
        }
    }

    public void processFiles() {
        config = loadConfig("configApp.xml");
        List<String> files = getAllFilename(config.getString(ConfigApp.INPUT_PATH));

        int countFile = files.size();
        for (int i = 0; i < countFile; i++) {
            List<String> output = processInputFile(files.get(i));
            int outputRecord = config.getInt(ConfigApp.OUTPUT_RECORD);
            for (int j = 0; j < output.size(); j = j + outputRecord) {
                if (output.size() >= j + outputRecord) {
                    processOutFile(output.subList(j, j + outputRecord));
                } else {
                    processOutFile(output.subList(j, output.size()));
                }
            }
        }
    }

    private List<String> processInputFile(String fileName) {
        FileInputStream fis = null;
        Reader reader = null;
        BufferedReader br = null;

        List<String> output = new ArrayList<>();
        try {
            fis = new FileInputStream(fileName);
            reader = new InputStreamReader(fis);
            br = new BufferedReader(reader);
            String line = null;
            while ((line = br.readLine()) != null) {
                output.add(processData(line));
            }
        } catch (Exception ex) {
            System.out.println("[processInputFile]" + ex.getMessage());
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (fis != null) {
                    fis.close();
                }
            } catch (IOException ex) {
                System.out.println("[processInputFile]" + ex.getMessage());
            }
        }
        return output;
    }

    private void processOutFile(List<String> output) {
        FileOutputStream fos = null;
        Writer writer = null;
        BufferedWriter bw = null;
        try {
            String newFileName = getNewFileName();

            File newFile = new File(newFileName);
            newFile.createNewFile();

            fos = new FileOutputStream(newFile);
            writer = new OutputStreamWriter(fos);
            bw = new BufferedWriter(writer);

            for (int i = 0; i < output.size(); i++) {
                bw.write(output.get(i));
                if (i != output.size() - 1) {
                    bw.newLine();
                }
            }
        } catch (Exception ex) {
            System.out.println("[processOutFile]" + ex.getMessage());
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException ex) {
                System.out.println("[processOutFile]" + ex.getMessage());
            }
        }
    }

    private String getNewFileName() {
        //output_yyyyMMddhhmmss_seq.txt
        String folderPath = config.getString(ConfigApp.OUTPUT_PATH) + "\\";
        String currDate = df.format(Calendar.getInstance().getTime());
        return folderPath + "output_" + currDate + "_" + (index++) + ".txt";
    }
}
