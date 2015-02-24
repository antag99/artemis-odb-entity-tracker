package net.namekdev.entity_tracker.connectors;

import java.util.BitSet;

/**
 * TODO: rename to CommunicationListener because it will send data in both directions
 *
 * @author Namek
 */
public interface UpdateListener {
	public static final int ADDED = 1 << 1;
	public static final int DELETED = 1 << 2;
//	public static final int CHANGED = 1 << 3;


	/**
	 * TODO Rename it to... getState()? CONNECTED may be added here
	 * @return
	 */
	int getListeningBitset();

	void addedEntitySystem(String name, BitSet allTypes, BitSet oneTypes, BitSet notTypes);

	void addedManager(String name);

	void addedComponentType(String name);

	void addedEntity(int entityId, BitSet components);

//	void changed(Entity e);

	void deletedEntity(int entityId);

}
