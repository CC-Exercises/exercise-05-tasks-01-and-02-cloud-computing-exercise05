package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

/**
 * Load balancer strategy which chooses the server alternately and sends the
 * request to it.
 *
 */
public class RoundRobin extends AbstractLoadBalancerStrategy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RoundRobin.class);

	private AtomicInteger index = new AtomicInteger();
	private static List<WebTarget> targets;

	public RoundRobin(List<WebTarget> targets) {
		RoundRobin.targets = targets;
		logger.debug("Number of Web Targets alias TextProcessors: {}", targets.size());
	}

	@Override
	protected WebTarget getNextTarget() {
		if (index.get() == targets.size()) {
			index.set(0);
		}

		logger.debug("Next index: " + index.get());
		return targets.get(index.getAndIncrement());
	}

	@Override
	public void executeStrategy(String text) {
		WebTarget target = this.getNextTarget();
		this.executePOSTRequest(target, text);
	}
}
