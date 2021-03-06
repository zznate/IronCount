package com.jointhegrid.ironcount.mockingbird;

import com.jointhegrid.ironcount.MessageHandler;
import com.jointhegrid.ironcount.StringSerializer;
import com.jointhegrid.ironcount.Workload;
import java.net.URI;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Stack;
import kafka.message.Message;
import me.prettyprint.hector.api.Cluster;
import me.prettyprint.hector.api.Keyspace;
import me.prettyprint.hector.api.beans.Composite;
import me.prettyprint.hector.api.factory.HFactory;
/*
 * http://www.slideshare.net/kevinweil/rainbird-realtime-analytics-at-twitter-strata-2011
 *
 */
public class MockingBirdMessageHandler implements MessageHandler{

  private Workload w;
  Cluster cluster ;
  Keyspace keyspace;
  DateFormat bucketByMinute = new SimpleDateFormat("yyyy-MM-dd-HH-mm");

  public MockingBirdMessageHandler(){}

  @Override
  public void setWorkload(Workload w) {
    this.w=w;
    cluster = HFactory.getOrCreateCluster("mocking", w.properties.get("mocking.cas"));
    keyspace = HFactory.createKeyspace(w.properties.get("mocking.ks"), cluster);
  }

  @Override
  /* message here should be an url formatted as a string
   http://sub.domain.com/myurl.s becomes
   incr com by 1
   incr com/domain by 1
   incr com/domain/sub by 1
   incr com/domain/sub/myurl.s by 1

   */

  public void handleMessage(Message m) {
     System.err.println("4");
    String url = getMessage(m);
    URI i = URI.create(url);
    String domain=i.getHost();
    String path = i.getPath();
    String [] parts = domain.split("\\.");
    Stack<String> s = new Stack<String>();
    s.add(path);
    s.addAll(Arrays.asList(parts));
    StringBuilder sb = new StringBuilder();

    for (int j=0;j<=parts.length;j++){
      sb.append(s.pop());
      countIt( sb.toString());
      sb.append(":");
    }
  }

  public void countIt(String s){
  
  
  }

  public static String getMessage(Message message) {
    ByteBuffer buffer = message.payload();
    byte[] bytes = new byte[buffer.remaining()];
    buffer.get(bytes);
    return new String(bytes);
  }

}
