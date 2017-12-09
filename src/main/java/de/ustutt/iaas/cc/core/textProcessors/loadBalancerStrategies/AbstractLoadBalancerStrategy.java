package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Provides and defines the general functions of a load balancer strategy.
 *
 */
public abstract class AbstractLoadBalancerStrategy {
	protected String response;

	public abstract void executeStrategy(String text);

	protected abstract WebTarget getNextTarget();

	protected void executePOSTRequest(WebTarget target, String text) {
		this.response = target.request(MediaType.TEXT_PLAIN).post(Entity.entity(text, MediaType.TEXT_PLAIN),
				String.class);
	}

	public String getResponse() {
		return this.response;
	}
}
