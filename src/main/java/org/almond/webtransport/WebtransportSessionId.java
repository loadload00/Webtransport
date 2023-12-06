package org.almond.webtransport;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class WebtransportSessionId {

  private static final ConcurrentMap<String, Long> map = new ConcurrentHashMap<>();

  public long get(String key) {
    return map.get(key);
  }

  public void set(String key, long sessionID) {
    map.put(key, sessionID);
  }

  public void remove(String key) {
    map.remove(key);
  }

}
