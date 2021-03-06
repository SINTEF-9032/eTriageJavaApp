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

/*
 * FileLoggerForm.java
 *
 * Created on 4 juil. 2012, 21:17:07
 */
package org.thingml.eTriage.desktop;

import java.io.File;
import java.util.prefs.Preferences;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import org.thingml.etriage.driver.Etriage;

/**
 *
 * @author franck
 */
public class FileLoggerForm extends javax.swing.JFrame {

    JFileChooser chooser = new JFileChooser();
    Preferences prefs = Preferences.userRoot().node(this.getClass().getName());
    Etriage etb;
    EtriageFileLogger logger;
    
    /** Creates new form FileLoggerForm */
    public FileLoggerForm(Etriage b) {
        this.etb = b;
        initComponents();
        chooser.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
        jTextFieldFolder.setText(prefs.get("LogFolder", ""));
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTextFieldFolder = new javax.swing.JTextField();
        jButton1 = new javax.swing.JButton();
        jLabel1 = new javax.swing.JLabel();
        jButtonRecord = new javax.swing.JButton();
        jButtonStop = new javax.swing.JButton();
        jButtonOscLog = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle("eTriage File Logger");
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosed(java.awt.event.WindowEvent evt) {
                formWindowClosed(evt);
            }
        });

        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/folder-18.png"))); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        jLabel1.setText("Output Folder :");

        jButtonRecord.setIcon(new javax.swing.ImageIcon(getClass().getResource("/play-2.png"))); // NOI18N
        jButtonRecord.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonRecordActionPerformed(evt);
            }
        });

        jButtonStop.setIcon(new javax.swing.ImageIcon(getClass().getResource("/pause-2.png"))); // NOI18N
        jButtonStop.setEnabled(false);
        jButtonStop.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopActionPerformed(evt);
            }
        });

        jButtonOscLog.setText("OscLog");
        jButtonOscLog.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonOscLogActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextFieldFolder, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton1))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addComponent(jButtonOscLog)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonStop)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButtonRecord)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(jLabel1)
                    .addComponent(jButton1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextFieldFolder, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 48, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jButtonRecord, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jButtonStop, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jButtonOscLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
    File folder = new File(jTextFieldFolder.getText());
    if (folder.exists() && folder.isDirectory()) chooser.setSelectedFile(folder);
    if (chooser.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
        jTextFieldFolder.setText(chooser.getSelectedFile().getAbsolutePath());
        prefs.put("LogFolder", folder.getAbsolutePath());
    }
}//GEN-LAST:event_jButton1ActionPerformed

private void jButtonRecordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonRecordActionPerformed
    File folder = new File(jTextFieldFolder.getText());
    if (!folder.exists() || !folder.isDirectory()) {
        if (chooser.showDialog(this, "OK") == JFileChooser.APPROVE_OPTION) {
             if (!chooser.getSelectedFile().exists() || !chooser.getSelectedFile().isDirectory()) {
                JOptionPane.showMessageDialog(null, "Please select an existing folder.", "Folder not found", JOptionPane.ERROR_MESSAGE);
                return;
            }
            jTextFieldFolder.setText(chooser.getSelectedFile().getAbsolutePath());
            folder = chooser.getSelectedFile();
        }
        else return; // abort
    }
    prefs.put("LogFolder", folder.getAbsolutePath());
    logger = new EtriageFileLogger(folder, etb);
    etb.addEtbListener(logger);
    logger.startLogging();
    jButtonRecord.setEnabled(false);
    jButtonStop.setEnabled(true);
}//GEN-LAST:event_jButtonRecordActionPerformed

private void jButtonStopActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopActionPerformed
    if (logger != null) {
        etb.removeEtbListener(logger);
        logger.stopLogging();
        logger = null;
    }
    jButtonRecord.setEnabled(true);
    jButtonStop.setEnabled(false);
}//GEN-LAST:event_jButtonStopActionPerformed

private void formWindowClosed(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosed
    if (logger != null) {
        etb.removeEtbListener(logger);
        logger.stopLogging();
        logger = null;
    }
}//GEN-LAST:event_formWindowClosed

    private void jButtonOscLogActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonOscLogActionPerformed
        UDPLoggerForm form = new UDPLoggerForm(etb);
        form.pack();
        form.setVisible(true);
    }//GEN-LAST:event_jButtonOscLogActionPerformed


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonOscLog;
    private javax.swing.JButton jButtonRecord;
    private javax.swing.JButton jButtonStop;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JTextField jTextFieldFolder;
    // End of variables declaration//GEN-END:variables
}
