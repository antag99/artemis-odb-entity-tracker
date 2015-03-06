package net.namekdev.entity_tracker.network;

import java.net.SocketAddress;
import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import net.namekdev.entity_tracker.connectors.WorldController;
import net.namekdev.entity_tracker.connectors.WorldUpdateListener;
import net.namekdev.entity_tracker.model.AspectInfo;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicator;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicatorProvider;
import net.namekdev.entity_tracker.network.base.RawConnectionOutputListener;
import net.namekdev.entity_tracker.network.base.Server;
import net.namekdev.entity_tracker.network.communicator.EntityTrackerCommunicator;
import net.namekdev.entity_tracker.utils.tuple.Tuple3;

import com.artemis.utils.Bag;

/**
 * Server listening to new clients, useful to pass into Entity Tracker itself.
 * Collects data to gather world state for incoming connections.
 *
 * @author Namek
 */
public class EntityTrackerServer extends Server implements WorldUpdateListener {
	private WorldController _worldController;
	private Bag<EntityTrackerCommunicator> _listeners = new Bag<EntityTrackerCommunicator>();

	private Bag<String> _managers = new Bag<String>();
	private Bag<Tuple3<Integer, String, AspectInfo>> _systems = new Bag<Tuple3<Integer, String, AspectInfo>>();
	private Bag<String> _componentTypes = new Bag<String>();
	private Map<Integer, BitSet> _entities = new HashMap<Integer,BitSet>();
	private Bag<Integer> _entitySystemsEntitiesCount = new Bag<Integer>();
	private Bag<Integer> _entitySystemsMaxEntitiesCount = new Bag<Integer>();


	public EntityTrackerServer() {
		super();
		super.clientListenerProvider = _communicatorProvider;
	}

	@Override
	public void injectWorldController(WorldController controller) {
		_worldController = controller;
	}

	@Override
	public int getListeningBitset() {
		// TODO
		return ENTITY_ADDED | ENTITY_DELETED | ENTITY_SYSTEM_STATS;
	}

	@Override
	public void addedSystem(int index, String name, BitSet allTypes, BitSet oneTypes, BitSet notTypes) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedSystem(index, name, allTypes, oneTypes, notTypes);
		}
		_systems.add(Tuple3.create(index, name, new AspectInfo(allTypes, oneTypes, notTypes)));
		_entitySystemsEntitiesCount.set(index, 0);
		_entitySystemsMaxEntitiesCount.set(index, 0);
	}

	@Override
	public void addedManager(String name) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedManager(name);
		}
		_managers.add(name);
	}

	@Override
	public void addedComponentType(int index, String name) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedComponentType(index, name);
		}
		_componentTypes.set(index, name);
	}

	@Override
	public void updatedEntitySystem(int systemIndex, int entitiesCount, int maxEntitiesCount) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.updatedEntitySystem(systemIndex, entitiesCount, maxEntitiesCount);
		}
		_entitySystemsEntitiesCount.set(systemIndex, entitiesCount);
		_entitySystemsMaxEntitiesCount.set(systemIndex, maxEntitiesCount);
	}

	@Override
	public void addedEntity(int entityId, BitSet components) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedEntity(entityId, components);
		}
		_entities.put(entityId, components);
	}

	@Override
	public void deletedEntity(int entityId) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.deletedEntity(entityId);
		}
		_entities.remove(entityId);
	}

	// TODO handle disconnection!

	private RawConnectionCommunicatorProvider _communicatorProvider = new RawConnectionCommunicatorProvider() {
		@Override
		public RawConnectionCommunicator getListener(String remoteName) {
			// Server requests communicator for given remote.

			EntityTrackerCommunicator newCommunicator = new EntityTrackerCommunicator() {
				@Override
				public void connected(SocketAddress remoteAddress, RawConnectionOutputListener output) {
					super.connected(remoteAddress, output);
					injectWorldController(_worldController);


					for (int i = 0, n = _systems.size(); i < n; ++i) {
						final Tuple3<Integer, String, AspectInfo> system = _systems.get(i);
						final AspectInfo aspects = system.item3;
						addedSystem(system.item1, system.item2, aspects.allTypes, aspects.oneTypes, aspects.exclusionTypes);
					}

					for (int i = 0, n = _managers.size(); i < n; ++i) {
						addedManager(_managers.get(i));
					}

					for (int i = 0, n = _componentTypes.size(); i < n; ++i) {
						addedComponentType(i, _componentTypes.get(i));
					}

					for (int i = 0, n = _systems.size(); i < n; ++i) {
						if (_entitySystemsEntitiesCount.get(i) != null) {
							int entitiesCount = _entitySystemsEntitiesCount.get(i);
							int maxEntitiesCount = _entitySystemsMaxEntitiesCount.get(i);
							updatedEntitySystem(i, entitiesCount, maxEntitiesCount);
						}
					}

					for (Entry<Integer, BitSet> entity : _entities.entrySet()) {
						addedEntity(entity.getKey(), entity.getValue());
					}
				}
			};
			_listeners.add(newCommunicator);

			return newCommunicator;
		}
	};
}
