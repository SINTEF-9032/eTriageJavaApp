/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author steffend
 */
package org.thingml.eTriage.desktop;

import java.net.*;
import java.io.*;
import java.util.logging.Level;
import java.util.logging.Logger;


public class ConsoleTcp implements Runnable{
    protected int serverPortNum;
    protected boolean stop;
    protected OutputStream toTcp = null;
    protected InputStream fromTcp = null;

    protected ConsoleTcpInterface ifFrame;

    public ConsoleTcp(int tcpPort, ConsoleTcpInterface ifFrame) {
        this.ifFrame = ifFrame;
        serverPortNum = tcpPort;
        this.stop = true;
        
        startTcp();
    }
    
    public void SetTcpPort ( int tcpPort) {
        serverPortNum = tcpPort;
    }

    public void startTcp() {
        if (stop == true) {
            stop = false;
            new Thread(this).start();
        }
    }
    
    public void stopTcp() {
        if (stop != true) {
            stop = true;
        }
    }

    public void sendToTcp(char ch) {
        byte[] txArr = new byte[1];
        txArr[0] = (byte) ch;
        if (toTcp != null) {
            try { 
                toTcp.write(txArr);
            } catch (IOException ex) {
                Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    @Override
    public void run() {
        ServerSocket serverSocket = null;
        Socket server = null;
        byte[] rxArr = new byte[1];
        
        while(stop == false) {
            try {
                if (serverSocket == null) {
                    serverSocket = new ServerSocket(serverPortNum);
                    serverSocket.setSoTimeout(2000);  // Timeout for wait connection
                }
                //System.out.println("Waiting for client on port " + serverSocket.getLocalPort() + "..."); 
                ifFrame.statusTxt("Listen at port " + serverSocket.getLocalPort() + "..."); 

                server = serverSocket.accept();
                
                server.setSoTimeout(2000);  // Timeout for wait read
                toTcp = server.getOutputStream();
                fromTcp = server.getInputStream();
                //System.out.println("Just connected to " + server.getRemoteSocketAddress()); 
                ifFrame.statusTxt("Connected to " + serverSocket.getLocalPort()); 
            }
            catch(SocketTimeoutException et) {
                //System.out.println("Listen timeout " + serverSocket.getLocalPort()); 
                if (serverPortNum != serverSocket.getLocalPort()) {
                    //System.out.println("Change listen port ... "); 
                    ifFrame.statusTxt("Change listen port ... "); 
                    try {
                        serverSocket.close();
                        serverSocket = null;
                    } catch (IOException ex) {
                        Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            catch(UnknownHostException ex) {
                    ex.printStackTrace();
            }
            catch(IOException e){
                    e.printStackTrace();
            }
            
            
            
            if (server != null) {
                while(server != null) {
                    try {
                        int ret = fromTcp.read(rxArr);
                        if (ret != -1) {
                            char ch = (char)rxArr[0];
                            //System.out.println("Server received: " + rxArr[0] + " : " + ch); 
                            ifFrame.rxFromTcp(ch); 
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException ex) {
                                Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        } else {
                            toTcp.close();
                            fromTcp.close();
                            server.close();
                            toTcp = null;
                            fromTcp = null;
                            server = null;
                            //System.out.println("Remote connection is closed");
                            ifFrame.statusTxt("Remote connection is closed");
                        }
                    } catch(SocketTimeoutException et) {
                        //System.out.println("Read timeout " + serverSocket.getLocalPort()); 
                        if (serverPortNum != serverSocket.getLocalPort()) {
                            //System.out.println("Change listen port 2 ... "); 
                            ifFrame.statusTxt("Close connection and change listen port ... "); 
                            try {
                                toTcp.close();
                                fromTcp.close();
                                server.close();
                                serverSocket.close();
                                toTcp = null;
                                fromTcp = null;
                                server = null;
                                serverSocket = null;
                            } catch (IOException ex) {
                                Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    } catch (IOException ex) {
                        Logger.getLogger(ConsoleTcp.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        }
    }


}
