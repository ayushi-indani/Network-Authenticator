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

import java.net.InetAddress;
import java.net.NetworkInterface;
/**
 *
 * @author user
 */
public class Host {
    static String getSystemName(){  
        try{
            InetAddress inetaddress=InetAddress.getLocalHost(); //Get LocalHost refrence
            String name = inetaddress.getHostName();   //Get Host Name
            return name;   //return Host Name
        }
        catch(Exception E){
            E.printStackTrace();  //print Exception StackTrace
            return null;
        }
    }
     
    /**
     * method to get Host IP
     * @return Host IP Address
     */
        static String getIPAddress(){
        try{
            InetAddress inetaddress=InetAddress.getLocalHost();  //Get LocalHost refrence
            String ip = inetaddress.getHostAddress();  // Get Host IP Address
            return ip;   // return IP Address
        }
        catch(Exception E){
            E.printStackTrace();  //print Exception StackTrace
            return null;
        }
         
    }
     
    /**
     * method to get Host Mac Address
     * @return  Mac Address
     */
        static String getMAC(){
         try{
            InetAddress inetaddress=InetAddress.getLocalHost(); //Get LocalHost refrence
             
            //get Network interface Refrence by InetAddress Refrence
            NetworkInterface network = NetworkInterface.getByInetAddress(inetaddress); 
            byte[] macArray = network.getHardwareAddress();  //get Harware address Array
            StringBuilder str = new StringBuilder();
             
            // Convert Array to String 
            for (int i = 0; i < macArray.length; i++) {
                    str.append(String.format("%02X%s", macArray[i], (i < macArray.length - 1) ? "-" : ""));
            }
            String macAddress=str.toString();
         
            return macAddress; //return MAc Address
        }
        catch(Exception E){
            E.printStackTrace();  //print Exception StackTrace
            return null;
        } 
    }
}