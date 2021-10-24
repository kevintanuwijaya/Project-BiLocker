package com.bilocker.utils;


import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Convert {

    private static Convert instance = null;

    public static Convert getInstance() {
        if (instance == null){
            return new Convert();
        }
        return instance;
    }

    private  Convert(){}

    public Timestamp convertStringToTimestamp(String strDate) {

        if(strDate == null){
            return null;
        }else{
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                Date parsedDate = dateFormat.parse(strDate);
                Timestamp timestamp = new Timestamp(parsedDate.getTime());

                return  timestamp;

            } catch(Exception e) { //this generic but you can control another types of exception
                // look the origin of excption
            }

        }

        return null;
    }
}
