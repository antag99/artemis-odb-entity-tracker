package net.namekdev.entity_tracker.network.communicator;

import java.net.SocketAddress;
import java.util.BitSet;

import net.namekdev.entity_tracker.connectors.WorldController;
import net.namekdev.entity_tracker.connectors.WorldUpdateListener;
import net.namekdev.entity_tracker.model.ComponentTypeInfo;
import net.namekdev.entity_tracker.model.FieldInfo;
import net.namekdev.entity_tracker.network.base.RawConnectionOutputListener;

/**
 * Communicator used by UI (client).
 *
 * @author Namek
 */
public class ExternalInterfaceCommunicator extends Communicator implements WorldController {
	private WorldUpdateListener _listener;


	public ExternalInterfaceCommunicator(WorldUpdateListener listener) {
		_listener = listener;
	}

	@Override
	public void connected(SocketAddress remoteAddress, RawConnectionOutputListener output) {
		super.connected(remoteAddress, output);
		_listener.injectWorldController(this);
	}

	@Override
	public void disconnected() {
		// TODO Auto-generated method stub

	}

	@Override
	public void bytesReceived(byte[] bytes, int offset, int length) {
		_deserializer.setSource(bytes, offset, length);

		byte packetType = _deserializer.readRawByte();

		switch (packetType) {
			case TYPE_ADDED_ENTITY_SYSTEM: {
				int index = _deserializer.readInt();
				String name = _deserializer.readString();
				BitSet allTypes = _deserializer.readBitSet();
				BitSet oneTypes = _deserializer.readBitSet();
				BitSet notTypes = _deserializer.readBitSet();
				_listener.addedSystem(index, name, allTypes, oneTypes, notTypes);
				break;
			}
			case TYPE_ADDED_MANAGER: {
				String name = _deserializer.readString();
				_listener.addedManager(name);
				break;
			}
			case TYPE_ADDED_COMPONENT_TYPE: {
				int index = _deserializer.readInt();
				String name = _deserializer.readString();
				int size = _deserializer.beginArray();

				ComponentTypeInfo info = new ComponentTypeInfo(name);
				info.fields.ensureCapacity(size);

				for (int i = 0; i < size; ++i) {
					FieldInfo field = new FieldInfo();
					field.isAccessible = _deserializer.readBoolean();
					field.fieldName = _deserializer.readString();
					field.classType = _deserializer.readString();
					field.isArray = _deserializer.readBoolean();
					field.valueType = _deserializer.readInt();

					info.fields.insertElementAt(field, i);
				}

				_listener.addedComponentType(index, info);
				break;
			}
			case TYPE_UPDATED_ENTITY_SYSTEM: {
				int index = _deserializer.readInt();
				int entitiesCount = _deserializer.readInt();
				int maxEntitiesCount = _deserializer.readInt();
				_listener.updatedEntitySystem(index, entitiesCount, maxEntitiesCount);
				break;
			}
			case TYPE_ADDED_ENTITY: {
				int entityId = _deserializer.readInt();
				BitSet components = _deserializer.readBitSet();
				_listener.addedEntity(entityId, components);
				break;
			}
			case TYPE_DELETED_ENTITY: {
				int entityId = _deserializer.readInt();
				_listener.deletedEntity(entityId);
				break;
			}
			case TYPE_UPDATE_COMPONENT_STATE: {
				int entityId = _deserializer.readInt();
				int index = _deserializer.readInt();
				int size = _deserializer.beginArray();

				Object[] values = new Object[size];

				for (int i = 0; i < size; ++i) {
					// TODO read field values to update entity component
				}

				_listener.updateComponentState(entityId, index, values);

				break;
			}

			default: throw new RuntimeException("Unknown packet type: " + (int)packetType);
		}
	}

	@Override
	public void setSystemState(String name, boolean isOn) {
		send(
			beginPacket(TYPE_SET_SYSTEM_STATE)
			.addString(name)
			.addBoolean(isOn)
		);
	}
}
