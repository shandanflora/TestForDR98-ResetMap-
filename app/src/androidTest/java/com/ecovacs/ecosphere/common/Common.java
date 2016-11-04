package com.ecovacs.ecosphere.common;

import android.os.SystemClock;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.robotium.solo.Solo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by lily.shan on 2016/6/15.
 * common function in this class
 */
public class Common {

    private static Common common = null;
    private Solo solo = null;

    public static Common getInstance(){
        if(common == null){
            common = new Common();
        }
        return common;
    }

    public void init(Solo solo){
        this.solo = solo;
    }

    /**
     * click view by id
     * @param strID String
     * @param iTime int
     * @return int
     */
    public int clickCtrlById(String strID, int iTime){

        int ctrl;
        View v;
        //Log.e("clickCtrlById", "clickCtrlById0");
        if(strID.equals("")){
            return -1;
        }
        Log.e("clickCtrlById", "clickCtrlById1");
        ctrl = solo.getCurrentActivity().getResources().getIdentifier(strID, "id", solo.getCurrentActivity().getPackageName());
        Log.e("clickCtrlById", Integer.toString(ctrl));
        v = solo.getView(ctrl);

        solo.clickOnView(v);

        if(iTime != 0){
            solo.sleep(iTime);
        }

        return 0;
    }

    public boolean showView(String strID){
        return solo.waitForView(solo.getView(strID));
    }

    /**
     *
     * @param strID String
     * @return View
     */
    public View getView(String strID){
        int ctrl;

        if(strID.equals("")){
            return null;
        }
        //Log.e("clickCtrlById", "clickCtrlById1");
        ctrl = solo.getCurrentActivity().getResources().getIdentifier(strID, "id", solo.getCurrentActivity().getPackageName());
        //Log.e("clickCtrlById", Integer.toString(ctrl));

        return solo.getView(ctrl);
    }

    public int getCtrlId(String strID){

        int ctrl;
        if(strID.equals("")){
            return -1;
        }
        ctrl = solo.getCurrentActivity().getResources().getIdentifier(strID, "id", solo.getCurrentActivity().getPackageName());
        Log.i("getCtrlId", Integer.toString(ctrl));

        return ctrl;
    }

    /**
     * Enter text to view by id
     * @param strID String
     * @param s String
     * @param iTime int
     * @return int
     */

    public int enterTextById(String strID, String s, int iTime ){

        int ctrl;
        EditText v;

        if(s.equals("") || strID.equals("")){
            return -1;
        }

        ctrl = solo.getCurrentActivity().getResources().getIdentifier(strID, "id",solo.getCurrentActivity().getPackageName());
        v = (EditText) solo.getView(ctrl);

        solo.clearEditText(v);
        solo.enterText(v, s) ;

        if(iTime != 0){
            solo.sleep(iTime);
        }

        return 0;
    }

    /**
     * get string of Toast
     * @return String
     */
    public String getToast(int timeout){
        TextView toastTextView;
        String toastText = "";
        long endTime = SystemClock.uptimeMillis() + timeout;
        while(SystemClock.uptimeMillis() < endTime){
            toastTextView = (TextView)solo.getView("message", 0);
            if(null != toastTextView){
                toastText = toastTextView.getText().toString();
                break;
            }
        }

        return toastText;
    }

    public void logout(int iSleep){

        Common.getInstance().clickCtrlById("right", iSleep);
        int i = 0;
        String strUserName;
        do {
            TextView textView = (TextView)solo.getView("username");
            strUserName = textView.getText().toString();
            solo.sleep(1000);
            i++;
            if(i > 20){
                Log.e("AutoTest—logout", "(AutoTest)Can not return personal center!!!");
                return;
            }
        }while (strUserName.length() == 0);

        Common.getInstance().clickCtrlById("shipping_address", iSleep);
        if(!solo.waitForText(PropertyData.getProperty("MyInformation"))){
            Log.e("AutoTest—logout", "(AutoTest)Can not return my information!!!");
            return;
        }
        Common.getInstance().clickCtrlById("exitLogin", iSleep);
        solo.sleep(1000);
        solo.clickOnButton("确定");
        if(!solo.waitForText(PropertyData.getProperty("Welcome_Regi"))){
            Log.e("AutoTest—logout", "(AutoTest)Can not return my information!!!");
        }
    }

    public void goBack(int iNumber, int iTime){
        for (int i = 0; i < iNumber; i++){
            solo.goBack();
            solo.sleep(iTime);
        }
    }

    /**
     * get current time
     * @return String
     */
    public String getCurTime(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public String getTimeDiff(String strBegin, String strEnd){
        String strTimeDiff;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long day = 0;
        long hour = 0;
        long min = 0;
        long sec = 0;

        try{
            Date dateBegin = sdf.parse(strBegin);
            Date dateEnd = sdf.parse(strEnd);
            long between = dateEnd.getTime() - dateBegin.getTime();
            day = between/(24 * 60 * 60 * 1000);
            hour = (between/(60 * 60 * 1000) - day * 24);
            min = ((between/(60*1000)) - day*24*60  -hour*60);
            sec = (between/1000 - day*24*60*60 - hour*60*60 - min*60);
        }catch (ParseException e){
            e.printStackTrace();
        }
        strTimeDiff = day + "day-" + hour + "hour-" + min + "min-" + sec + "sec";

        return strTimeDiff;
    }

}
