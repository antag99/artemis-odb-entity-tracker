package net.namekdev.entity_tracker.model;

import com.artemis.Aspect;
import com.artemis.EntitySubscription;
import com.artemis.EntitySystem;
import com.artemis.utils.IntBag;

public class EntitySystemInfo {
	public String systemName;
	public EntitySystem system;
	public Aspect.Builder aspect;
	public AspectInfo aspectInfo;
	public IntBag actives;
	public EntitySubscription subscription;


	public EntitySystemInfo(String systemName, EntitySystem system, Aspect.Builder aspect, AspectInfo aspectInfo, IntBag actives, EntitySubscription subscription) {
		this.systemName = systemName;
		this.system = system;
		this.aspect = aspect;
		this.aspectInfo = aspectInfo;
		this.actives = actives;
		this.subscription = subscription;
	}
}