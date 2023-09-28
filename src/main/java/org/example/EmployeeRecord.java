package org.example;
import java.util.Date;

 public class EmployeeRecord {
     String employeeName;
     String positionID;
     Date timeStart;
     String date;
     Date timeOut;

     double hoursWorked;

     EmployeeRecord(String employeeName, String positionID,String date, Date timeStart, Date timeOut, double hoursWorked) {
         this.employeeName = employeeName;
         this.positionID = positionID;
         this.timeStart = timeStart;
         this.date = date;
         this.timeOut = timeOut;
         this.hoursWorked = hoursWorked;
     }
 }
