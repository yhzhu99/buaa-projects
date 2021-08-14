package com.topelec.zigbeecontrol;


import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import realarm.hardware.HardwareControl;

import static com.topelec.zigbeecontrol.Command.LED_SENSOR;

/**
 * Created by Amber on 2015/3/19.
 */
public class SensorControl {

    private static final String TAG = "SensorControl";

    private Command mCommand;

    private File device;
    private boolean isUartOpened = false;
    private FileDescriptor mFd;
    private FileInputStream mInputStream;
    private FileOutputStream mOutputStream;
    private ReadThread mReadThread;
    private SendThread mSendThread;
//    private EnterQueueThread mEnterQueueThread;

    private LinkedList<byte[]> sendingQueue = new LinkedList<byte[]>();


    /*
     抽象目标，被观察者
     */
    private LedListener ledListener;
    private TempHumListener tempHumListener;
    private MotorListener motorListener;
    private LightSensorListener lightSensorListener;
    private InfraRedSensorListener infraRedSensorListener;
    private HallSensorListener hallSensorListener;
    private SmokeSensorListener smokeSensorListener;
    private DimmableLedListener dimmableLedListener;
    private ShakeSensorListener shakeSensorListener;
    private AccelerationSensorListener accelerationSensorListener;
    private DopplerSensorListener dopplerSensorListener;


    /**
     * 存放观察者
     */
    private List<LedListener> ledListeners = new ArrayList<LedListener>();
    private List<TempHumListener> tempHumListeners = new ArrayList<TempHumListener>();
    private List<MotorListener> motorListeners = new ArrayList<MotorListener>();
    private List<LightSensorListener> lightSensorListeners = new ArrayList<LightSensorListener>();
    private List<InfraRedSensorListener> infraRedSensorListeners = new ArrayList<InfraRedSensorListener>();
    private List<HallSensorListener> hallSensorListeners = new ArrayList<HallSensorListener>();
    private List<SmokeSensorListener> smokeSensorListeners = new ArrayList<SmokeSensorListener>();
    private List<DimmableLedListener> dimmableLedListeners = new ArrayList<DimmableLedListener>();
    private List<ShakeSensorListener> shakeSensorListeners = new ArrayList<ShakeSensorListener>();
    private List<AccelerationSensorListener> accelerationSensorListeners = new ArrayList<AccelerationSensorListener>();
    private List<DopplerSensorListener> dopplerSensorListeners = new ArrayList<DopplerSensorListener>();
    private List<PESensorListener> peSensorListeners = new ArrayList<PESensorListener>();

    /**
     * 构造函数
     */
    public SensorControl() {
            try {
                initUart();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

//        mReadThread = new ReadThread();
////        mEnterQueueThread = new EnterQueueThread();
//        mSendThread = new SendThread();
//
//        mReadThread.start();
////        mEnterQueueThread.start();
//        mSendThread.start();

    }

//    public static SensorControl getInstance() {
//        if (null == mInstance) {
//            mInstance = new SensorControl();
//        }
//        return mInstance;
//    }


    /**
     * 串口初始化函数
     * @throws IOException
     */
    private void initUart() throws IOException
    {

        //检验串口是否可读写，如果不可读写，授权串口可读写
        device=new File(Command.zigbeePort);

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
 //                   throw new SecurityException();
                }
            } catch (Exception e) {
                e.printStackTrace();
//                throw new SecurityException();
            }
        }

        mFd = HardwareControl.OpenSerialPort(device.getAbsolutePath(), Command.bautrate,0);
        if (mFd == null) {
            Log.e(TAG, "native open returns null");
            throw new IOException();
        }else {
            isUartOpened = true;
            mInputStream = new FileInputStream(mFd);
            mOutputStream = new FileOutputStream(mFd);
        }

    }

    public void actionControl(boolean isOpen) {

        if (isOpen) {
//            try {
//                initUart();
//            } catch (IOException e1) {
//                // TODO Auto-generated catch block
//                e1.printStackTrace();
//            }
            mReadThread = new ReadThread();
//            mEnterQueueThread = new EnterQueueThread();
            mSendThread = new SendThread();

            mReadThread.start();
//            mEnterQueueThread.start();
            mSendThread.start();

        }else {
//            mEnterQueueThread.finishToSend();
//            mSendThread.finishToSend();

            if (mSendThread != null)
                mSendThread.interrupt();
//
//            if (mReadThread != null)
//                mReadThread.interrupt();
            mReadThread.finishToSend();
        }
    }

    public void closeSerialDevice() {
        HardwareControl.CloseSerialPort();
        isUartOpened = false;
    }
    /**
     * 发送打开LED1的指令
     */

    public void led1_On(boolean isWaiting) {

        queueToSend(Command.led1_on, isWaiting);
    }

    /**
     * 发送关闭LED1的指令
     */
    public void led1_Off(boolean isWaiting) {

        queueToSend(Command.led1_off,isWaiting);
    }

    /**
     * 发送打开LED2的指令
     */
    public void led2_On(boolean isWaiting) {

        queueToSend(Command.led2_on,isWaiting);
    }

    /**
     * 发送关闭LED2的指令
     */
    public void led2_Off(boolean isWaiting) {

        queueToSend(Command.led2_off,isWaiting);
    }

    /**
     * 发送打开LED3的指令
     */
    public void led3_On(boolean isWaiting) {

        queueToSend(Command.led3_on,isWaiting);
    }

    /**
     * 发送关闭LED3的指令
     */
    public void led3_Off(boolean isWaiting) {

        queueToSend(Command.led3_off,isWaiting);
    }

    /**
     * 发送打开LED4的指令
     */
    public void led4_On(boolean isWaiting) {

        queueToSend(Command.led4_on,isWaiting);
    }

    /**
     * 发送关闭LED4的指令
     */
    public void led4_Off(boolean isWaiting) {

        queueToSend(Command.led4_off,isWaiting);
    }

    /**
     * 发送打开全部LED的指令
     */
    public void allLeds_On(boolean isWaiting) {

        queueToSend(Command.led_all_on,isWaiting);
    }

    /**
     * 发送关闭全部LED的指令
     */
    public void allLeds_Off(boolean isWaiting) {

        queueToSend(Command.led_all_off,isWaiting);
    }

    /**
     * 发送电机正传的指令
     */
    public void fanForward(boolean isWaiting) {

        queueToSend(Command.fan_forward,isWaiting);
    }

    /**
     * 发送电机反转的指令
     */
    public void fanBackward(boolean isWaiting) {

        queueToSend(Command.fan_backward,isWaiting);
    }

    /**
     * 发送电机停止的指令
     */
    public void fanStop(boolean isWaiting) {

        queueToSend(Command.fan_stop,isWaiting);
    }

    /**
     * 查询温度
     */
    public void checkTemperature(boolean isWaiting) {

        queueToSend(Command.get_temp,isWaiting);
    }

    /**
     * 查询湿度
     */
    public void checkHumidity(boolean isWaiting) {

        queueToSend(Command.get_hum,isWaiting);
    }

    /**
     * 查询光照强度
     */
    public void checkBrightness(boolean isWaiting) {

        queueToSend(Command.get_brightness,isWaiting);
    }

    /**
     * 查询有害气体浓度
     */
    public void checkSmoke(boolean isWaiting) {

        queueToSend(Command.get_smoke,isWaiting);
    }

    /**
     * 查询霍尔磁场状态
     */
    public void checkHall(boolean isWaiting) {

        queueToSend(Command.get_hall,isWaiting);
    }

    /**
     * 查询红外状态
     */
    public void checkIR(boolean isWaiting) {

        queueToSend(Command.get_IR,isWaiting);
    }

    /**
     * 发送无极调光控制指令
     */
    public void steplessLedControl(byte progress,boolean isWaiting) {

        Command.set_stepless_led[5] = progress;
        queueToSend(Command.set_stepless_led,isWaiting);
    }

    /**
     * 查询震动状态
     * @param isWaiting
     */
    public void checkShake(boolean isWaiting) {
        queueToSend(Command.get_shake,isWaiting);
    }

    /**
     * 查询三轴加速度数据
     * @param isWaiting
     */
    public void checkX(boolean isWaiting) {
        queueToSend(Command.get_x,isWaiting);
    }

    public void checkY(boolean isWaiting) {
        queueToSend(Command.get_y,isWaiting);
    }
    public void checkZ(boolean isWaiting) {
        queueToSend(Command.get_z,isWaiting);
    }

    /**
     * 查询多普勒传感器状态
     * @param isWaiting
     */
    public void checkDoppler(boolean isWaiting) {
        queueToSend(Command.get_doppler,isWaiting);
    }

    /**
     * 查询光电传感器状态
     * @param isWaiting
     */
    public void checkPE(boolean isWaiting) {
        queueToSend(Command.get_PE,isWaiting);
    }
    /**
     * 插入发送队列
     * @param element     需要插入待发送的指令队列的指令 byte[]类型
     * @param isWaiting   true：插入队列尾部，排队等待发送，一般用于程序循环发送;
     *                       false：插入队列头部，优先发送，一般用于界面触控发送的指令
     */
    private void queueToSend(byte[] element,boolean isWaiting) {

        if (isWaiting == true) {                 //插入队列尾部，等待发送
            synchronized (sendingQueue) {
                sendingQueue.addLast(element);
            }
        } else {                                 //插入队列头部，优先发送
            synchronized (sendingQueue) {
                sendingQueue.addFirst(element);
            }
        }
    }

    /**
     * 取发送的指令
     * @return
     */
    private byte[] getQueueElement() {
        synchronized (sendingQueue) {
            if (sendingQueue.size() > 0) {
                return sendingQueue.removeFirst();
            }else {
                return null;
            }
        }
    }

    /**
     * 数据处理函数
     */
    private void parseCommand(byte[] cmd) {

//        int temperature,humidity;
        int ad_value,temphum_value;
        Message msg = new Message();
        Bundle data = new Bundle();

        switch (cmd[1]) {
            case Command.LED_SENSOR://LED灯控制节点，暂无返回值
                notifyLedListener(cmd[2],cmd[3]);
                break;
            case Command.FAN_SENSOR://电机控制节点，暂无返回值
                notifyMotorListener(cmd[3]);
                break;
            case Command.TEMPHUM_SENSOR://温湿度控制节点，返回温度或者湿度信息
                temphum_value = (cmd[3]&0xFF)*256 + (cmd[4]&0xFF);
                if (temphum_value != 0xFFFF)
                    notifyTempHumListener(cmd[2],temphum_value);
                break;
            case Command.BRIGHTNESS_SENSOR://光照强度采集节点，返回光照强度是否超过阈值
                notifyLightSenserListener(cmd[3]);
                break;
            case Command.IR_SENSOR://红外信号采集节点，返回红外信号是否被阻断
                notifyInfraRedSensorListener(cmd[3]);
                break;
            case Command.HALL_SENSOR://霍尔磁场检测节点，返回是否检测到磁场
                notifyHallSensorListener(cmd[3]);
                break;
            case Command.SMOKE_SENSOR://有害气体检测节点，返回检测到的有害气体浓度是否超过阈值
                notifySmokeSensorListener(cmd[3]);
                break;
            case Command.STEPLESS_LED://无极调光灯控制节点，暂无返回值
                notifyDimmableLedListener(cmd[3]);
                break;
            case Command.SHAKE_SENSOR://震动
                notifyShakeSensorListener(cmd[3]);
                break;
            case Command.AD_SENSOR://三轴加速度
                ad_value = (cmd[3]&0xFF) * 256 + (cmd[4]&0xFF);
                notifyAccelerationSensorListener(cmd[2], ad_value);
                break;
            case Command.DOPPLER_SENSOR://多普勒
                notifyDopplerSensorListener(cmd[3]);
                break;
            case Command.PE_SENSOR://光电
                notifyPESensorListener(cmd[3]);
            default:
                break;
        }
    }

    /**
     * 读串口线程
     */
    private class ReadThread extends Thread {

        private boolean isOver = false;
        public void finishToSend() {
            isOver = true;
        }

        @Override
        public void run() {
            super.run();
            int size;
            int flag = 0;
            int sum = 0;
            int cmd_len = 0;
            byte[] cmd = new byte[6];

            while(isOver ==  false) {
//            while (!Thread.currentThread().isInterrupted()) {

                try {
                    byte[] buffer = new byte[64];
                    if (mInputStream == null) return;
                    size = mInputStream.read(buffer);
                    if (size > 0) {
                        //                    onDataReceived(buffer, size); //此处分析收到数据传感器种类
                        Log.d(TAG,toHex(buffer));
                        for (int i = 0; i < size; i++) {

                            if (flag == 0) {
                                if (buffer[i] == 0x55) {
                                    flag++;
                                }
                            } else if (flag == 1) {
                                cmd_len = buffer[i];
                                flag++;
                                sum += buffer[i];
                            } else if (flag >= 2 && flag < cmd_len+1) {

                                cmd[flag++ -2] = buffer[i];
                                sum += buffer[i];

                            } else if (flag == cmd_len+1) {
                                if (sum == buffer[i]) {
                                    //TODO 分析指令
                                    parseCommand(cmd);
                                    Log.d(TAG,toHex(cmd));
                                    flag = 0;
                                    sum = 0;
                                    cmd_len = 0;
                                }
                            }

                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    flag = 0;
                    sum = 0;
                    cmd_len = 0;
                    continue;
                }
            }
        }
    }


    /**
     * 串口发送线程
     */
    public class SendThread extends Thread {

        boolean isOver = false;
        byte[] ee;
        /**
         * 终止线程
         */
        public void finishToSend() {
            isOver = true;
        }

        @Override
        public void run() {
//            while (isOver == false) {
            while (!Thread.currentThread().isInterrupted()) {
                synchronized (mOutputStream) {
                    try {
                        if(sendingQueue.size() > 0)
                            mOutputStream.write(getQueueElement());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                //CHECK_SENSOR_DELAYms发送一次串口指令
                try {
                    Thread.currentThread().sleep(Command.CHECK_SENSOR_DELAY);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 将需要查询的数据指令循环插入队列的线程
     */
//    public class EnterQueueThread extends Thread {
//        boolean isOver = false;
//        int flag = 1;
//
//        /**
//         * 终止线程
//         */
//        public void finishToSend() {
//            isOver = true;
//        }
//        @Override
//        public void run() {
//
//            while (isOver == false) {
//
//
//                switch (flag) {
//                    case 1://发送查询温度指令
//                        checkTemperature(true);
//                        flag++;
//                        break;
//                    case 2://发送查询湿度指令
//                        checkHumidity(true);
//                        flag++;
//                        break;
//                    case 3://发送查询光照强度指令
//                        checkBrightness(true);
//                        flag++;
//                        break;
//                    case 4://发送查询有害气体指令
//                        checkSmoke(true);
//                        flag++;
//                        break;
//                    case 5://发送查询霍尔磁场指令
//                        checkHall(true);
//                        flag++;
//                        break;
//                    case 6://发送查询红外信号指令
//                        checkIR(true);
//                        flag =1;
//                        break;
//                    default:
//                        break;
//                }
//
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    //e.printStackTrace();
//                    Thread.currentThread().interrupt();
//                }
//            }
//        }
//    }

    /**
     * for  debug
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
     * led控制返回结果接口
     * led_id:1~5
     * led_status：代表打开或者关闭
     * 接口中函数在对应的需要更新状态的Activity中实现
     */
    public interface LedListener {
        public void LedControlResult(byte led_id,byte led_status);
    }

    /**
     * 添加LED状态观察者
     */
    public void addLedListener(LedListener led) {
        synchronized (ledListeners) {
            if (!ledListeners.contains(led)) {
                ledListeners.add(led);
            }
        }
    }

    /**
     * 删除LED状态观察者
     */
    public void removeLedListener(LedListener led) {
        synchronized (ledListeners) {
            ledListeners.remove(led);
        }
    }

    /**
     * LED状态发生变化，通知各个观察者
     */
    public void notifyLedListener(byte led_id,byte led_status) {
        for (LedListener ledListener:ledListeners) {
            ledListener.LedControlResult(led_id,led_status);
        }
    }

    /**
     * 电机控制返回接口
     * motor_status：1~3
     */
    public interface MotorListener {
        public void motorControlResult(byte motor_status);
    }

    /**
     * 添加电机状态观察者
     * @param motor
     */
    public void addMotorListener(MotorListener motor) {
        synchronized (motorListeners) {
            if (!motorListeners.contains(motor)) {
                motorListeners.add(motor);
            }
        }
    }

    /**
     * 删除电机观察者
     * @param motor
     */
    public void removeMotorListener(MotorListener motor) {
        synchronized (motorListeners) {
            motorListeners.remove(motor);
        }
    }

    /**
     * 点击状态发生改变，通知各个观察者
     * @param motor_status
     */
    public void notifyMotorListener(byte motor_status) {
        for (MotorListener motorListener:motorListeners) {
            motorListener.motorControlResult(motor_status);
        }
    }

    /**
     * 温湿度模块 观察者接口
     * senser_id：1 温度 2 湿度
     * senser_data：温度或湿度值
     */
    public interface TempHumListener {
        public void tempHumReceive(byte senser_id,int senser_data);
    }

    /**
     * 添加温湿度数据观察者
     * @param tempHum
     */
    public void addTempHumListener(TempHumListener tempHum) {
        synchronized (tempHumListeners) {
            if (!tempHumListeners.contains(tempHum)) {
                tempHumListeners.add(tempHum);
            }
        }
    }

    /**
     * 删除温湿度数据观察者
     * @param tempHum
     */
    public void removeTempHumListener(TempHumListener tempHum) {
        synchronized (tempHumListeners) {
            tempHumListeners.remove(tempHum);
        }
    }

    /**
     * 温湿度数据发生改变，通知各个观察者
     * @param senser_id 1 温度 2 湿度
     * @param senser_data
     */
    public void notifyTempHumListener(byte senser_id,int senser_data) {
        for (TempHumListener tempHumListener:tempHumListeners) {
            tempHumListener.tempHumReceive(senser_id,senser_data);
        }
    }

    /**
     * 光敏传感器 数据观察者接口
     */
    public interface LightSensorListener {
        public void lightSensorReceive(byte sensor_status);
    }

    /**
     * 添加光敏传感器数据观察者
     */
    public void addLightSensorListener(LightSensorListener lightSensor) {
        synchronized (lightSensorListeners) {
            if (!lightSensorListeners.contains(lightSensor)) {
                lightSensorListeners.add(lightSensor);
            }
        }
    }

    /**
     * 删除光敏传感器数据观察者
     * @param lightSenser
     */
    public void removeLightSensorListener(LightSensorListener lightSenser) {
        synchronized (lightSensorListeners) {
            lightSensorListeners.remove(lightSenser);
        }
    }

    /**
     * 光敏传感器状态发生改变，通知各个观察者
     * @param lightSenser
     */
    public void notifyLightSenserListener(byte lightSenser) {
        for (LightSensorListener lightSenserListener:lightSensorListeners) {
            lightSenserListener.lightSensorReceive(lightSenser);
        }
    }

    public interface InfraRedSensorListener {
        public void infraRedSensorReceive(byte sensor_status);
    }

    public void addInfraRedSensorLinstener(InfraRedSensorListener infraRedSensor) {
        synchronized (infraRedSensorListeners) {
            if (!infraRedSensorListeners.contains(infraRedSensor)) {
                infraRedSensorListeners.add(infraRedSensor);
            }
        }
    }
    public void removeInfraRedSensorListener(InfraRedSensorListener infraRedSensor) {
        synchronized (infraRedSensorListeners) {
            infraRedSensorListeners.remove(infraRedSensor);
        }
    }
    public void notifyInfraRedSensorListener(byte infraRedSensor) {
        for (InfraRedSensorListener infraRedSensorListener:infraRedSensorListeners) {
            infraRedSensorListener.infraRedSensorReceive(infraRedSensor);
        }
    }

    public interface HallSensorListener {
        public void hallSensorReceive(byte sensor_status);
    }

    public void addHallSensorListener(HallSensorListener hallSensor) {
        synchronized (hallSensorListeners) {
            if (!hallSensorListeners.contains(hallSensor)) {
                hallSensorListeners.add(hallSensor);
            }
        }
    }
    public void removeHallSensorListener(HallSensorListener hallSensor) {
        synchronized (hallSensorListeners) {
            hallSensorListeners.remove(hallSensor);
        }
    }
    public void notifyHallSensorListener(byte hallSensor) {
        for (HallSensorListener hallSensorListener:hallSensorListeners) {
            hallSensorListener.hallSensorReceive(hallSensor);
        }
    }

    public interface SmokeSensorListener {
        public void smokeSensorReceive(byte sensor_status);
    }

    public void addSmokeSensorListener(SmokeSensorListener smokeSensor) {
        synchronized (smokeSensorListeners) {
            if (!smokeSensorListeners.contains(smokeSensor)) {
                smokeSensorListeners.add(smokeSensor);
            }
        }
    }

    public void removeSmokeSensorListener(SmokeSensorListener smokeSensor) {
        synchronized (smokeSensorListeners) {
            smokeSensorListeners.remove(smokeSensor);
        }
    }

    public void notifySmokeSensorListener(byte smokeSensor) {
        for (SmokeSensorListener smokeSensorListener:smokeSensorListeners) {
            smokeSensorListener.smokeSensorReceive(smokeSensor);
        }
    }

    public interface DimmableLedListener {
        public void dimmableLedReceive(byte sensor_status);
    }

    public void addDimmableLedListener(DimmableLedListener dimmableLed) {
        synchronized (dimmableLedListeners) {
            if (!dimmableLedListeners.contains(dimmableLed)) {
                dimmableLedListeners.add(dimmableLed);
            }
        }
    }

    public void removeDimmableLedListener(DimmableLedListener dimmableLed) {
        synchronized (dimmableLedListeners) {
            dimmableLedListeners.remove(dimmableLed);
        }
    }

    public void notifyDimmableLedListener(byte dimmableLed) {
        for (DimmableLedListener dimmableLedListener:dimmableLedListeners) {
            dimmableLedListener.dimmableLedReceive(dimmableLed);
        }
    }

    public interface ShakeSensorListener {
        public void shakeSensorReceive(byte sensor_status);
    }

    public void addShakeSensorListener(ShakeSensorListener shakeSensor) {
        synchronized (shakeSensorListeners) {
            if (!shakeSensorListeners.contains(shakeSensor)) {
                shakeSensorListeners.add(shakeSensor);
            }
        }
    }

    public void removeShakeSensorListener(ShakeSensorListener shakeSensor) {
        synchronized (shakeSensorListeners) {
            shakeSensorListeners.remove(shakeSensor);
        }
    }

    public void notifyShakeSensorListener(byte shakeSensor) {
        for (ShakeSensorListener shakeSensorListener:shakeSensorListeners) {
            shakeSensorListener.shakeSensorReceive(shakeSensor);
        }
    }

    public interface AccelerationSensorListener {
        public void accelerationSensorReceive(byte sensor_id,int sensor_data);
    }

    public void addAccelerationSensorListener(AccelerationSensorListener accelerationSensor) {
        synchronized (accelerationSensorListeners) {
            if (!accelerationSensorListeners.contains(accelerationSensor)) {
                accelerationSensorListeners.add(accelerationSensor);
            }
        }
    }

    public void removeAccelerationSensorListener(AccelerationSensorListener accelerationSensor) {
        synchronized (accelerationSensorListeners) {
            accelerationSensorListeners.remove(accelerationSensor);
        }
    }

    public void notifyAccelerationSensorListener(byte sensor_id,int accelerationSensor){
        for (AccelerationSensorListener accelerationSensorListener:accelerationSensorListeners) {
            accelerationSensorListener.accelerationSensorReceive(sensor_id,accelerationSensor);
        }
    }

    /**
     *
     private DopplerSensorListener dopplerSensorListener;
     */
    public interface DopplerSensorListener {
        public void dopplerSensorReceive(byte sensor_status);
    }

    public void addDopplerSensorListener(DopplerSensorListener dopplerSensor) {
        synchronized (dopplerSensorListeners) {
            if (!dopplerSensorListeners.contains(dopplerSensor)) {
                dopplerSensorListeners.add(dopplerSensor);
            }
        }
    }
    public void removeDopplerSensorListener(DopplerSensorListener dopplerSensor) {
        synchronized (dopplerSensorListeners) {
            dopplerSensorListeners.remove(dopplerSensor);
        }
    }
    public void notifyDopplerSensorListener(byte dopplerSensor) {
        for (DopplerSensorListener dopplerSensorListener:dopplerSensorListeners) {
            dopplerSensorListener.dopplerSensorReceive(dopplerSensor);
        }
    }


    public interface PESensorListener {
        public void peSensorReceive(byte sensor_status);
    }

    public void addPESensorListener(PESensorListener peSensor) {
        synchronized (peSensorListeners) {
            if (!peSensorListeners.contains(peSensor)) {
                peSensorListeners.add(peSensor);
            }
        }
    }

    public void removePESensorListener(PESensorListener peSensor) {
        synchronized (peSensorListeners) {
            peSensorListeners.remove(peSensor);
        }
    }

    public void notifyPESensorListener(byte peSensor) {
        for (PESensorListener peSensorListener:peSensorListeners) {
            peSensorListener.peSensorReceive(peSensor);
        }
    }

}
