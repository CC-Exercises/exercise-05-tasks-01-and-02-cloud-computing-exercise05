package de.ustutt.iaas.cc.core.textProcessors.loadBalancerStrategies;

import javax.ws.rs.client.WebTarget;

public interface ILoadBalancerStrategy {

	public WebTarget getNextTarget();
}
