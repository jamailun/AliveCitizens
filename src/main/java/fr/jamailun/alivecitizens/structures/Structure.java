package fr.jamailun.alivecitizens.structures;

import org.bukkit.configuration.serialization.ConfigurationSerializable;

import java.util.Objects;
import java.util.UUID;

public abstract class Structure implements ConfigurationSerializable {
	
	protected final String uuid;
	
	public Structure() {
		uuid = UUID.randomUUID().toString();
	}
	
	public Structure(String uuid) {
		this.uuid = uuid;
	}
	
	public String getUuid() {
		return uuid;
	}
	
	@Override
	public boolean equals(Object o) {
		if (this == o) {
			return true;
		}
		if(!(o instanceof Structure struct)) {
			return false;
		}
		return Objects.equals(uuid, struct.uuid);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(uuid);
	}
}
