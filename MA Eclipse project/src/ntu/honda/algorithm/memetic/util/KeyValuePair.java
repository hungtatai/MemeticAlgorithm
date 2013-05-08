package ntu.honda.algorithm.memetic.util;

@SuppressWarnings("rawtypes")
public class KeyValuePair<K, V> {
	//private AbstractMap.SimpleEntry<K, V> pair;
	public final K Key;
	public final V Value;
	
	public KeyValuePair(K key, V value) {
		this.Key = key;
		this.Value = value;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((Key == null) ? 0 : Key.hashCode());
		result = prime * result + ((Value == null) ? 0 : Value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		KeyValuePair other = (KeyValuePair) obj;
		if (Key == null) {
			if (other.Key != null)
				return false;
		} else if (!Key.equals(other.Key))
			return false;
		if (Value == null) {
			if (other.Value != null)
				return false;
		} else if (!Value.equals(other.Value))
			return false;
		return true;
	}
	
	
}
