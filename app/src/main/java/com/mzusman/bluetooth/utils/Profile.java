package com.mzusman.bluetooth.utils;

/*
 * Class : .
 * Created by mzusman - morzusman@gmail.com on 4/20/16.
 */
public class Profile  {
    private String Date;
    private String Time;
    private String Run_Time;
    private String Avg_Speed;
    private String Avg_RPM;
    private String Driver_Name;
    private String File_Path;

    public String getFile_Path() {
        return File_Path;
    }

    public String getDate() {
        return Date;
    }

    public String getTime() {
        return Time;
    }

    public String getRun_Time() {
        return Run_Time;
    }

    public String getAvg_Speed() {
        return Avg_Speed;
    }

    public String getAvg_RPM() {
        return Avg_RPM;
    }

    public String getDriver_Name() {
        return Driver_Name;
    }

    public Profile(String date, String time, String run_Time, String avg_Speed, String avg_RPM, String driver_Name, String file_Path) {
        Date = date;
        Time = time;
        Run_Time = run_Time;
        Avg_Speed = avg_Speed;
        Avg_RPM = avg_RPM;
        Driver_Name = driver_Name;
        File_Path = file_Path;
    }
}
