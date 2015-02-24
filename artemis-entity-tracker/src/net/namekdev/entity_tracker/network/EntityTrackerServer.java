package net.namekdev.entity_tracker.network;

import java.util.BitSet;

import net.namekdev.entity_tracker.connectors.UpdateListener;

public class EntityTrackerServer implements UpdateListener {
	EntityTrackerCommunicator communication;



	@Override
	public int getListeningBitset() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void addedEntitySystem(String name, BitSet allTypes, BitSet oneTypes, BitSet notTypes) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedManager(String managerName) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedComponentType(String name) {
		// TODO Auto-generated method stub

	}

	@Override
	public void addedEntity(int entityId, BitSet components) {
		// TODO Auto-generated method stub

	}

	@Override
	public void deletedEntity(int entityId) {
		// TODO Auto-generated method stub

	}
}
