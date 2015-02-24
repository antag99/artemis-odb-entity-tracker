package net.namekdev.entity_tracker.network;

import java.util.BitSet;

import com.artemis.utils.Bag;

import net.namekdev.entity_tracker.connectors.UpdateListener;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicator;
import net.namekdev.entity_tracker.network.base.RawConnectionCommunicatorProvider;
import net.namekdev.entity_tracker.network.base.Server;

/**
 * TODO Consider deriving from Server.
 *
 * @author Namek
 */
public class EntityTrackerServer implements UpdateListener, RawConnectionCommunicatorProvider {
	private Server _server;
	private Bag<EntityTrackerCommunicator> _listeners = new Bag<EntityTrackerCommunicator>();


	public EntityTrackerServer() {
		_server = new Server(this).start();
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
	}

	@Override
	public void addedManager(String name) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedManager(name);
		}
	}

	@Override
	public void addedComponentType(String name) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedComponentType(name);
		}
	}

	@Override
	public void addedEntity(int entityId, BitSet components) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.addedEntity(entityId, components);
		}
	}

	@Override
	public void deletedEntity(int entityId) {
		for (int i = 0, n = _listeners.size(); i < n; ++i) {
			EntityTrackerCommunicator communicator = _listeners.get(i);
			communicator.deletedEntity(entityId);
		}
	}

	// TODO handle disconnection!

	@Override
	public RawConnectionCommunicator getListener(String remoteName) {
		// Server requests communicator for given remote.

		EntityTrackerCommunicator newCommunicator = new EntityTrackerCommunicator();
		_listeners.add(newCommunicator);

		return newCommunicator;
	}
}
