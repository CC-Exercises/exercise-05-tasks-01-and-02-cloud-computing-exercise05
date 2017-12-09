package de.ustutt.iaas.cc.core.textProcessors;

import org.slf4j.Logger;

import de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies.AbstractLoadBalancerStrategy;

/**
 * A text processor that sends the text to one of a set of remote REST API for
 * processing (and balances the load depending on the load balancer strategy).
 */
public class RemoteTextProcessorMulti implements ITextProcessor {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RemoteTextProcessorMulti.class);
	private AbstractLoadBalancerStrategy strategyExecutor;

	public RemoteTextProcessorMulti(AbstractLoadBalancerStrategy strategyExecutor) {
		super();
		this.strategyExecutor = strategyExecutor;
		logger.info("Load balancer strategy: {}", strategyExecutor.getClass().getName());
	}

	@Override
	public String process(String text) {
		strategyExecutor.executeStrategy(text);
		String processedText = strategyExecutor.getResponse();
		return processedText != null ? processedText : text;
	}
}