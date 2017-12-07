package de.ustutt.iaas.cc.core.textProcessors;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;

import de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies.ILoadBalancerStrategy;

/**
 * A text processor that sends the text to one of a set of remote REST API for
 * processing (and balances the load depending on the load balancer strategy).
 */
public class RemoteTextProcessorMulti implements ITextProcessor {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RemoteTextProcessorMulti.class);
	private ILoadBalancerStrategy strategy;

	public RemoteTextProcessorMulti(ILoadBalancerStrategy strategy) {
		super();
		this.strategy = strategy;
		logger.info("Load balancer strategy: {}", strategy.getClass().getName());
	}

	@Override
	public String process(String text) {
		WebTarget target = strategy.getNextTarget();
		String processedText = postRequest(target, text);
		return processedText;
	}

	private String postRequest(WebTarget target, String text) {
		return target.request(MediaType.TEXT_PLAIN).post(Entity.entity(text, MediaType.TEXT_PLAIN), String.class);
	}
}