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
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author user
 */
public class FlumeClient {
    public void connect(String header) {
        
            MyRpcClient client = new MyRpcClient();
            // Initialize client with the remote Flume agent's host and port
            client.init("192.168.0.128", 8995);
            System.gc();
            StringBuilder s=new StringBuilder();
            String host_name=Host.getSystemName();
            StringBuilder host_ip=new StringBuilder();
            host_ip.append(Host.getIPAddress());
            String host_mac=Host.getMAC();
            String system_info = host_name + " " + host_mac + " ";
            system_info = system_info + host_ip;
            
            try{
                 FileInputStream fi= new FileInputStream("C:\\Project\\templog.txt");
                 BufferedInputStream bi=new BufferedInputStream(fi);
                   
                 String ss;
                 BufferedReader r = new BufferedReader(new InputStreamReader(bi, StandardCharsets.UTF_8));
                 s.append("\n"+"Logname-"+header+"\n");
                 while((ss = r.readLine())!= null){
                     
                     if(ss.length()>1){
                         s=s.append(system_info);
                     }
                     ss=ss.trim();
                     s=s.append(ss);
                     
                 
                 }
                 
                 int i=0;
                 /*
                 while((i=bi.read())!=-1){
                    //s=s.append(system_info);
                    s=s.append((char)i);
                 }*/
                 System.out.println(s.toString());
                 client.sendDataToFlume(s.toString());
            }catch(Exception e){System.out.println(e);}
    }
}