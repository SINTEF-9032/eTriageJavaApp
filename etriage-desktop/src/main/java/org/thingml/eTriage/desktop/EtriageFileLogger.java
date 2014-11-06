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
package org.thingml.eTriage.desktop;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Locale;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.thingml.etriage.driver.Etriage;
import org.thingml.etriage.driver.EtriageListener;

/**
 *
 * @author ffl
 */
public class EtriageFileLogger implements EtriageListener {
    
    private SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
    private String SEPARATOR = "\t";
    
    protected File folder;
    
    protected Etriage etb;
    
    protected boolean logging = false;
    protected boolean request_start = false;
    protected long startTime = 0;
    protected long last_ski = 0;
    protected long last_hum = 0;
    protected long last_mag = 0;
    protected long last_imu = 0;
    protected long last_qat = 0;
    
    
    protected PrintWriter log;
    protected PrintWriter ski;
    protected PrintWriter imu;
    protected PrintWriter qat;
    protected PrintWriter hum;
    protected PrintWriter mag;
    
    public EtriageFileLogger(File folder, Etriage etb) {
        this.folder = folder;
        this.etb = etb;
    }
    
    public boolean isLogging() {
        return logging;
    }
    
    public void startLoggingInFolder(File sFolder) {
        try {
           log = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_log.txt")));
           log.println("# This file contains one line per data received from the etb unit.");
           
           ski = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_ski.txt")));
           ski.println("RXTime" + SEPARATOR + "CorrTime" + SEPARATOR + "RawTime" + SEPARATOR + "dT" + SEPARATOR + "Skin Temperature (°C)");
           
           hum = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_hum.txt")));
           hum.println("RXTime" + SEPARATOR + "CorrTime" + SEPARATOR + "RawTime" + SEPARATOR + "dT" + SEPARATOR + "T1" + SEPARATOR + "H1" + SEPARATOR + "T2" + SEPARATOR + "H2");
           
           mag = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_mag.txt")));
           mag.println("RXTime" + SEPARATOR + "CorrTime" + SEPARATOR + "RawTime" + SEPARATOR + "dT" + SEPARATOR + "Mag. X" + SEPARATOR + "Mag. Y" + SEPARATOR + "Mag. Z");
           
           
           imu = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_imu.txt")));
           imu.println("RXTime" + SEPARATOR + "CorrTime" + SEPARATOR + "RawTime" + SEPARATOR + "dT" + SEPARATOR +  "Acc. X" + SEPARATOR + "Acc. Y" + SEPARATOR + "Acc. Z" + SEPARATOR + "Gyro. X" + SEPARATOR + "Gyro. Y" + SEPARATOR + "Gyro. Z");

           qat = new PrintWriter(new FileWriter(new File(sFolder, "eTriage_qat.txt")));
           qat.println("RXTime" + SEPARATOR + "CorrTime" + SEPARATOR + "RawTime" + SEPARATOR + "dT" + SEPARATOR + "Quad. W" + SEPARATOR + "Quad. X" + SEPARATOR + "Quad. Y" + SEPARATOR + "Quad. Z" + SEPARATOR + "Pitch" + SEPARATOR + "Roll" + SEPARATOR + "Yaw");

           
       } catch (IOException ex) {
           Logger.getLogger(EtriageFileLogger.class.getName()).log(Level.SEVERE, null, ex);
       }
       last_ski = System.currentTimeMillis();
       last_hum = System.currentTimeMillis();
       last_mag = System.currentTimeMillis();
       last_imu = System.currentTimeMillis();
       startTime = System.currentTimeMillis();
       logging = true;
    }
    
    public void startLogging() {
       String sName = createSessionName(); 
       File sFolder = new File(folder, sName);
       
       // To avoid overwriting an exiting folder (in case several logs are created at the same time)
       int i=1;
       while (sFolder.exists()) {
           sFolder = new File(folder, sName + "-" + i);
           i++;
       }
       
       sFolder.mkdir();
       startLoggingInFolder(sFolder);
    }
    
    public void stopLogging() {
        if (logging) {
            logging = false;
            log.close();
            ski.close();
            imu.close();
            mag.close();
            hum.close();
            log = null;
            ski = null;
            mag = null;
            imu = null;
            mag = null;
        }
    }
    
    public String createSessionName() {
        SimpleDateFormat timestampFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        return timestampFormat.format( Calendar.getInstance().getTime());
    }
    
    public String currentTimeStamp(int timestamp) {
        if (timestamp<0 || etb == null) return "" + System.currentTimeMillis() + SEPARATOR + "?" + SEPARATOR + "?";
        else return "" + System.currentTimeMillis() + SEPARATOR + etb.getEpochTimestamp(timestamp) + SEPARATOR + timestamp;
    }
        
    private DecimalFormat tempFormat = new DecimalFormat("0.00");
    @Override
    public void skinTemperature(double temp, int timestamp) {
        if (logging) {
            ski.println(currentTimeStamp(timestamp) + SEPARATOR + (System.currentTimeMillis() - last_ski) + SEPARATOR + tempFormat.format(temp));
            log.println(currentTimeStamp(timestamp) + SEPARATOR + "[skinTemperature]" + SEPARATOR + tempFormat.format(temp));
            last_ski = System.currentTimeMillis();
        }
    }

    @Override
    public void skinTemperatureInterval(int value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[skinTemperatureInterval]" + SEPARATOR + value);
    }

    @Override
    public void battery(int battery, int timestamp) {
        if (logging) log.println(currentTimeStamp(timestamp) + SEPARATOR + "[battery]" + SEPARATOR + battery + "%" + SEPARATOR + timestamp);
    }

    @Override
    public void manufacturer(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[manufacturer]" + SEPARATOR + value);
    }

    @Override
    public void model_number(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[model_number]" + SEPARATOR + value);
    }

    @Override
    public void serial_number(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[serial_number]" + SEPARATOR + value);
    }

    @Override
    public void hw_revision(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[hw_revision]" + SEPARATOR + value);
    }

    @Override
    public void fw_revision(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[fw_revision]" + SEPARATOR + value);
    }

    @Override
    public void testPattern(byte[] data, int timestamp) {
    }

    @Override
    public void timeSync(int seq, int timestamp) {
    }
    
    // eTriage bracelet
    @Override
    public void etbDateTime(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbTimeDate]" + SEPARATOR + value);
    }
    
    @Override
    public void etbPosition(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbPosition]" + SEPARATOR + value);
    }

    @Override
    public void etbTriageLevel(int value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbTriageLevel]" + SEPARATOR + value);
    }

    @Override
    public void etbLocation(int value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbLocation]" + SEPARATOR + value);
    }

    @Override
    public void etbLocationId(String value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbLocationId]" + SEPARATOR + value);
    }
    
    @Override
    public void etbConnectionInterval(int value) {
        if (logging) log.println(currentTimeStamp(-1) + SEPARATOR + "[etbConnectionInterval]" + SEPARATOR + value);
    }
    
    @Override
    public void etbConsole(String value) {
    }
}