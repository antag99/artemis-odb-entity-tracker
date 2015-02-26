package net.namekdev.entity_tracker.network;

import java.util.BitSet;

import com.artemis.utils.Bag;

import net.namekdev.entity_tracker.connectors.UpdateListener;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicator;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicatorProvider;
import net.namekdev.entity_tracker.network.base.RawConnectionOutputListener;
import net.namekdev.entity_tracker.network.base.Server;

/**
 *
 * @author Namek
 */
public class EntityTrackerServer extends Server implements UpdateListener {
	private Bag<EntityTrackerCommunicator> _listeners = new Bag<EntityTrackerCommunicator>();
	private Bag<String> _managers = new Bag<String>();


	public EntityTrackerServer() {
		super();
		super.clientListenerProvider = _communicatorProvider;
	}


	@Override
	public int getListeningBitset() {
		// TODO
		return ADDED | DELETED;
	}

	@Override
	public void addedEntitySystem(String name, BitSet allTypes, BitSet oneTypes, BitSet notTypes) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedEntitySystem(name, allTypes, oneTypes, notTypes);
		}
		// TODO save that for new future connections
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
	public void addedComponentType(String name) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedComponentType(name);
		}
		// TODO save that for new future connections
	}

	@Override
	public void addedEntity(int entityId, BitSet components) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedEntity(entityId, components);
		}
		// TODO save that for new future connections
	}

	@Override
	public void deletedEntity(int entityId) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.deletedEntity(entityId);
		}
		// TODO save that for new future connections
	}

	// TODO handle disconnection!

	private RawConnectionCommunicatorProvider _communicatorProvider = new RawConnectionCommunicatorProvider() {
		@Override
		public RawConnectionCommunicator getListener(String remoteName) {
			// Server requests communicator for given remote.

			EntityTrackerCommunicator newCommunicator = new EntityTrackerCommunicator() {

				@Override
				public void connected(RawConnectionOutputListener output) {
					super.connected(output);

					for (int i = 0; i < _managers.size(); ++i) {
						addedManager(_managers.get(i));
					}

					// TODO notify of rest data
				}

			};
			_listeners.add(newCommunicator);

			return newCommunicator;
		}
	};
}
