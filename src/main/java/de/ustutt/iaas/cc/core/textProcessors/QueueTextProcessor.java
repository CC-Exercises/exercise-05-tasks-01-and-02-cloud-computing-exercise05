package de.ustutt.iaas.cc.core.textProcessors;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Queue;
import javax.jms.QueueConnection;
import javax.jms.QueueConnectionFactory;
import javax.jms.QueueReceiver;
import javax.jms.QueueSender;
import javax.jms.QueueSession;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazonaws.auth.PropertiesFileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.google.api.client.repackaged.com.google.common.base.Strings;

import de.ustutt.iaas.cc.TextProcessorConfiguration;

/**
 * A text processor that uses JMS to send text to a request queue and then waits
 * for the processed text on a response queue. For each text processing request,
 * a unique ID is generated that is later used to correlate responses to their
 * original request.
 * <p>
 * The text processing is realized by (one or more) workers that read from the
 * request queue and write to the response queue.
 * <p>
 * This implementation supports ActiveMQ as well as AWS SQS.
 * 
 * @author hauptfn
 *
 */
public class QueueTextProcessor implements ITextProcessor {

	private final static Logger logger = LoggerFactory.getLogger(QueueTextProcessor.class);
	private QueueConnectionFactory conFactory;
	private QueueConnection connection;
	private QueueSession session;
	private Queue requestQueue;
	private Queue responseQueue;
	private QueueSender sender;
	private QueueReceiver receiver;

	private static final String ID_Prop = "MsgID";
	private static ConcurrentMap<String, CompletableFuture<String>> jobsMap = new ConcurrentHashMap<String, CompletableFuture<String>>();

	public QueueTextProcessor(TextProcessorConfiguration conf) {
		super();
		logger.debug("Initializing QueueTextProcessor.");

		try {
			switch (conf.mom) {
			case SQS:
				initSQS(conf);
				break;
			case ActiveMQ:
				initActiveMQ(conf);
				break;
			}
			// create sender and receiver
			sender = session.createSender(requestQueue);
			receiver = session.createReceiver(responseQueue);
			receiver.setMessageListener(new ML());
			// start connection (!)
			connection.start();

		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initSQS(TextProcessorConfiguration conf) {
		// http://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-java-message-service-jms-client.html
		try {
			// Create the connection factory using the properties file
			// credential provider.
			conFactory = SQSConnectionFactory.builder().withRegion(Region.getRegion(Regions.US_WEST_2))
					.withAWSCredentialsProvider(new PropertiesFileCredentialsProvider("aws.properties")).build();
			// create connection.
			connection = conFactory.createQueueConnection();
			// create session
			// https://docs.oracle.com/javaee/7/api/javax/jms/Session.html
			session = connection.createQueueSession(false, Session.AUTO_ACKNOWLEDGE);
			// lookup queue (has to be created before, using e.g. the AWS
			// management console)
			requestQueue = session.createQueue(conf.requestQueueName);
			responseQueue = session.createQueue(conf.responseQueueName);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void initActiveMQ(TextProcessorConfiguration conf) {
		// TODO
	}

	@Override
	public String process(String text) {
		String id = UUID.randomUUID().toString();
		CompletableFuture<String> job = new CompletableFuture<String>();
		jobsMap.put(id, job);

		try {
			TextMessage msg = session.createTextMessage(text);
			msg.setStringProperty(ID_Prop, id);
			logger.info("Send -> {}: {}, Text: {}", ID_Prop, id, text);
			sender.send(msg);
		} catch (JMSException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			String proccessedText = job.get(10, TimeUnit.SECONDS);
			return proccessedText;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TimeoutException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return text;
	}

	private static class ML implements MessageListener {
		@Override
		public void onMessage(Message message) {
			logger.debug("Message Listener");
			if (message instanceof TextMessage) {
				try {
					String id = message.getStringProperty(ID_Prop);
					if (Strings.isNullOrEmpty(id)) {
						logger.warn("Received message has no {}.", ID_Prop);
						return;
					}

					CompletableFuture<String> job = jobsMap.get(id);
					if (job == null) {
						logger.warn("Completable Future for {}: {} is null", ID_Prop, id);
						return;
					}

					String receivedMessage = ((TextMessage) message).getText();

					job.complete(receivedMessage);
					jobsMap.remove(id);
					logger.info("Received -> {}: {}, Text: {}", ID_Prop, id, receivedMessage);
				} catch (JMSException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
