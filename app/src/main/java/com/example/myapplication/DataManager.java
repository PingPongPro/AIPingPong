package com.example.myapplication;


public class DataManager {
    //传入字符串处理
    private int STRING_TIMES = 0;//字符串计数\

    private double ACCELCONSTANT = 4096;
    private double GRYOCONSTANT = 16.4;

    public double ACCL_X = 0;
    public double ACCL_Y = 0;
    public double ACCL_Z = 0;
    public double GRYO_X = 0;
    public double GRYO_Y = 0;
    public double GRYO_Z = 0;

    public String fullData = null;//构成完整字符串
    //传入字符串处理变量到此结束

    public void DataManage(String data){
        if(STRING_TIMES == 0){
            STRING_TIMES = 1;
            fullData = data;
        }
        else if(STRING_TIMES == 1){
            STRING_TIMES = 0;
            fullData += data;
            for(int i = 2; i < 14; i = i + 4) {
                String tmp = "";
                int tmpInt = 0;
                for(int j = 0; j < 4; j++){
                    tmp += fullData.charAt(i + j);
                }
                try {
                    tmpInt = (short)Integer.parseInt(tmp,16);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(i == 2){
                    ACCL_X = tmpInt/ACCELCONSTANT;
                }
                else if(i == 6){
                    ACCL_Y = tmpInt/ACCELCONSTANT;
                }
                else if(i == 10){
                    ACCL_Z = tmpInt/ACCELCONSTANT;
                }
            }
            for(int i = 14; i < 26; i = i + 4) {
                String tmp = "";
                int tmpInt = 0;
                for(int j = 0; j < 4; j++) {
                    tmp += fullData.charAt(i + j);
                }
                try {
                    tmpInt = (short)Integer.parseInt(tmp,16);
                }catch (Exception e){
                    e.printStackTrace();
                }
                if(i  == 14){
                    GRYO_X = tmpInt/GRYOCONSTANT;
                }
                else if(i == 18){
                    GRYO_Y = tmpInt/GRYOCONSTANT;
                }
                else if(i == 22){
                    GRYO_Z = tmpInt/GRYOCONSTANT;
                }
            }
        }
    }
}
