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
import java.nio.charset.Charset;
import org.apache.flume.Event;
import org.apache.flume.EventDeliveryException;
import org.apache.flume.api.RpcClient;
import org.apache.flume.api.RpcClientFactory;
import org.apache.flume.event.EventBuilder;

public class MyRpcClient {
  public RpcClient client;
  public String hostname;
  public int port;

  public MyRpcClient() {
  }
  
  public void init(String hostname, int port) {
    // Setup the RPC connection
    this.hostname = hostname;
    this.port = port;
    this.client = RpcClientFactory.getDefaultInstance(hostname, port);
    // Use the following method to create a thrift client (instead of the above line):
    // this.client = RpcClientFactory.getThriftInstance(hostname, port);
  }

  public void sendDataToFlume(String data) {
    
    // Create a Flume Event object that encapsulates the sample data
    Event event = EventBuilder.withBody(data, Charset.forName("ISO-8859-1"));

    // Send the event
    try {
      client.append(event);
    } catch (EventDeliveryException e) {
      // clean up and recreate the client
      client.close();
      client = null;
      client = RpcClientFactory.getDefaultInstance(hostname, port);
      // Use the following method to create a thrift client (instead of the above line):
      // this.client = RpcClientFactory.getThriftInstance(hostname, port);
    }
  }

  public void cleanUp() {
    // Close the RPC connection
    client.close();
  }
}
