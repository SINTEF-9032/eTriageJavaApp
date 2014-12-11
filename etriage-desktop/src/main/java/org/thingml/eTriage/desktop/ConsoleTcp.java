/**
 * Copyright (C) 2013 SINTEF <steffen.dalgard@sintef.no>
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

/*
 * ConsoleFrame.java
 *
 * Created on 5 Dec. 2013,
 */
package org.thingml.eTriage.desktop;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.text.DefaultCaret;
import org.thingml.etriage.driver.Etriage;
import org.thingml.etriage.driver.EtriageListener;
//import java.io.DataInputStream;
import java.io.InputStream;
//import java.io.PrintStream;
//import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.ServerSocket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Steffen
 */
public class ConsoleTcp extends javax.swing.JFrame implements Runnable, EtriageListener {

    protected Etriage etb;
    protected int     tcpPort;
    protected boolean stop;
    //protected DataInputStream is = null;
    protected InputStream is = null;
    //protected PrintStream os = null;
    protected OutputStream os = null;
    protected Socket clientSocket = null;    
    protected ServerSocket btServer = null;
    
    public ConsoleTcp(Etriage b, int tcpPort) {
        this.etb = b;
        this.tcpPort = tcpPort;
        this.stop = true;
        
        if (b != null) {
            b.addEtbListener(this);
            b.sendBtConStart();
        }
        initComponents();
        
        DefaultCaret caret = (DefaultCaret)jTextArea1.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        
        jTextArea1.addKeyListener(new KeyListener()
        {
              //When any key is pressed and released then the 
              //keyPressed and keyReleased methods are called respectively.
              //The keyTyped method is called when a valid character is typed.
              //The getKeyChar returns the character for the key used. If the key
              //is a modifier key (e.g., SHIFT, CTRL) or action key (e.g., DELETE, ENTER)
              //then the character will be a undefined symbol.
              @Override 
              public void keyPressed(KeyEvent e)
              {
                  e.consume();
              }
              @Override
              public void keyReleased(KeyEvent e)
              {
                  e.consume();
              }
              
              @Override
              public void keyTyped(KeyEvent e)
              {
                  //The getKeyModifiers method is a handy
                  //way to get a String representing the
                  //modifier key.
                  e.consume();
              }
        });
        
        start_tcp();
    }

    public void start_tcp() {
        if (stop == true) {
            stop = false;
            new Thread(this).start();
        }
    }

    public void stop_tcp() {
        if (stop != true) {
            stop = true;
        }
    }


@Override
    public void run() {
        int value;
        byte[] bArr = new byte[1];
        
        jTextArea1.append("Starting ConsoleTcp \n");
        try {
            btServer = new ServerSocket(tcpPort);
        } catch (IOException e) {
            System.out.println(e);
            jTextArea1.append("Failed to create ServerSocket \n");
            btServer = null;
        }        
        if (btServer != null) {
            do {
                jTextArea1.append("... listen to port" + tcpPort + "\n");

                // open a server socket listening for connection
                try {
                    clientSocket = btServer.accept();
                    jTextArea1.append("... connected\n");
                    //is = new DataInputStream(clientSocket.getInputStream());
                    is = clientSocket.getInputStream();
                    //os = new PrintStream(clientSocket.getOutputStream());
                    //os = new ByteArrayOutputStream(clientSocket.getOutputStream());
                    os = clientSocket.getOutputStream();

                    /* As long as we receive data, echo that data back to the client. */
                    while (!stop) {
                        is.read(bArr);
                        value = bArr[0];
                        System.out.println("From TCP: " + value + " : "+ bArr[0]);
                        if (etb != null)
                            etb.setetbConsole(value);
                    }
                } catch (IOException e) {
                  System.out.println(e);
                }
                
                try {
                    clientSocket.close();
                    is.close();
                    os.close();
                } catch (IOException ex) {
                    Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
                }
                  
                is = null;
                os = null;
                clientSocket = null;
                
            } while (!stop);
            is = null;
            os = null;
            clientSocket = null;
            btServer = null;
        }
    }
    
    
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("d-LIVER Console");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                ConsoleTcp.this.windowClosed(evt);
            }
        });

        jScrollPane1.setAutoscrolls(true);
        jScrollPane1.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jScrollPane1.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setFont(new java.awt.Font("Courier New", 0, 11)); // NOI18N
        jTextArea1.setLineWrap(true);
        jTextArea1.setRows(5);
        jScrollPane1.setViewportView(jTextArea1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 800, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1)
                    .addContainerGap()))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 700, Short.MAX_VALUE)
            .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                    .addContainerGap()
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 678, Short.MAX_VALUE)
                    .addContainerGap()))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void windowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_windowClosed
     if (etb != null) etb.removeEtbListener(this);
}//GEN-LAST:event_windowClosed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    // End of variables declaration//GEN-END:variables



    @Override
    public void skinTemperature(double temp, int timestamp) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void skinTemperatureInterval(int value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

    @Override
    public void battery(int battery, int timestamp) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
   }

    @Override
    public void testPattern(byte[] data, int timestamp) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void timeSync(int seq, int timestamp) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void manufacturer(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void model_number(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void serial_number(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void hw_revision(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void fw_revision(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbDateTime(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbPosition(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbTriageLevel(int value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbLocation(int value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbLocationId(String value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbConnectionInterval(int value) {
        // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void etbConsole(String value) {
        int firstByte = value.charAt(0);
        btPutChar(firstByte);
    }

    public void btPutChar(int value) {
        byte[] bArr = new byte[1];
        bArr[0] = (byte) value;
        
        if (os != null) {
            try {
                //os.print(ch);
                os.write(bArr);
            } catch (IOException ex) {
                Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
            }
            System.out.println("To TCP: " + value + " : "+ bArr[0]);
        }
    }


}
