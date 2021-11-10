package com.topelec.rfidcontrol;

import android.os.Bundle;
import android.os.ConditionVariable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TableRow;

import com.topelec.zigbeecontrol.Command;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import realarm.hardware.HardwareControl;

/**
 * RFID模块控制类
 * Created by Amber on 2015/3/31.
 */
public class ModulesControl {


    //最大接受的数据字节数
    private static final int MAX_RECEIVE_DATA_LEN = 256;
    //最大发送的数据字节数
    private static final int MAX_SEND_DATA_LEN = 256;


    private static final String TAG = ".ModulesControl";
    private static ModulesControl mInstance;

    private Command mCommand;

    private File device;
    private FileDescriptor mFd;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;


    private HFReadThread mHFReadThread;
    private HFWriteThread mHFWriteThread;

    private LFReadThread mLFReadThread;
    private LFWriteThread mLFWriteThread;
    private UHFReadThread mUHFReadThread;
    private UHFWriteThread mUHFWriteThread;

    private boolean isUartOpened = false;
    private int baudrate = 0;

    private String lfPassword = new String();

    private  ConditionVariable cv =  new ConditionVariable(false);
    private  ConditionVariable lfPasswordCv = new ConditionVariable(false);

    private int counter = 10;
    private boolean FrequencyOpened = false;

    //UI更新同步handler
    private Handler uiHandler;


    String  rfidType;// "LF";"HF";"UHF"

    public ModulesControl(Handler updateHandler) {
        uiHandler = updateHandler;

        //初始化串口
        try {
            initUart("HF",null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
//    public static ModulesControl getInstance(Handler updateHandler) {
//        if (null == mInstance) {
//            mInstance = new ModulesControl(updateHandler);
//        }
//        return mInstance;
//    }

    /**
     * 线程控制
     */
    public void actionControl(boolean isOpen) {
        if (isOpen) {

            if (mHFReadThread == null)
                mHFReadThread = new HFReadThread();
            if (mHFWriteThread == null)
                mHFWriteThread = new HFWriteThread();

            mHFReadThread.start();
            mHFWriteThread.start();
        }else {
//            mHFWriteThread.finishRunning();
//            mHFReadThread.finishRunning();
            if (mHFWriteThread != null)
                mHFWriteThread.interrupt();
            if (mHFReadThread != null)
                mHFReadThread.interrupt();

        }
    }

    public void closeSerialDevice() {
        HardwareControl.CloseSerialPort();
        isUartOpened = false;
    }

    /**
     * 串口初始化函数
     * @throws java.io.IOException
     */
    private void initUart(String RfidType,String Password) throws IOException
    {
        String rfidPort = new String();
        if (RfidType.equals("LF")) {

            if (baudrate == Command.lfBautrate) {
                return;
            } else {
                HardwareControl.CloseSerialPort();
                isUartOpened = false;
                lfPassword = Password;
                rfidPort = Command.lfRfidPort;
                baudrate = Command.lfBautrate;
            }

        }else if (RfidType.equals("HF")) {

            if (baudrate == Command.hfBautrate) {
                return;
            } else {
                HardwareControl.CloseSerialPort();
                isUartOpened = false;
                rfidPort = Command.hfRfidPort;
                baudrate = Command.hfBautrate;
            }

        } else if (RfidType.equals("UHF")) {
            if (baudrate == Command.uhfBautrate) {
                return;
            } else {
                HardwareControl.CloseSerialPort();
                isUartOpened = false;
                rfidPort = Command.uhfRfidPort;
                baudrate = Command.uhfBautrate;
            }

        } else {
            return;
        }


        //检验串口是否可读写，如果不可读写，授权串口可读写
        device=new File(rfidPort);

			/* Check access permission */
        if (!device.canRead() || !device.canWrite()) {
            try {
					/* Missing read/write permission, trying to chmod the file */
                Process su;
                su = Runtime.getRuntime().exec("/system/bin/su");
                String cmd = "chmod 666 " + device.getAbsolutePath() + "\n"
                        + "exit\n";
                su.getOutputStream().write(cmd.getBytes());
                if ((su.waitFor() != 0) || !device.canRead()
                        || !device.canWrite()) {
                    throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SecurityException();
            }
        }


        //打开串口
        mFd = HardwareControl.OpenSerialPort(device.getAbsolutePath(), baudrate,0);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }
        else{
            mInputStream = new FileInputStream(mFd);
            mOutputStream = new FileOutputStream(mFd);
            isUartOpened = true;
        }

    }

    /**
     * 关闭所有RFID工作
     */
    public void closeRFID() {

        if (isUartOpened) {
            HardwareControl.CloseSerialPort();
            isUartOpened = false;
            baudrate = 0;
        }

        if (mLFWriteThread != null)
            mLFWriteThread.interrupt();
        if (mLFReadThread != null)
            mLFReadThread.interrupt();

        if (mHFWriteThread != null)
            mHFWriteThread.interrupt();
        if (mHFReadThread != null)
            mHFReadThread.interrupt();

        if (mUHFWriteThread != null)
            mUHFWriteThread.interrupt();

        if (mUHFReadThread != null)
            mUHFReadThread.interrupt();

    }
//    /**
//     * 中断线程，结束循环
//     */
//    public void interruptThread() {
//        if (mHFWriteThread != null)
//            mHFWriteThread.interrupt();
//        if (mHFReadThread != null)
//            mHFReadThread.interrupt();
//    }
    /**
     * 启动低频模块
     * @param password
     */
    public void startLfRfid(String password){

        /**
         * 关闭高频模块工作线程
         */
        if (mHFWriteThread != null)
            mHFWriteThread.interrupt();
        if (mHFReadThread != null)
            mHFReadThread.interrupt();

        /**
         * 关闭超高频模块工作线程
         */
        if (mUHFWriteThread != null)
            mUHFWriteThread.interrupt();

        if (mUHFReadThread != null)
            mUHFReadThread.interrupt();

        //初始化低频模块串口及工作线程
        try {
            initUart("LF",password);
        } catch (IOException e) {
            e.printStackTrace();
        }

        mLFReadThread = new LFReadThread();
        mLFReadThread.start();

        mLFWriteThread = new LFWriteThread();
        mLFWriteThread.start();
    }

    public void startHFRfid() {
        if (mLFWriteThread != null)
            mLFWriteThread.interrupt();
        if (mLFReadThread != null)
            mLFReadThread.interrupt();

        if (mUHFWriteThread != null)
            mUHFWriteThread.interrupt();
        if (mUHFReadThread != null)
            mUHFReadThread.interrupt();

        try {
            initUart("HF",null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mHFReadThread = new HFReadThread();
        mHFReadThread.start();

        mHFWriteThread = new HFWriteThread();
        mHFWriteThread.start();
    }

    public void startUHFRfid() {

        if (mLFWriteThread != null)
            mLFWriteThread.interrupt();
        if (mLFReadThread != null)
            mLFReadThread.interrupt();

        /**
         * 关闭超高频模块工作线程
         */
        if (mHFWriteThread != null)
            mHFWriteThread.interrupt();

        if (mHFReadThread != null)
            mHFReadThread.interrupt();


        try {
            initUart("UHF",null);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mUHFReadThread = new UHFReadThread();
        mUHFReadThread.start();
        mUHFWriteThread = new UHFWriteThread();
        mUHFWriteThread.start();
    }

    /**
     * 十六进制字符串转byte数组
     * @param hexString
     * @return
     */
    public static byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++) {
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),16).byteValue();
        }
        return result;
    }
    /**
     * 十六进制byte数组转String  for debug
     * @param buf
     * @return
     */
    public static String toHex(byte[] buf) {
        if (buf == null)
            return "";
        StringBuffer result = new StringBuffer(2 * buf.length);
        for (int i = 0; i < buf.length; i++) {
            appendHex(result, buf[i]);
        }
        return result.toString();
    }

    private final static String HEX = "0123456789ABCDEF";
    private static void appendHex(StringBuffer sb, byte b) {
        sb.append(HEX.charAt((b >> 4) & 0x0f)).append(HEX.charAt(b & 0x0f));
    }


    /**
     * 高频RFID模块，组合整理发送对应的数据指令
     * @param pSendData       输入指令中的 【命令字】+【数据域】
     * @param DataLen         输入指令中的 【命令字】+【数据域】 长度
     * @return                 是否发送成功
     */
    private void SendData(byte[] pSendData, int DataLen) {
        //byte pSendDataTemp[MAX_SEND_DATA_LEN] = { 0x02, 0x00, 0x00, (byte)(DataLen + 2) };
        //定义发送指令数组，并初始化
        byte[] pSendDataTemp = new byte[MAX_SEND_DATA_LEN];
        pSendDataTemp[0] = 0x02;
        pSendDataTemp[1] = 0x00;
        pSendDataTemp[2] = 0x00;
        pSendDataTemp[3] = (byte)(DataLen + 2);

        byte checkSum = (byte) (DataLen + 2);

        //重新组织数据
        int TxLen = 4;
        for (int i = 0; i < DataLen; i++) {
            if (pSendData[i] == 0x02 || pSendData[i] == 0x03 || pSendData[i] == 0x10) {
                pSendDataTemp[TxLen++] = 0x10;
            };
            pSendDataTemp[TxLen++] = pSendData[i];
            checkSum += pSendData[i];
        }

        //添加校验和及帧尾
        pSendDataTemp[TxLen++] = checkSum;
        pSendDataTemp[TxLen++] = 0x03;

        //TODO:发送pSendDataTemp到串口
        try {
            mOutputStream.write(pSendDataTemp);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @return 操作成功，返回true；操作失败，返回false；
     */
    private void  m104bpc_setUpRfidTypeA() {

        byte[] pSendData = new byte[MAX_SEND_DATA_LEN];
        pSendData[0] = 0x3A;
        pSendData[1] = 0x41;
        int nSendLen = 2;
        SendData(pSendData,nSendLen);
    }


    /**
     * 射频控制,设置天线状态
     * @param open  true——打开；false——关闭
     * @return  操作成功，返回true；操作失败，返回false；
     */
    private void m104bpc_FrequencyCtrl(boolean open) {
//        counter = 3;
//        isReturnedValue = false;
        FrequencyOpened = open;
        //准备待发送数据
        byte [] pSendData = new byte[MAX_SEND_DATA_LEN];
        pSendData[0] = 0x05;
        pSendData[1] = (open == true) ? (byte)0x01 : (byte)0x00;

        int nSendLen = 2;
        SendData(pSendData,nSendLen);
    }

    /**
     * 激活卡片（寻卡）
     * @param activeAll true——寻天线范围内的所有卡
     *                    false——寻未进入睡眠状态的卡
     */
    private void m104bpc_ActiveCard(boolean activeAll) {
        //准备待发送数据
        byte[] pSendData = new byte[MAX_SEND_DATA_LEN];
        pSendData[0] = 0x46;
        pSendData[1] = (activeAll == true) ? (byte)0x52 : (byte)0x26;

        int nSendLen = 2;
        SendData(pSendData,nSendLen);
    }

    /**
     * 防冲突（获取卡号）
     */
    private void m104bpc_Anticollision() {
        //准备待发送数据
        byte[] pSendData = new byte[MAX_SEND_DATA_LEN];
        pSendData[0] = 0x47;
        pSendData[1] = 0x04;
        int nSendLen = 2;

        SendData(pSendData,nSendLen);
    }

    /**
     * 低频RFID模块在password mode下，验证密码。
     * @param password
     */
    private void LF_Setup_Password_Mode(byte[] password) {

        int length = Command.set_password_mode.length + password.length;
        byte[] cmd = new byte[length];

        System.arraycopy(Command.set_password_mode.length,0,cmd,0,Command.set_password_mode.length);
        System.arraycopy(password,0,cmd,Command.set_password_mode.length,password.length);

        try {
            mOutputStream.write(cmd);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 低频模块读取Page One数据，直接调用即可
     */
    private void LF_Read_Page_One() {
        try {
            mOutputStream.write(Command.read_page_one);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 特高频模块读取ID号,直接调用即可。
     */
    private void UHF_Read_ID() {
        try {
            mOutputStream.write(Command.read_id_once);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    /**
     * 低频RFID模块写线程
     */
    private class LFWriteThread extends Thread {

        int flag = 0;
        boolean isOver = false;
        public void finishRunning() {
            isOver = true;
        }

        private void verifyPassword() {
            int counter = 3;
            if (lfPassword == null || lfPassword.length() == 0) {

                Log.v(TAG,"password is null");
                return;
            }

            while (counter-- > 0) {
                LF_Setup_Password_Mode(toByte(lfPassword));
                if (lfPasswordCv.block(500)) {
                    //成功返回数据被open()唤醒
                    break;
                } else {
                    //超时唤醒
                    Message msg = new Message();
                    Bundle data = new Bundle();
                    msg.what = Command.LF_PASSWORD;
                    data.getBoolean("password",false);
                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                    mLFReadThread.interrupt();
                    Thread.currentThread().interrupt();//终端线程
                }
            }
        }
        @Override
        public void run() {

            super.run();
            while (isOver == false) {

                switch (flag) {
                    case 0:
                        verifyPassword();
                        flag++;
                        break;
                    case 1:
                        LF_Read_Page_One();
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            Thread.currentThread().interrupt();
                        }
                        break;
                    default:
                        break;
                }
            }
        }
    }

    /**
     * 低频RFID模块读线程
     */
    private class LFReadThread extends Thread {
        boolean isOver = false;

        public void finishRunning() {
            isOver = true;
        }

        @Override
        public void run() {
            super.run();
            int size;

            while(isOver == false) {

                try {
                    byte[] buffer = new byte[MAX_RECEIVE_DATA_LEN];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    // Log.d(TAG,"SIZE = "+size+"");
                    if (size > 0) {

                        // Log.d(TAG,toHex(buffer));
                        for (int i = 0; i < size; i++) {
                            Message msg = new Message();
                            Bundle data = new Bundle();
                            switch (buffer[i]) {
                                case (byte) 0xE0:
                                    if (buffer[i+1] == 0x15 && buffer[i+2] == 0x0A && buffer[i+8] == 0x02) {
                                        byte[] lfCardId = new byte[5];
                                        System.arraycopy(buffer,i+3,lfCardId,0,5);
                                        i = i+9;
                                        //todo:通知督导的卡号
                                        msg.what = Command.LF_ID;
                                        data.putString("lfCardId",toHex(lfCardId));
                                        msg.setData(data);
                                        uiHandler.sendMessage(msg);
                                    }
                                    break;
                                case 0x00:
                                    if (buffer[i+1] == 0x08 && buffer[i+2] == 0x80 && buffer[i+3] == 0xD8) {
                                        //todo:通知验证密码正确
                                        lfPasswordCv.open();
                                        msg.what = Command.LF_PASSWORD;
                                        data.putBoolean("password",true);
                                        msg.setData(data);
                                        uiHandler.sendMessage(msg);
                                    }
                                    break;
                                default:
                                    break;

                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    /**
     * 高频RFID模块写线程
     * 读卡号流程：设置卡片类型TypeA——>打开天线——>（寻卡——>防冲突 【区间内循环查询】）
     * 关闭：关闭（寻卡——>防冲突 【区间内循环查询】）——>关闭天线
     */
    private class HFWriteThread extends Thread {

        int flag = 0;
        boolean isOver = false;

        public void initHFRfid() {

            Message msg = new Message();
            Bundle data = new Bundle();
            while (counter-- > 0) {
                m104bpc_setUpRfidTypeA();
                if (cv.block(500)) {   //设置类型；true if the condition was opened
                    //TODO：成功设置——1）重置计数器；2）执行打开射频动作

                    while (counter-- > 0) {
                        m104bpc_FrequencyCtrl(true);
                        if (cv.block(500)) { //打开天线
                            //TODO:成功打开天线——1）重置计数器；2）继续执行
                            //suspend = false;
                            break;
                        } else {
                            //TODO:打开不成功——1）停止线程；2）抛错误信息到UI
                            if (counter == 0) {
                                msg.what = Command.HF_FREQ;
                                data.putBoolean("result",false);
                                msg.setData(data);
                                uiHandler.sendMessage(msg);
                                mHFReadThread.interrupt();
                                Thread.currentThread().interrupt();//中断线程
                            }
                        }
                    }
                    break;  //停止循环发送TYPE_A
                } else {              //false if the call returns because of the timeout.
                    //TODO：设置不成功——1）停止线程；2）抛错误信息到UI
                    if (counter == 0) {
                        msg.what = Command.HF_TYPE;
                        data.putBoolean("result",false);
                        msg.setData(data);
                        uiHandler.sendMessage(msg);
                        Thread.currentThread().interrupt();//终端线程
                    }
//                    Log.v(TAG,"TYPE A FAIL");
                }
            }
        }

        public void finishRunning() {
            isOver = true;
        }
        @Override
        public void run() {

            super.run();

//            while (isOver == false) {
            while (!Thread.currentThread().isInterrupted()) {

                switch (flag) {
                    case 0://初始化(TYPE_A + 打开射频)
                        initHFRfid();
                        flag++;
                        break;
                    case 1://寻卡
                        m104bpc_ActiveCard(true);
                        flag++;
                        cv.block(1000);
                        break;
                    case 2://防冲突
                        m104bpc_Anticollision();
                        flag = 1;
                        try {
                            sleep(500);
                        } catch (InterruptedException e) {
                            Thread.currentThread().interrupt();
                            e.printStackTrace();
                        }
                        break;
                    default:
                        break;
                }

            }
        }
    }



    /**
     * 高频RFID读线程
     */
    private class HFReadThread extends Thread {

        boolean isOver = false;

        public void finishRunning() {
            isOver = true;
        }
        /**
         *
         * @param whichCmd
         * @param g_ReceiveData
         */
        private void AnalyzeHFCmd(int whichCmd,byte[] g_ReceiveData) {

            Message msg = new Message();
            Bundle data = new Bundle();


            switch (whichCmd) {
                case 0x3A:    //设置卡片类型成功，TypeA
                    msg.what = Command.HF_TYPE;
                    if (g_ReceiveData[0] == 0x00){
                        //TODO 设置卡片类型成功，发送打开射频指令
//                        data.putBoolean("result",true);
                        counter = 3;
                        cv.open();
                    } else {
                        //TODO 错误处理
//                        data.putBoolean("result",false);
                    }
//                    msg.setData(data);
//                    syncHandler.sendMessage(msg);
                    break;
                case 0x05:     //射频控制（打开或者关闭）成功
                    msg.what = Command.HF_FREQ;
                    if (g_ReceiveData[0] == 0x00) {
                        //TODO 打开天线或者关闭天线成功
//                        data.putBoolean("result",true);
                        counter = 3;
                        cv.open();

                    }else {
                        //TODO 错误处理
//                        data.putBoolean("result",false);

                    }

                    break;
                case 0x46:       //激活卡片，寻卡，成功
                    msg.what = Command.HF_ACTIVE;
                    if (g_ReceiveData[0] == 0x00) {
                        //TODO:成功激活卡片
                        data.putBoolean("result",true);
                    } else {
                        //TODO:错误处理
                        data.putBoolean("result",false);

                    }

                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                    break;
                case 0x47:      //防冲突（获取卡号）成功
                    msg.what = Command.HF_ID;
                    if (g_ReceiveData[0] == 0x00) {
                        //TODO:数据1-4位为卡号，更新UI并显示
                        byte[] newData = new byte[4];
                        System.arraycopy(g_ReceiveData,1,newData,0,4);
                        data.putBoolean("result",true);

                        data.putString("cardNo",toHex(newData));
//                         Log.d(TAG,"CardNo = "+ toHex(newData));
                    } else {
                        //TODO：错误处理
                        data.putBoolean("result",false);

                    }

                    msg.setData(data);
                    uiHandler.sendMessage(msg);
                    break;
            }
        }


        @Override
        public void run() {
            super.run();
            int size;
            int flag = 0;

            //记录是否出现转义符
            boolean escape = false;
            //校验和
            byte checkSum = 0;
            // 已接收的数据长度（执行结果 + 数据域）
            int dataReceived = 0;
            //返回数据处理状态
            int g_nReceiveStatus = 0;
            //返回数据长度
            int g_nReceiveDataLen = 0;
            //返回数据（执行结果+数据域）
            byte[] g_ReceiveData = new byte[MAX_RECEIVE_DATA_LEN];
            //记录返回指令类型
            byte whichCmd = 0;

//            while(isOver == false) {
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    byte[] buffer = new byte[MAX_RECEIVE_DATA_LEN];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    // Log.d(TAG,"SIZE = "+size+"");
                    if (size > 0) {

                        // Log.d(TAG,toHex(buffer));
                        for (int i = 0; i < size; i++) {   //每一个循环检测字节，先过滤转义符，再处理字符含义

                            //处理转义字符
                            switch (buffer[i]) {
                                case 0x02:
                                    if (escape) {
                                        escape = false;
                                    } else {
                                        g_nReceiveStatus = 1;
                                        g_nReceiveDataLen = 0;
                                        checkSum = 0;
                                        dataReceived = 0;
                                        continue;
                                    }
                                    break;
                                case 0x03:
                                    if (escape) {
                                        escape = false;
                                    }
                                    break;
                                case 0x10:
                                    if (escape) {
                                        escape = false;
                                    } else {
                                        escape = true;
                                        continue;
                                    }
                                    break;
                            }


                            //处理接收的数据
                            switch (g_nReceiveStatus) {
                                case 0:       //等待帧头
                                    break;
                                case 1:       //模块地址（忽略）
                                case 2:
                                    checkSum += (byte)buffer[i];
                                    g_nReceiveStatus++;
                                    break;
                                case 3:       //长度
                                    g_nReceiveDataLen = buffer[i] - 2;
                                    checkSum += (byte)buffer[i];
                                    g_nReceiveStatus++;
                                    break;
                                case 4:       //命令（忽略）
                                    whichCmd = (byte)buffer[i];
                                    checkSum += (byte)buffer[i];
                                    g_nReceiveStatus++;
                                    break;
                                case 5:       // 数据（执行结果 + 数据域）
                                    g_ReceiveData[dataReceived++] = buffer[i];
                                    checkSum += (byte)buffer[i];
                                    if (dataReceived >= g_nReceiveDataLen) {
                                        g_nReceiveStatus++;
                                    }
                                    break;
                                case 6:      // 校验
                                    if (checkSum == (byte)buffer[i])
                                    {
                                        g_nReceiveStatus++;
                                    } else {
                                        g_nReceiveStatus = 0;
                                    }
                                    break;
                                case 7:       //帧尾
                                    if (buffer[i] == 0x03) {
                                        //  g_ReceiveData[dataReceived++] = 0;
                                        //TODO 分析指令
                                        // Log.d(TAG,"*************g_ReceiveData = "+toHex(g_ReceiveData));
                                        // Log.d(TAG,"whichCmd = "+whichCmd+"");
                                        AnalyzeHFCmd(whichCmd, g_ReceiveData);

                                    }else {
                                        g_nReceiveStatus = 0;
                                    }
                                    break;
                                default:
                                    g_nReceiveStatus = 0;
                                    break;
                            }
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }


    /**
     * 特高频RFID模块写线程
     */
    private class  UHFWriteThread extends Thread {

        boolean isOver = false;
        public void finishRunning() {
            isOver = true;
        }
        @Override
        public void run() {

            super.run();
            while (isOver == false) {

                UHF_Read_ID();
                try {
                    sleep(500);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 特高频RFID模块读线程
     */
    private class UHFReadThread extends Thread {
        boolean isOver = false;
        public void finishRunning() {
            isOver = true;
        }
        @Override
        public void run() {
            super.run();
            int size;

            while(isOver == false) {

                try {
                    byte[] buffer = new byte[MAX_RECEIVE_DATA_LEN];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);

                    if (size > 0) {
                        //todo:通知督导的卡号
                        Message msg = new Message();
                        Bundle data = new Bundle();
                       // int j = 0;
                        for (int i = 0; i < size; i++) {   //每一个循环检测字节，先过滤转义符，再处理字符含义

                            switch (buffer[i]) {
                                case (byte) 0xBB:
                                    if (buffer[i+1] == 0x02 && buffer[i+2] == 0x22 && buffer[i+3] == 0x00 && buffer[i+4] == 0x10 && buffer[i+21] == 0x7E) {
                                        byte[] uhfCardId = new byte[16];
                                        System.arraycopy(buffer,i+5,uhfCardId,0,16);
                                        i = i+22;

                                        msg.what = Command.UHF_ID;
                                        data.putString("uhfCardId",toHex(uhfCardId));
                                        Log.v(TAG, "UHFCardID = " + toHex(uhfCardId));
                                        msg.setData(data);
                                        uiHandler.sendMessage(msg);
                                    }
                                    break;
                                default:
                                    break;

                            }

                        }
                    }


                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}
