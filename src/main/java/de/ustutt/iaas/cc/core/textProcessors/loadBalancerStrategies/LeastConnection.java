package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import javax.ws.rs.client.WebTarget;

import org.slf4j.Logger;

/**
 * Load balancer strategy which chooses the server with the least number of
 * connections and sends the request to it.
 *
 */
public class LeastConnection extends AbstractLoadBalancerStrategy {
	private static final Logger logger = org.slf4j.LoggerFactory.getLogger(LeastConnection.class);
	private static final Random random = new Random();
	private static ConcurrentMap<WebTarget, Integer> connectionsMap = new ConcurrentHashMap<>();

	public LeastConnection(List<WebTarget> targets) {
		targets.forEach(target -> {
			connectionsMap.put(target, 0);
		});
	}

	@Override
	protected WebTarget getNextTarget() {
		Entry<WebTarget, Integer> leastConnection = connectionsMap.entrySet().iterator().next();
		for (Entry<WebTarget, Integer> connection : connectionsMap.entrySet()) {
			if (connection.getValue() < leastConnection.getValue()) {
				leastConnection = connection;
			}
		}

		logger.info("Least Connection -> URL: {}, Num of connections: {}", leastConnection.getKey().getUri().toString(),
				leastConnection.getValue());
		return leastConnection.getKey();
	}

	public void incrementNumConnections(WebTarget target) {
		// For test purposes add a random number to number of connections
		connectionsMap.put(target, connectionsMap.get(target) + 1 + random.nextInt(2));
	}

	public void decrementNumConnections(WebTarget target) {
		connectionsMap.put(target, connectionsMap.get(target) - 1);
	}

	@Override
	public String executeAndGetResult(String text) {
		synchronized (this) {
			String response = null;
			WebTarget target = this.getNextTarget();
			this.incrementNumConnections(target);
			response = this.executePOSTRequest(target, text);
			this.decrementNumConnections(target);
			return response;
		}
	}
}
