package lab04.message.filtering;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.StringJoiner;

public class LogEvent implements Serializable {
  //Timestamp is the primary Key
  private LocalDateTime id;

  //Log Body
  private String body;

  //Unique Server Id
  private String machineId;

  private LogLevel logLevel;

  public LogEvent() {
    super();
  }

  public LogEvent(String body, String machineId, LogLevel logLevel) {
    this.id = LocalDateTime.now();
    this.body = body;
    this.machineId = machineId;
    this.logLevel = logLevel;
  }

  public LocalDateTime getId() {
    return id;
  }

  public void setId(LocalDateTime id) {
    this.id = id;
  }

  public String getBody() {
    return body;
  }

  public void setBody(String body) {
    this.body = body;
  }

  public String getMachineId() {
    return machineId;
  }

  public void setMachineId(String machineId) {
    this.machineId = machineId;
  }

  public LogLevel getLogLevel() {
    return logLevel;
  }

  public void setLogLevel(LogLevel logLevel) {
    this.logLevel = logLevel;
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", LogEvent.class.getSimpleName() + "[", "]")
        .add("id=" + id)
        .add("body='" + body + "'")
        .add("machineId='" + machineId + "'")
        .add("logLevel=" + logLevel)
        .toString();
  }
}
