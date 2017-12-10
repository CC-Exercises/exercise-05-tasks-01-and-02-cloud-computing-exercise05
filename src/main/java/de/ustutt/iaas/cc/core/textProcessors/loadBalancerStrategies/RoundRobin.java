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

	private static AtomicInteger atomicIndex = new AtomicInteger();
	private static List<WebTarget> targets;

	public RoundRobin(List<WebTarget> targets) {
		RoundRobin.targets = targets;
		logger.debug("Number of Web Targets alias TextProcessors: {}", targets.size());
	}

	@Override
	protected WebTarget getNextTarget() {
		int index = atomicIndex.getAndIncrement() % targets.size();
		logger.debug("Next index: {} ", index);
		return targets.get(index);
	}

	@Override
	public void executeStrategy(String text) {
		WebTarget target = this.getNextTarget();
		this.executePOSTRequest(target, text);
	}
}
