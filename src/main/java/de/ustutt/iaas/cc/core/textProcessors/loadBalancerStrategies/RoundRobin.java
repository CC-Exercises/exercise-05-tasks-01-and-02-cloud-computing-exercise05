package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

public class RoundRobin implements ILoadBalancerStrategy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(RoundRobin.class);

	private AtomicInteger index = new AtomicInteger();
	private List<WebTarget> targets;

	public RoundRobin(List<WebTarget> targets) {
		this.targets = targets;
		logger.debug("Number of Web Targets alias TextProcessors: {}", targets.size());
	}

	@Override
	public WebTarget getNextTarget() {
		if (index.get() == targets.size()) {
			index.set(0);
		}

		logger.debug("Next index: " + index.get());
		return targets.get(index.getAndIncrement());
	}
}
