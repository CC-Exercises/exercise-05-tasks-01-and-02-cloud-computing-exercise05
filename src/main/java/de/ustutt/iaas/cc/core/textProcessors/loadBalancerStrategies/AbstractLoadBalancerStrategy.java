package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Provides and defines the general functions of a load balancer strategy.
 *
 */
public abstract class AbstractLoadBalancerStrategy {
	/**
	 * Executes the strategy and returns the result text.
	 * 
	 * @param text
	 * @return The result text
	 */
	public abstract String executeAndGetResult(String text);

	/**
	 * Returns the next to be invoked web target.
	 * 
	 * @return Next web target
	 */
	protected abstract WebTarget getNextTarget();

	/**
	 * Executes the POST request which sends the text to the next web target.
	 * 
	 * @param target
	 * @param text
	 * @return The processed text
	 */
	protected String executePOSTRequest(WebTarget target, String text) {
		return target.request(MediaType.TEXT_PLAIN).post(Entity.entity(text, MediaType.TEXT_PLAIN), String.class);
	}
}
