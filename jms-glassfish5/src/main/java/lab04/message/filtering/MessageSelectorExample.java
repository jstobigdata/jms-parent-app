package lab04.message.filtering;

import labxx.common.settings.CommonSettings;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.util.UUID;

public class MessageSelectorExample {
  private static ConnectionFactory connectionFactory = null;
  private static Queue defaultQueue = null;

  static {
    connectionFactory = CommonSettings.getConnectionFactory();
    defaultQueue = CommonSettings.getDefaultQueue();
  }

  @Test
  public void messageFilterOnProperties() throws JMSException, InterruptedException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      LogEvent event1 = new LogEvent("Sample ERROR Log", UUID.randomUUID().toString(), LogLevel.ERROR);
      LogEvent event2 = new LogEvent("Sample DEBUG Log", UUID.randomUUID().toString(), LogLevel.DEBUG);
      LogEvent event3 = new LogEvent("Sample INFO Log", UUID.randomUUID().toString(), LogLevel.INFO);
      LogEvent event4 = new LogEvent("Sample WARN Log", UUID.randomUUID().toString(), LogLevel.WARN);

      //NOTE - If you keep "logLevel = DEBUG", it will not work!
      JMSConsumer consumer = jmsContext.createConsumer(defaultQueue, "logLevel = 'DEBUG'");
      consumer.setMessageListener(msg -> {
        System.out.println(msg);
        try {
          LogEvent event = (LogEvent) ((ObjectMessage) msg).getObject();
          System.out.println(event);
        } catch (JMSException e) {
          e.printStackTrace();
        }
      });

      JMSProducer producer = jmsContext.createProducer();
      //send event1
      ObjectMessage objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event1);
      objectMessage.setStringProperty("logLevel", event1.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      //Send event2
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event2);
      objectMessage.setStringProperty("logLevel", event2.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      //Send event3
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event3);
      objectMessage.setStringProperty("logLevel", event3.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      //Send event4
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event4);
      objectMessage.setStringProperty("logLevel", event4.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      Thread.sleep(2000);
      consumer.close();
    }
  }

  @Test
  public void messageFilterOnHeader() throws JMSException, InterruptedException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      LogEvent event1 = new LogEvent("Sample ERROR Log", UUID.randomUUID().toString(), LogLevel.ERROR);
      LogEvent event2 = new LogEvent("Sample DEBUG Log", UUID.randomUUID().toString(), LogLevel.DEBUG);
      LogEvent event3 = new LogEvent("Sample INFO Log", UUID.randomUUID().toString(), LogLevel.INFO);
      LogEvent event4 = new LogEvent("Sample WARN Log", UUID.randomUUID().toString(), LogLevel.WARN);

      JMSConsumer consumer = jmsContext.createConsumer(defaultQueue, "logLevel = 'ERROR' OR JMSPriority BETWEEN 5 AND 9");
      consumer.setMessageListener(msg -> {
        System.out.println(msg);
        try {
          LogEvent event = (LogEvent) ((ObjectMessage) msg).getObject();
          System.out.println(event);
        } catch (JMSException e) {
          e.printStackTrace();
        }
      });

      JMSProducer producer = jmsContext.createProducer();
      //send event1
      ObjectMessage objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event1);
      objectMessage.setStringProperty("logLevel", event1.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      //Send event2
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event2);
      objectMessage.setStringProperty("logLevel", event2.getLogLevel().name());
      producer.send(defaultQueue, objectMessage);

      //Send event3
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event3);
      objectMessage.setStringProperty("logLevel", event3.getLogLevel().name());
      producer.setPriority(5);
      producer.send(defaultQueue, objectMessage);

      //Send event4
      objectMessage = jmsContext.createObjectMessage();
      objectMessage.setObject(event4);
      objectMessage.setStringProperty("logLevel", event4.getLogLevel().name());
      //Reset to normal priority
      producer.setPriority(4);
      producer.send(defaultQueue, objectMessage);

      Thread.sleep(2000);
      consumer.close();
    }
  }
}
