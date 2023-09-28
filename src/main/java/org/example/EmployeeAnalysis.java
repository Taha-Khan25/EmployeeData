package org.example;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class EmployeeAnalysis {

    public static void main(String[] args) {
        String csvFile = "src/main/java/org/example/Assignment_Timecard.csv"; // Replace with your CSV file path

        List<EmployeeRecord> records = new ArrayList<>();

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy hh.mm a", Locale.ENGLISH);

        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            br.readLine(); // Skip the header row

            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 10) {
                    String employeeName = parts[7].trim();
                    String positionID = parts[0].trim();
                    String timeStartStr = parts[2].trim();
                    String timeOutStr = parts[3].trim();
                    String date = parts[2].split(" ")[0].trim();
                    String timecardHoursStr = parts[4].trim();

                    if (!timeStartStr.isEmpty() && !timeOutStr.isEmpty()) {
                        Date timeStart = dateFormat.parse(timeStartStr);
                        Date timeOut = dateFormat.parse(timeOutStr);
                        double hoursWorked = parseTimecardHours(timecardHoursStr);

                        EmployeeRecord record = new EmployeeRecord(employeeName, positionID, date,timeStart, timeOut, hoursWorked);
                        records.add(record);
                    }
                }
            }
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        Set<EmployeeRecord> consecutive7Days = new HashSet<>();
        Set<EmployeeRecord> shortBreaks = new HashSet<>();
        //List<EmployeeRecord> longShifts = new ArrayList<>();
        HashMap<String,EmployeeRecord> longShifts=new HashMap<>();

        int consecutiveDays = 1;
        for (int i = 1; i < records.size(); i++) {
            EmployeeRecord currentRecord = records.get(i);
            EmployeeRecord previousRecord = records.get(i - 1);

            double hoursBetweenShifts = (currentRecord.timeStart.getTime() - previousRecord.timeOut.getTime()) / (60 * 60 * 1000);

            if (currentRecord.employeeName.equals(previousRecord.employeeName)) {
                if (hoursBetweenShifts < 10 && hoursBetweenShifts > 1) {
                    shortBreaks.add(currentRecord);
                }

                if (hoursBetweenShifts > 14) {
                    if (!longShifts.containsKey(currentRecord.positionID)) {
                        longShifts.put(currentRecord.positionID, currentRecord);
                    }
                }

                if (currentRecord.timeStart.getTime() - previousRecord.timeOut.getTime() <= 24 * 60 * 60 * 1000) {
                    consecutiveDays++;
                    if (consecutiveDays >= 7) {
                        consecutive7Days.add(currentRecord);
                    }
                } else {
                    consecutiveDays = 1;
                }
            }
        }

        // Print employees meeting each criterion
        System.out.println("Employees who have worked for 7 consecutive days:");
        for (EmployeeRecord record : consecutive7Days) {
            System.out.println("Name: " + record.employeeName + ", Position ID: " + record.positionID);
        }

        System.out.println("\nEmployees who have less than 10 hours between shifts:");
        for (EmployeeRecord record : shortBreaks) {
            System.out.println("Name: " + record.employeeName + ", Position ID: " + record.positionID);
        }

        System.out.println("\nEmployees who have worked for more than 14 hours in a single shift:");
        longShifts.forEach((k, v)
                -> System.out.println("Position ID:" + k + " " + "Employee Name: " + v.employeeName));
    }


    private static double parseTimecardHours(String timecardHoursStr) {
        String[] parts = timecardHoursStr.split(":");
        if (parts.length == 2) {
            int hours = Integer.parseInt(parts[0].trim());
            int minutes = Integer.parseInt(parts[1].trim());
            return hours + (minutes / 60.0);
        }
        return 0.0;
    }
}