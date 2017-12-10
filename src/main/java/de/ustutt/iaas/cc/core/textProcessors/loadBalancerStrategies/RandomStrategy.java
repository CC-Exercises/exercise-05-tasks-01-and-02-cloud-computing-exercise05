package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.List;
import java.util.Random;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

/**
 * Load balancer strategy which chooses a server randomly and sends the request
 * to it.
 *
 */
public class RandomStrategy extends AbstractLoadBalancerStrategy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RandomStrategy.class);

	private static final Random randomGenerator = new Random();
	private static List<WebTarget> targets;

	public RandomStrategy(List<WebTarget> targets) {
		RandomStrategy.targets = targets;
		logger.debug("Number of Web Targets alias TextProcessors: {}", targets.size());
	}

	@Override
	protected WebTarget getNextTarget() {
		int index = randomGenerator.nextInt(targets.size());
		logger.debug("Next index: " + index);
		return targets.get(index);
	}

	@Override
	public void executeStrategy(String text) {
		WebTarget target = this.getNextTarget();
		this.executePOSTRequest(target, text);
	}
}
