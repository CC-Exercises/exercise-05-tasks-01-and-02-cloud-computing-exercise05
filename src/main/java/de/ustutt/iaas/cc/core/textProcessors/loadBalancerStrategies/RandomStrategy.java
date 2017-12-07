package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.List;
import java.util.Random;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

public class RandomStrategy implements ILoadBalancerStrategy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RandomStrategy.class);

	private final Random randomGenerator = new Random();
	private List<WebTarget> targets;

	public RandomStrategy(List<WebTarget> targets) {
		this.targets = targets;
		logger.debug("Number of Web Targets alias TextProcessors: {}", targets.size());
	}

	@Override
	public WebTarget getNextTarget() {
		int index = randomGenerator.nextInt(targets.size());
		logger.debug("Next index: " + index);
		return targets.get(index);
	}

}
