package com.example.kimdongun.promise.Dday;

import java.util.Calendar;

/**
 * Created by KimDongun on 2017-01-15.
 */

public class DdayClass {
    public static int calDday(int year, int month, int day){ //계산할 날짜
        Calendar calendar =Calendar.getInstance();
        calendar.set(year,month,day);
        long mD=calendar.getTimeInMillis();  //디데이날짜를 밀리타임으로 바꿈

        Calendar calendar2 = Calendar.getInstance();  //현재 날짜 불러옴
        calendar2.set(calendar2.get(Calendar.YEAR),calendar2.get(Calendar.MONTH),calendar2.get(Calendar.DAY_OF_MONTH));
        long mT=calendar2.getTimeInMillis();   //오늘 날짜를 밀리타임으로 바꿈

        //Dday 계산
        Long mR = (mD - mT);

        if(mR < 0){
            return -1;
        }else {
            return (int)(mR / (24 * 60 * 60 * 1000));
        }
    }

    public static int calDhour(int year, int month, int day, int hour, int minute){ //계산할 날짜
        Calendar calendar =Calendar.getInstance();
        calendar.set(year,month,day, hour, minute);
        long mD=calendar.getTimeInMillis();  //디데이날짜를 밀리타임으로 바꿈

        Calendar calendar2 = Calendar.getInstance();  //현재 날짜 불러옴
        calendar2.set(calendar2.get(Calendar.YEAR),calendar2.get(Calendar.MONTH),calendar2.get(Calendar.DAY_OF_MONTH),
                calendar2.get(Calendar.HOUR_OF_DAY), calendar2.get(Calendar.MINUTE));
        long mT=calendar2.getTimeInMillis();   //오늘 날짜를 밀리타임으로 바꿈

        //Dday 계산
        Long mR = (mD - mT);

        if(mR < 0){
            return -1;
        }else {
            return 1;
        }
    }
}
