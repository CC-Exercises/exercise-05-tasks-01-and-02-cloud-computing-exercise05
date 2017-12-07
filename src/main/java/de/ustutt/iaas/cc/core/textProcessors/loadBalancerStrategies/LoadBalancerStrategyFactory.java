package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

import de.ustutt.iaas.cc.TextProcessorConfiguration;

public class LoadBalancerStrategyFactory {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LoadBalancerStrategyFactory.class);

	public ILoadBalancerStrategy getLoadBalacerStrategy(TextProcessorConfiguration.LoadBalancerStrategy paramStrategy,
			List<String> textProcessorResources, Client client) {
		ILoadBalancerStrategy strategy = null;
		List<WebTarget> targets = new ArrayList<>();
		textProcessorResources.forEach(resource -> {
			targets.add(client.target(resource));
		});
		switch (paramStrategy) {
		case roundRobin:
			logger.info("Round Robin");
			strategy = new RoundRobin(targets);
			break;
		default:
			logger.info("Random");
			strategy = new RandomStrategy(targets);
		}
		return strategy;
	}
}
