package labxx.common.settings;

import javax.jms.*;

/**
 * Just an Utility code to empty the entire Destination.
 * Only for code debug purpose, don't use in your project.
 */
public class EmptyDestinations {

  public static void empty(Destination destination){
    try (JMSContext jmsContext = CommonSettings.getConnectionFactory().createContext()) {
      try {
        JMSConsumer consumer = jmsContext.createConsumer(destination);
        while (true) {
          Message message = consumer.receive(1000);
          if (message == null) {
            break;
          }
        }
        System.out.println("Destination emptied");
      } catch (Exception e) {
        e.printStackTrace();
      }
    }
  }
}