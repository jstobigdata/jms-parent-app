package lab01.message.model;

import labxx.common.settings.CommonSettings;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.jms.*;
import java.io.Serializable;
import java.util.StringJoiner;
import java.util.UUID;

/**
 * Test - TextMessage, ByteMessage, ObjectMessage, StreamMessage, MapMessage.
 */
public class MessageTypesTest {

  private static ConnectionFactory connectionFactory = null;
  private static Queue queue = null;

  @BeforeAll
  public static void setUp() {
    connectionFactory = CommonSettings.getConnectionFactory();
    queue = CommonSettings.getDefaultQueue();
  }

  /**
   * @throws JMSException
   */
  @Test
  public void testTextMessage() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      TextMessage message = jmsContext.createTextMessage("Test TextMessage Type");
      producer.send(queue, message);

      TextMessage receivedMessage = (TextMessage) jmsContext.createConsumer(queue).receive();
      System.out.println("Received message: " + receivedMessage.getText());

    }
  }

  @Test
  public void testByteMessage() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      BytesMessage message = jmsContext.createBytesMessage();
      message.writeUTF("नमस्ते");
      message.writeBoolean(true);
      message.writeLong(12345L);
      producer.send(queue, message);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      BytesMessage receivedMessage = (BytesMessage) consumer.receive();
      System.out.println("==== ByteMessage Demo ====");
      System.out.println(receivedMessage.readUTF());
      System.out.println(receivedMessage.readBoolean());
      System.out.println(receivedMessage.readLong());
    }
  }

  @Test
  public void testStreamMessage() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      StreamMessage message = jmsContext.createStreamMessage();
      message.writeString("String Content");
      message.writeString("Another string");
      message.writeInt(101);
      producer.send(queue, message);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      StreamMessage receivedMessage = (StreamMessage) consumer.receive();
      System.out.println("===== StreamMessage =====");
      System.out.println(receivedMessage.readString());
      System.out.println(receivedMessage.readString());
      System.out.println(receivedMessage.readInt());
    }
  }

  @Test
  public void testMapMessage() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      MapMessage message = jmsContext.createMapMessage();
      message.setString("sampleKey", "sampleValue");
      producer.send(queue, message);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      MapMessage receivedMessage = (MapMessage) consumer.receive();
      System.out.println("===== MapMessage Demo =====");
      System.out.println(receivedMessage.getString("sampleKey"));
    }
  }

  @Test
  public void testObjectMessage() throws JMSException {
    try (JMSContext jmsContext = connectionFactory.createContext()) {
      JMSProducer producer = jmsContext.createProducer();
      ObjectMessage message = jmsContext.createObjectMessage();
      message.setObject(new UserCommand("John", "john@gmail.com"));
      producer.send(queue, message);

      JMSConsumer consumer = jmsContext.createConsumer(queue);
      ObjectMessage receivedMessage = (ObjectMessage) consumer.receive();
      System.out.println("===== ObjectMessage Demo =====");
      System.out.println(receivedMessage.getObject());
    }
  }


  private static class UserCommand implements Serializable {
    private String id;
    private String name;
    private String email;

    public UserCommand(String name, String email) {
      id = UUID.randomUUID().toString();
      this.name = name;
      this.email = email;
    }

    @Override
    public String toString() {
      return new StringJoiner(", ", UserCommand.class.getSimpleName() + "[", "]")
          .add("id='" + id + "'")
          .add("name='" + name + "'")
          .add("email='" + email + "'")
          .toString();
    }
  }
}
