package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

import de.ustutt.iaas.cc.TextProcessorConfiguration;

/**
 * A factory which returns a load balancer strategy depending on the strategy
 * configuration.
 * 
 *
 */
public class LoadBalancerStrategyFactory {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoadBalancerStrategyFactory.class);

	public AbstractLoadBalancerStrategy getLoadBalacerStrategy(
			TextProcessorConfiguration.LoadBalancerStrategy strategyConfig, List<String> textProcessorResources,
			Client client) {
		AbstractLoadBalancerStrategy strategy = null;
		List<WebTarget> targets = new ArrayList<>();
		textProcessorResources.forEach(resource -> {
			targets.add(client.target(resource));
		});
		switch (strategyConfig) {
		case roundRobin:
			logger.info("Round Robin");
			strategy = new RoundRobin(targets);
			break;
		case leastConnection:
			logger.info("Least Connection");
			strategy = new LeastConnection(targets);
			break;
		default:
			logger.info("Random");
			strategy = new RandomStrategy(targets);
		}
		return strategy;
	}
}
