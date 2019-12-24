package labxx.common.settings;

import javax.jms.ConnectionFactory;
import javax.jms.Queue;
import javax.jms.Topic;
import javax.naming.InitialContext;
import javax.naming.NamingException;

public class CommonSettings {
  private static ConnectionFactory CONNECTION_FACTORY = null;
  private static Queue PTP_QUEUE = null;
  private static Topic PUB_SUB_TOPIC = null;
  private static Queue DEFAULT_REPLY_QUEUE = null;

  static {
    try {
      InitialContext initialContext = new InitialContext();
      CONNECTION_FACTORY = (ConnectionFactory) initialContext.lookup("jms/__defaultConnectionFactory");
      PTP_QUEUE = (Queue) initialContext.lookup("jms/PTPQueue");
      DEFAULT_REPLY_QUEUE = (Queue) initialContext.lookup("jms/ReplyQueue");
      PUB_SUB_TOPIC = (Topic) initialContext.lookup("jms/PubSubTopic");
    } catch (NamingException e) {
      e.printStackTrace();
    }
  }

  public static ConnectionFactory getConnectionFactory() {
    return CONNECTION_FACTORY;
  }

  public static Queue getDefaultQueue() {
    return PTP_QUEUE;
  }

  public static Queue getDefaultReplyQueue() {
    return DEFAULT_REPLY_QUEUE;
  }

  public static Topic getDefautTopic() {
    return PUB_SUB_TOPIC;
  }
}
