package de.ustutt.iaas.cc.core.textProcessors;

import org.slf4j.Logger;

import de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies.AbstractLoadBalancerStrategy;

/**
 * A text processor that sends the text to one of a set of remote REST API for
 * processing (and balances the load depending on the load balancer strategy).
 */
public class RemoteTextProcessorMulti implements ITextProcessor {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RemoteTextProcessorMulti.class);
	private AbstractLoadBalancerStrategy strategy;

	public RemoteTextProcessorMulti(AbstractLoadBalancerStrategy strategy) {
		super();
		this.strategy = strategy;
		logger.info("Load balancer strategy: {}", strategy.getClass().getName());
	}

	@Override
	public String process(String text) {
		String processedText = strategy.executeAndGetResult(text);
		return processedText != null ? processedText : text;
	}
}