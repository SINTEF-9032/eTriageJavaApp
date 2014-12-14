/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.thingml.eTriage.desktop;

/**
 *
 * @author steffend
 */
public interface ConsoleTcpInterface {
    void statusTxt (String txt);
    void rxFromTcp (char ch);
}
