/**
 * Copyright (C) 2012 SINTEF <franck.fleurey@sintef.no>
 *
 * Licensed under the GNU LESSER GENERAL PUBLIC LICENSE, Version 3, 29 June 2007;
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * 	http://www.gnu.org/licenses/lgpl-3.0.txt
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingml.etriage.driver;

import java.util.ArrayList;
import org.thingml.bglib.BGAPIDefaultListener;
import org.thingml.bglib.BGAPI;
import org.thingml.rtsync.core.TimeSynchronizable;
import org.thingml.rtsync.core.TimeSynchronizer;
//import org.thingml.bglib.samples.ByteUtils;

/**
 *
 * @author ffl
 */
public class Etriage extends BGAPIDefaultListener implements TimeSynchronizable {
    
    
    private final int DEFAULT_SUB = 0x01; // 0x01 for notifications and 0x02 for indications
    
    private ArrayList<EtriageListener> listeners = new ArrayList<EtriageListener>();
    
    public synchronized void addEtbListener(EtriageListener l) {
        listeners.add(l);
    }
    
    public synchronized void removeEtbListener(EtriageListener l) {
        listeners.remove(l);
    }
    
    protected BGAPI bgapi;
    protected int connection;
    
    // 16 bits timestamps with a 4ms resolution -> 18bits timestamps in ms -> Max value = 0x3FFF
    private TimeSynchronizer rtsync = new TimeSynchronizer(this, 0x03FFFF);

    public TimeSynchronizer getTimeSynchronizer() {
        return rtsync;
    }
    
    public long getEpochTimestamp(int ts) {
        if (rtsync.isRunning()) return rtsync.getSynchronizedEpochTime(ts);
        else return 0;
    }
    
    public Etriage(BGAPI bgapi, int connection) {
        this.bgapi = bgapi;
        this.connection = connection;
        bgapi.addListener(this);
    }
    
    public void disconnect() {
        bgapi.removeListener(this);
    }
    
    public void startTimeSync() {
        subscribeTimeSync();
        rtsync.start_timesync();
    }
    
    public void stopTimeSync() {
        unsubscribeTimeSync();
        rtsync.stop_timesync();
    }
    
    public static final int ALERT_LEVEL = 0x4B;
    
    public void readAlertLevel() {
        bgapi.send_attclient_read_by_handle(connection, ALERT_LEVEL);
    }
    
    public void setAlertLevel(int value) {
        byte[] i = new byte[1];
        i[0] = (byte)(value & 0xFF);
        bgapi.send_attclient_write_command(connection, ALERT_LEVEL, i);
    }
    
    /**************************************************************
     * Skin Temperature
     **************************************************************/ 
    public static final int THERMOMETER_VALUE = 0x1F;
    public static final int THERMOMETER_CONFIG = 0x20;
    public static final int THERMOMETER_INTERVAL = 0x24;
    
    public void subscribeSkinTemperature() {
        bgapi.send_attclient_write_command(connection, THERMOMETER_CONFIG, new byte[]{0x02, 0x00});
    }
    
    public void unsubscribeSkinTemperature() {
        bgapi.send_attclient_write_command(connection, THERMOMETER_CONFIG, new byte[]{0x00, 0x00});
    }
    
    public void readSkinTemperatureInterval() {
        bgapi.send_attclient_read_by_handle(connection, THERMOMETER_INTERVAL);
    }
    
    public void setSkinTemperatureInterval(int value) {
        byte[] i = new byte[2];
        i[1] = (byte)((value>>8) & 0xFF);
        i[0] = (byte)(value & 0xFF);
        bgapi.send_attclient_write_command(connection, THERMOMETER_INTERVAL, i);
    }
    
    private synchronized void skinTemperature(byte[] value) {
        
        int ts = ((value[6] & 0xFF) << 8) + (value[5] & 0xFF);
        
        for (EtriageListener l : listeners) {
            l.skinTemperature(getTemperature(value), ts*4);
        }
    }
    private synchronized void skinTemperatureInterval(byte[] value) {
        for (EtriageListener l : listeners) {
            l.skinTemperatureInterval((value[1]<<8) + (value[0] & 0xFF));
        }
    }
    

   
    /**************************************************************
     * TESTING PATTERN
     **************************************************************/ 
    public static final int CLK_VALUE = 0x44;
    public static final int CLK_CONFIG = 0x45;
    
    @Override
    public void sendTimeRequest(int seqNum) {
        bgapi.send_attclient_write_command(connection, CLK_VALUE, new byte[]{(byte)seqNum});
    }
    
    public void subscribeTimeSync() {
        bgapi.send_attclient_write_command(connection, CLK_CONFIG, new byte[]{0x01, 0x00});
    }
    
    public void unsubscribeTimeSync() {
        bgapi.send_attclient_write_command(connection, CLK_CONFIG, new byte[]{0x00, 0x00});
    }
    
    private synchronized void timeSync(byte[] value) {
        
        int ts = ((value[2] & 0xFF) << 8) + (value[1] & 0xFF);
        
        rtsync.receive_TimeResponse(value[0] & 0xFF, ts*4);
        
        for (EtriageListener l : listeners) {
            l.timeSync(value[0] & 0xFF, ts*4);
        }
    }
    
    /**************************************************************
     * TESTING PATTERN
     **************************************************************/ 
    public static final int TEST_VALUE = 0x47;
    public static final int TEST_CONFIG = 0x48;
    
    public void subscribeTestPattern() {
        bgapi.send_attclient_write_command(connection, TEST_CONFIG, new byte[]{0x01, 0x00});
    }
    
    public void unsubscribeTestPattern() {
        bgapi.send_attclient_write_command(connection, TEST_CONFIG, new byte[]{0x00, 0x00});
    }
    
    private synchronized void testPattern(byte[] value) {
        
        int ts = (value[0] & 0xFF);
        
        for (EtriageListener l : listeners) {
            l.testPattern(value, ts*4);
        }
    }
    
    /**************************************************************
     * Battery
     **************************************************************/ 

    public static final int BATTERY_VALUE = 0x28;
    public static final int BATTERY_CONFIG = 0x29;
    
    public void subscribeBattery() {
        bgapi.send_attclient_write_command(connection, BATTERY_CONFIG, new byte[]{0x01, 0x00});
    }
    
    public void unsubscribeBattery() {
        bgapi.send_attclient_write_command(connection, BATTERY_CONFIG, new byte[]{0x00, 0x00});
    }
    
    private synchronized void battery(byte[] value) {
        
        if (value.length < 3) {
            System.err.println("ISenseU Error: battery value length " + value.length + "instead of 3 (1 byte for the batery level + 2 bytes for timestamps" );
            return;
        }
        
        int ts = ((value[2] & 0xFF) << 8) + (value[1] & 0xFF);
        
        for (EtriageListener l : listeners) {
            l.battery((int)value[0], ts*4);
        }
    }
    
    /**************************************************************
     * Device info
     **************************************************************/ 
    
    public static final int MANUFACTURER = 0x0B;
    public static final int MODEL = 0x19;
    public static final int SERIAL = 0x11;
    public static final int HW_REV = 0x16;
    public static final int FW_REV = 0x0E;
    
    public void requestDeviceInfo() {
        bgapi.send_attclient_read_by_handle(connection, MANUFACTURER);
    }
    
    synchronized void manufacturer(byte[] value) {
        for (EtriageListener l : listeners) {
            l.manufacturer(new String(value));
        }
        bgapi.send_attclient_read_by_handle(connection, MODEL);
    }
    
    synchronized void model_number(byte[] value) {
        for (EtriageListener l : listeners) {
            l.model_number(new String(value));
        }
        bgapi.send_attclient_read_by_handle(connection, SERIAL);
    }
    
    synchronized void serial_number(byte[] value) {
        for (EtriageListener l : listeners) {
            l.serial_number(new String(value));
        }
        bgapi.send_attclient_read_by_handle(connection, HW_REV);
    }
    
    synchronized void hw_revision(byte[] value) {
        for (EtriageListener l : listeners) {
            l.hw_revision(new String(value));
        }
        bgapi.send_attclient_read_by_handle(connection, FW_REV);
    }
    
    synchronized void fw_revision(byte[] value) {
        for (EtriageListener l : listeners) {
            l.fw_revision(new String(value));
        }
    }
    
  
    /**************************************************************
     * eTriage bracelet
     **************************************************************/ 
    
    public static final int ETB_DATETIME = 0x2C;
    public static final int ETB_POSITION = 0x2F;
    public static final int ETB_TRIAGE_LEVEL = 0x32;
    public static final int ETB_LOCATION = 0x35;
    public static final int ETB_LOCATION_ID = 0x38;
    public static final int ETB_CONNECTION_INTERVAL = 0x3F;
    public static final int ETB_CONSOLE = 0x3B;

    public void readetbDateTime() {
        bgapi.send_attclient_read_by_handle(connection, ETB_DATETIME);
    }
    
    public void setetbDateTime(String value) {
        byte[] i = value.getBytes();
        bgapi.send_attclient_write_command(connection, ETB_DATETIME, i);
    }
    
    private synchronized void etbDateTime(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbDateTime(new String(value));
        }
    }
    
    public void readetbPosition() {
        bgapi.send_attclient_read_by_handle(connection, ETB_POSITION);
    }
    
    public void setetbPosition(String value) {
        byte[] i = value.getBytes();
        bgapi.send_attclient_write_command(connection, ETB_POSITION, i);
    }
    
    private synchronized void etbPosition(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbPosition(new String(value));
        }
    }
    
    public void readetbTriageLevel() {
        bgapi.send_attclient_read_by_handle(connection, ETB_TRIAGE_LEVEL);
    }
    
    public void setetbTriageLevel(int value) {
        byte[] i = new byte[1];
        i[0] = (byte)(value & 0xFF);
        bgapi.send_attclient_write_command(connection, ETB_TRIAGE_LEVEL, i);
    }
    
    private synchronized void etbTriageLevel(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbTriageLevel((value[0]));
        }
    }

    public void readetbLocation() {
        bgapi.send_attclient_read_by_handle(connection, ETB_LOCATION);
    }
    
    public void setetbLocation(int value) {
        byte[] i = new byte[1];
        i[0] = (byte)(value & 0xFF);
        bgapi.send_attclient_write_command(connection, ETB_LOCATION, i);
    }
    
    private synchronized void etbLocation(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbLocation((value[0]));
        }
    }

    public void readetbLocationId() {
        bgapi.send_attclient_read_by_handle(connection, ETB_LOCATION_ID);
    }
    
    public void setetbLocationId(String value) {
        byte[] i = value.getBytes();
        bgapi.send_attclient_write_command(connection, ETB_LOCATION_ID, i);
    }
    
    private synchronized void etbLocationId(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbLocationId(new String(value));
        }
    }

    public void readetbConnectionInterval() {
        bgapi.send_attclient_read_by_handle(connection, ETB_CONNECTION_INTERVAL);
    }
    
    public void setetbConnectionInterval(int value) {
        byte[] i = new byte[2];
        i[0] = (byte)(value & 0xFF);
        i[1] = (byte)(value>>8 & 0xFF);
        bgapi.send_attclient_write_command(connection, ETB_CONNECTION_INTERVAL, i);
    }
    
    private synchronized void etbConnectionInterval(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbConnectionInterval((value[1]<<8) + (value[0] & 0xFF));
        }
    }

    public void readetbConsole() {
        bgapi.send_attclient_read_by_handle(connection, ETB_CONSOLE);
    }
    
    public void setetbConsole(int value) {
        byte[] i = new byte[1];
        i[0] = (byte)(value & 0xFF);
        bgapi.send_attclient_write_command(connection, ETB_CONSOLE, i);
    }
    
    private synchronized void etbConsole(byte[] value) {
        for (EtriageListener l : listeners) {
            l.etbConsole(new String(value));
        }
    }
    
    public void subscribeEtbConsole() {
        bgapi.send_attclient_write_command(connection, ETB_CONSOLE, new byte[]{0x01, 0x00});
    }
    
    public void unsubscribeEtbConsole() {
        bgapi.send_attclient_write_command(connection, ETB_CONSOLE, new byte[]{0x00, 0x00});
    }
    
    public void sendBtConStart()
    {
        subscribeEtbConsole();
    }

    /**************************************************************
     * Receive attribute values
     **************************************************************/ 

    long receivedBytes = 0;
    
    public long getReceivedBytes() {
        return receivedBytes;
    }
    
    @Override
    public void receive_attclient_attribute_value(int connection, int atthandle, int type, byte[] value) {
        if (this.connection == connection) {
            receivedBytes += value.length;
            switch(atthandle) {
                
                case THERMOMETER_VALUE: skinTemperature(value); break;
                case THERMOMETER_INTERVAL: skinTemperatureInterval(value); break;
                
                case BATTERY_VALUE: battery(value); break;
                    
                case MANUFACTURER: manufacturer(value); break;
                case MODEL: model_number(value); break;
                case SERIAL: serial_number(value); break;
                case HW_REV: hw_revision(value); break;
                case FW_REV: fw_revision(value); break;
                
                case CLK_VALUE: timeSync(value); break;
                case TEST_VALUE: testPattern(value); break;
                    
                case ETB_DATETIME: etbDateTime(value); break;
                case ETB_POSITION: etbPosition(value); break;
                case ETB_TRIAGE_LEVEL: etbTriageLevel(value); break;
                case ETB_LOCATION: etbLocation(value); break;
                case ETB_LOCATION_ID: etbLocationId(value); break;
                case ETB_CONNECTION_INTERVAL: etbConnectionInterval(value); break;
                case ETB_CONSOLE: etbConsole(value); break;
                    
                default: 
                    System.out.println("[eTriage Driver] Got unknown attribute. Handle=" + Integer.toHexString(atthandle) + " val = " + bytesToString(value));
                    break;
            }
        }
    }
    
    public static double getTemperature(byte[] value) {
        if (value.length < 5) {
            //System.err.println("Cannot convert " + ByteUtils.bytesToString(value) + " to a temperature (expecting 5 bytes).");
            System.err.println("Cannot convert " + value + " to a temperature (expecting 5 bytes).");
            return 0;
        }
        int flags = value[0] & 0xFF;
        int exp = value[4];
        int mantissa = (value[3] << 16) + (value[2] << 8) + (value[1] & 0xFF);
        double result = mantissa * Math.pow(10, exp);
        return result;
    }
    
    public String bytesToString(byte[] bytes) {
        StringBuffer result = new StringBuffer();
        result.append("[ ");
        for(byte b : bytes) result.append( Integer.toHexString(b & 0xFF) + " ");
        result.append("]");
        return result.toString();        
    }

}
