/*
 * Copyright 2017 user.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.profesorfalken.jpowershell;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.CheckBox;
import org.apache.commons.io.FilenameUtils;

/**
 *
 * @author mid11
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    public CheckBox application;
    public CheckBox hardware_events;
    public CheckBox internet_explorer;
    public CheckBox oalerts;
    public CheckBox key_mgmt;       
    public CheckBox media_center;
    public CheckBox security;
    public CheckBox system;
    public CheckBox powershell;
    public CheckBox eventLogs;
    public CheckBox firewallLogs;
    public CheckBox serviceLogs;
    int count=1;
 
    void writeLog(String logName){
//        Get-WinEvent -FilterHashtable @{Logname='Application';}| select TimeCreated,ID,Message
                File file1=new File("C:/Windows/System32/winevt/Logs/"+logName+".evtx");
                PowerShell ps=null;
                if (!file1.exists() || !file1.isFile()) {
                     System.out.println("Not exists");
                }
                else{
                    if(((file1.length())/1024)<=1){
                        System.out.println("Log file empty!");
                    }else{
//                            String s1="Get-EventLog -LogName \""+logName+"\" > \"C:\\Project\\templog.txt\" | select -ExpandProperty message";
//                            String s1="Get-WinEvent -FilterHashtable @{Logname=\'"+logName+"\';} > \"C:\\Project\\templog.txt\" ; select TimeCreated,ID,Message";
//                            String s1="Get-EventLog -LogName \"Application\"> \"C:\\Project\\templog.csv\" | select -ExpandProperty message";
                            String s1="Get-EventLog -LogName \""+logName+"\" > \"C:\\Project\\templog.txt\" | Format-Table -wrap -AutoSize";
                            try{
                                    ps=PowerShell.openSession();
                                    PowerShell.executeSingleCommand("mode con:cols=8000 lines=8000");
                                    PowerShell.executeSingleCommand(s1);
//                                    String output=pr.getCommandOutput();
//                                    System.out.println(output);
                                    ps.executeCommand("exit");
                            }catch(PowerShellNotAvailableException e){System.out.println(e);}
                            finally {
                                        if (ps!= null){
                                                ps.close();
                                        }
                            }
                            FlumeClient fc=new FlumeClient();
                            fc.connect(logName);
                    }
                }
    }
    private void eventLogs(){
        
        if(application.isSelected()){
            writeLog("Application");
        }
        
        if(hardware_events.isSelected()){
            writeLog("HardwareEvents");
//                        if(((file1.length())/1024)>2048){
////                            PowerShell.executeSingleCommand("Clear-EventLog \"HardwareEvents\"");
//                            try{
//                                ps=PowerShell.openSession();
//                                ps.executeSingleCommand("Clear-EventLog \"HardwareEvents\"");
//                                ps.executeCommand("exit");
//                            }catch(PowerShellNotAvailableException e){System.out.println(e);}
//                            finally {
//                                if (ps!= null){
//                                    ps.close();
//                                }
//                            }
//                        }
        }
        
        if(internet_explorer.isSelected()){
            writeLog("Internet Explorer");
        }
                
        if(key_mgmt.isSelected()){
            writeLog("Key Management Service");
        }
                        
       if(media_center.isSelected()){
            writeLog("Media Center");
        }
                                
        if(oalerts.isSelected()){
            writeLog("OAlerts");
        }
                                        
        if(security.isSelected()){
            writeLog("Security");
        }
                                                
        if(system.isSelected()){
            writeLog("System");
        }
       
        if(powershell.isSelected()){
            writeLog("Windows PowerShell");
        }
    }
    
    private void firewallLogs(){
        PowerShell ps = null;
        try{
            ps=PowerShell.openSession();
            PowerShell.executeSingleCommand("netsh advfirewall set allprofiles state on");
            PowerShell.executeSingleCommand("netsh advfirewall set allprofiles logging filename %systemroot%\\system32\\LogFiles\\Firewall\\pfirewall.log");
            PowerShell.executeSingleCommand("netsh advfirewall set allprofiles logging maxfilesize 4096");
            PowerShell.executeSingleCommand("netsh advfirewall set allprofiles logging droppedconnections enable");
            PowerShell.executeSingleCommand("netsh advfirewall set allprofiles logging allowedconnections enable");
            ps.executeCommand("exit");
           }catch(PowerShellNotAvailableException e){System.out.println(e);}
            finally {
                if (ps!= null){
                    ps.close();
                }
            }
        
        try{
		FileInputStream fi=new FileInputStream("C:\\Windows\\System32\\LogFiles\\Firewall\\pfirewall.log");
		BufferedInputStream bi=new BufferedInputStream(fi);
                FileOutputStream fo=new FileOutputStream("C:\\Project\\templog.txt");
		int i=0;
		while((i=bi.read())!=-1){
                    byte b[]=((char)i+"").getBytes();
                    fo.write(b);
		}
		fi.close();
       }catch(IOException e){
		System.out.println(e);
        }
        FlumeClient fc=new FlumeClient();
        fc.connect("Firewall");
    }
    
    private void serviceLogs(){
        
        String eventName;
        File f = new File("C:\\Windows\\System32\\winevt\\Logs");
        File[] files = f.listFiles();
        for (File file : files) {
            if(file.getName().contains("Microsoft-Windows")){
            
                file.setReadable(true);
                int a=file.getName().indexOf("%");
                if(a==-1){
                   eventName=FilenameUtils.removeExtension(file.getName());
                }
                else{
                   eventName=file.getName().substring(0, a);
                }
                if(((file.length())/1024)<=68){
                    continue;
                }
                
                String s="Get-WinEvent -FilterHashTable @{ProviderName=\""+eventName+"\";} > \"C:\\Project\\templog.txt\" ; ft timestamp, message -auto";
                PowerShell ps = null;
                try{
                    ps=PowerShell.openSession();
                    ps.executeSingleCommand(s);
                    ps.executeCommand("exit");
                }catch(PowerShellNotAvailableException e){System.out.println(e);}
                finally {
                    if (ps!= null){
                        ps.close();
                    }
                }
                FlumeClient fc=new FlumeClient();
                fc.connect(eventName);
            }
            else{
            }
        }
    }
    
    @FXML
    private void handleButtonAction(ActionEvent event) {
//        try 
//            { 
//                Runtime.getRuntime().exec("cmd.exe mode con:cols=8000 lines=8000");
//            }
//            catch(IOException e1) {e1.printStackTrace();}
//          ProcessBuilder p=new ProcessBuilder("cmd.exe", "/C", "mode con:cols=2000 lines=2000");
          new File("C:\\Project").mkdir();
          PowerShell.executeSingleCommand("mode con:cols=8000 lines=8000");
//        PowerShell.executeSingleCommand("New-Item -ItemType directory -Path C:\\Project");
        
        if(eventLogs.isSelected()||application.isSelected()||hardware_events.isSelected()||internet_explorer.isSelected()||key_mgmt.isSelected()||media_center.isSelected()||oalerts.isSelected()||security.isSelected()||system.isSelected()||powershell.isSelected()){
            eventLogs();          
        }
        if(firewallLogs.isSelected()){
            firewallLogs();
        }
        if(serviceLogs.isSelected()){
            serviceLogs();
        }
//        Alert alert = new Alert(AlertType.INFORMATION);
//        alert.setTitle("Operation Completed");
//        alert.setHeaderText(null);
//        alert.setContentText("Please check for files in C:\\Project");
//        alert.showAndWait();
    }
    
    @FXML
    private void handleCheckBoxAction(ActionEvent event){
        
        if(count%2!=0){
            application.setSelected(true);
            hardware_events.setSelected(true);
            internet_explorer.setSelected(true);
            oalerts.setSelected(true);
            key_mgmt.setSelected(true);
            media_center.setSelected(true);
            security.setSelected(true);
            system.setSelected(true);
            powershell.setSelected(true);
        }
        else{
            application.setSelected(false);
            hardware_events.setSelected(false);
            internet_explorer.setSelected(false);
            oalerts.setSelected(false);
            key_mgmt.setSelected(false);
            media_center.setSelected(false);
            security.setSelected(false);
            system.setSelected(false);
            powershell.setSelected(false);
        }
        count++;
    }
    
//    @FXML
//    private void handleFirewallAction(ActionEvent event){
//        Alert alert = new Alert(AlertType.INFORMATION);
//        alert.setTitle("ALERT");
//        alert.setHeaderText(null);
//        alert.setContentText("Please run command using 'administrator' to access this log");
//        //java -jar jar_path.jar
//        alert.showAndWait();
//        firewallLogs.setSelected(false);
//    }
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
    }    
}
