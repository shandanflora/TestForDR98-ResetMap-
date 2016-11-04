package com.ecovacs.ecosphere;

import android.provider.ContactsContract;
import android.util.Log;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.view.View;

import com.ecovacs.ecosphere.common.Common;
import com.ecovacs.ecosphere.common.PropertyData;
import com.robotium.solo.Solo;

import java.util.ArrayList;

/**
 * Created by lily.shan on 2016/10/25.
 * DR98
 */
public class DR98Clean {

    private static DR98Clean dr98Clean = null;
    private Solo solo = null;

    enum MAP_TYPE {
        CREATE_MAP,
        RESET_MAP
    }

    private DR98Clean(){

    }

    public static DR98Clean getInstance(){
        if(dr98Clean == null){
            dr98Clean = new DR98Clean();
        }
        return dr98Clean;
    }

    public void init(Solo solo){
        this.solo = solo;
    }

    public boolean login(){
        if(!Common.getInstance().showView("login")){
            Log.e("AutoTest", "(login)Not show Welcome Activity!!!");
            return false;
        }
        Common.getInstance().clickCtrlById("login", 100);
        if(!Common.getInstance().showView("username")){
            Log.e("AutoTest", "(login)Not show Login Activity!!!");
            return false;
        }
        Common.getInstance().enterTextById("username", PropertyData.getProperty("login_phone"), 100);
        Common.getInstance().enterTextById("password", PropertyData.getProperty("login_pwd"), 100);
        Common.getInstance().clickCtrlById("login", 1000);
        if (solo.searchText(PropertyData.getProperty("main_update"))){
            Log.i("AutoTest", "find new version!!!");
            solo.clickOnButton(PropertyData.getProperty("ConfirmCancel"));
        }
        return Common.getInstance().showView("right");
    }

    public boolean logout(){
        //must return to MainPage Activity
        Common.getInstance().clickCtrlById("right", 100);
        Common.getInstance().clickCtrlById("shipping_address", 100);
        Common.getInstance().clickCtrlById("exitLogin", 100);

        solo.clickOnButton(PropertyData.getProperty("ConfirmTrue"));
        return Common.getInstance().showView("login");
    }

    private MAP_TYPE getBuildMapType(){
        MAP_TYPE mapType;
        if(solo.waitForText(PropertyData.getProperty("deebot_createMap"), 0, 3000, false, true)){
            //Log.i("AutoTest", "1.(getBuildMapType)create_map");
            mapType = MAP_TYPE.CREATE_MAP;
        }else {
            //Log.i("AutoTest", "2.(getBuildMapType)reset_map");
            mapType = MAP_TYPE.RESET_MAP;
        }
        return mapType;
    }

    private int getDR98OnLineIndex(){
        int iIndex = -1;
        ArrayList<TextView> textViews = solo.getCurrentViews(TextView.class, solo.getView("listView_device"));
        //Log.i("AutoTest", "(getDR98OnLineIndex)1.The size of View: " + Integer.toString(textViews.size()));
        int iSize = textViews.size();
        //Log.i("AutoTest", "(getDR98OnLineIndex)2.The size of TextView: " + Integer.toString(iSize));
        for (int i = 0; i < iSize; i++) {
            if (textViews.get(i).getText().equals(PropertyData.getProperty("main_device"))) {
                //Log.i("AutoTest", "(getDR98OnLineIndex)3.The text of TextView: " + textViews.get(i).getText());
                //Log.i("AutoTest", "(getDR98OnLineIndex)4.The text of TextView: " + textViews.get(i + 1).getText());
                if (textViews.get(i + 1).getText().equals(PropertyData.getProperty("main_onLine"))) {
                    iIndex = i;
                    break;
                }
            }
        }
        return iIndex;
    }

    private boolean showDeebotCleanActivity(){
        int iIndex = getDR98OnLineIndex();
        Log.i("AutoTest", "(showDeebotCleanActivity)The index of DR98: " + Integer.toString(iIndex));
        if(-1 == iIndex){
            Log.e("AutoTest", "(showDeebotCleanActivity)Not find DR98 or DR98 not online!!!");
            return false;
        }
        ListView listView = (ListView) solo.getView("listView_device");
        ArrayList<TextView> listTextViews = solo.getCurrentViews(TextView.class, listView);
        solo.clickOnView(listTextViews.get(iIndex));
        solo.sleep(1000);

        return Common.getInstance().showView("eco_action_more");
    }

    public boolean buildMap(String strNum){
        boolean bResult = false;
        //first
        if(Integer.parseInt(strNum) == 1){
            if(!showDeebotCleanActivity()){
                Log.e("AutoTest", "(buildMap)Not show Deebot clean Activity!!!");
                return false;
            }
            Log.i("AutoTest", "(buildMap)Show Deebot clean Activity!!!");
        }
        MAP_TYPE map_type = getBuildMapType();
        switch (map_type){
            case CREATE_MAP:
                Log.i("AutoTest", "(buildMap)create map!!!");
                bResult = createMap(strNum);
                break;
            case RESET_MAP:
                Log.i("AutoTest", "(buildMap)reset map!!!");
                bResult = resetMap(strNum);
                break;
            default:
                break;
        }
        return bResult;
    }

    private boolean createMap(String strNum){
        boolean bCreate = solo.getView("create_map").isShown();
        while (!bCreate) {
            solo.sleep(1000);
            Log.i("AutoTest", "Show create map button(loop): " + Boolean.toString(bCreate));
            bCreate = solo.getView("create_map").isShown();
        }
        Log.i("AutoTest", "Show create map button: " + Boolean.toString(bCreate));
        solo.sleep(1000);
        solo.clickOnView(solo.getView("create_map"));
        //Common.getInstance().clickCtrlById("create_map", 1000);
        //begin to create map
        isBuildingMap();
        Log.i("AutoTest", "(createMap)Begin to create map!!!");
        String strBegin = Common.getInstance().getCurTime();
        while (!isCompletedBuildMap()){
            solo.sleep(1000);
        }
        Log.i("AutoTest", "(createMap)create map completed!!!");
        //get current time
        String strEnd = Common.getInstance().getCurTime();
        String strTimeDiff = Common.getInstance().getTimeDiff(strBegin, strEnd);
        Log.i("AutoTest", "(createMap)build map cost time: " + strTimeDiff);
        //on charge
        onCharge();
        //charging
        isCharging();
        //screenshot
        screenShot(strNum);
        String strEndReturnChar = Common.getInstance().getCurTime();
        String strTimeDiff2 = Common.getInstance().getTimeDiff(strEnd, strEndReturnChar);
        Log.i("AutoTest", "(createMap)return charge cost time: " + strTimeDiff2);
        //charge complete
        isCharged();
        return true;
    }

    private void screenShot(String strNum){
        Common.getInstance().clickCtrlById("goto_full_screen", 4000);
        solo.takeScreenshot("createMap" + strNum);
        //solo.sleep(1000);
        Log.i("AutoTest", "(createMap)screenshot completed!!!");
        Common.getInstance().goBack(1, 200);
    }

    private void onCharge(){
        //TextView textView = (TextView)solo.getView("deebot_statues");
        Log.i("AutoTest", "(onCharge)Ready to return charge!!!");
        ImageView imageView = (ImageView)solo.getView("deebot_auto_image");
        while (!imageView.isSelected()){
            solo.sleep(1000);
        }
        Common.getInstance().clickCtrlById("deebot_return_image", 1000);
        solo.clickOnButton(PropertyData.getProperty("ConfirmTrue"));
        Log.i("AutoTest", "(onCharge)click return charge!!!");
    }

    private void isBuildingMap(){
        TextView textView = (TextView)solo.getView("deebot_statues");
        while (!textView.getText().toString().equals(PropertyData.getProperty("main_buildMap"))){
            solo.sleep(1000);
            //Log.i("AutoTest", "(isCharged)The battery is: " + textView.getText().toString());
        }
        Log.i("AutoTest", "(isBuildingMap)The status is:" + textView.getText().toString());
    }

    private void isCharging(){
        TextView textView = (TextView)solo.getView("deebot_statues");
        Log.i("AutoTest", "(isCharged)The battery is return charge!!!");
        while (!textView.getText().toString().equals(PropertyData.getProperty("main_charging"))){
            solo.sleep(1000);
            //Log.i("AutoTest", "(isCharged)The battery is: " + textView.getText().toString());
        }
        Log.i("AutoTest", "(isCharged)The status is:" + textView.getText().toString());
    }

    private void isCharged(){
        TextView textView = (TextView)solo.getView("deebot_battery_statues");
        //Not charge while battery over 30%
        String strPer = textView.getText().toString();
        strPer = strPer.substring(0, strPer.length() - 1);
        if(Integer.parseInt(strPer) > 40){
            Log.i("AutoTest", "(isCharged)The battery is: " + strPer + "%!!!");
            return;
        }
        Log.i("AutoTest", "(isCharged)The battery is charging!!!");
        while (!textView.getText().toString().equals(PropertyData.getProperty("main_battery"))){
            solo.sleep(1000);
            //Log.i("AutoTest", "(isCharged)The battery is: " + textView.getText().toString());
        }
        Log.i("AutoTest", "(isCharged)The battery is 100%!!!");
    }

    private boolean showSettingActivity(){
        Common.getInstance().clickCtrlById("eco_action_more", 500);
        return solo.waitForText(PropertyData.getProperty("setting_title"));
    }

    private boolean showResetActivity(){
        ArrayList<TextView> textViews = solo.getCurrentViews(TextView.class, solo.getView("content"));
        for(TextView textView: textViews){
            if(textView.getText().equals(PropertyData.getProperty("setting_resetMap"))){
                solo.clickLongOnView(textView);
            }
        }
        return solo.waitForText(PropertyData.getProperty("reset_confirm"));
    }

    private boolean isCompletedBuildMap(){
        ImageView imageViewStop = (ImageView)solo.getView("stop_build_map");
        boolean bStop = imageViewStop.isShown();
        //Log.i("AutoTest", "1.(buildMap)show stop button: " + Boolean.toString(!bStop));
        ImageView imageViewFull = (ImageView)solo.getView("goto_full_screen");
        boolean bFull = imageViewFull.isShown();
        //Log.i("AutoTest", "2.(buildMap)show full screen button: " + Boolean.toString(bFull));
        return !bStop && bFull;
    }

    private boolean resetMap(String strNum){
        if(!showSettingActivity()){
            Log.e("AutoTest", "(resetMap)Not show setting activity!!!");
            return false;
        }
        Log.i("AutoTest", "(resetMap)Show setting activity!!!");
        if(!showResetActivity()){
            Log.e("AutoTest", "(resetMap)Not show reset map activity!!!");
            return false;
        }
        Log.i("AutoTest", "(resetMap)Show reset map activity!!!");
        //click button
        Common.getInstance().clickCtrlById("reset_consumables", 200);
        //confirm
        solo.clickOnButton(PropertyData.getProperty("reset_confirmReset"));
        //solo.sleep(1500);
        //begin build map
        return createMap(strNum);
    }

}
