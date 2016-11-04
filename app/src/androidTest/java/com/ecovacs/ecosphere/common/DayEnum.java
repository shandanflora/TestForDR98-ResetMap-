package com.ecovacs.ecosphere.common;

/**
 * Created by lily.shan on 2016/7/5.
 */
public enum DayEnum {

    SUN(1), MON(2), TUES(3), WEDNES(4), THUR(5), FRI(6), SAT(7);

    private int value = 0;

    DayEnum(int value) {    //    必须是private的，否则编译错误
        this.value = value;
    }

    public static DayEnum valueOf(int value) {    //    手写的从int到enum的转换函数
        switch (value) {
            case 1:
                return SUN;
            case 2:
                return MON;
            case 3:
                return TUES;
            case 4:
                return WEDNES;
            case 5:
                return THUR;
            case 6:
                return FRI;
            case 7:
                return SAT;
            default:
                return null;
        }
    }

    public static String valueOfStr(DayEnum dayEnum) {    //    手写的从int到enum的转换函数
        switch (dayEnum) {
            case SUN:
                return "周日";
            case MON:
                return "周一";
            case TUES:
                return "周二";
            case WEDNES:
                return "周三";
            case THUR:
                return "周四";
            case FRI:
                return "周五";
            case SAT:
                return "周六";
            default:
                return "";
        }
    }

    public int value() {
        return this.value;
    }

}
