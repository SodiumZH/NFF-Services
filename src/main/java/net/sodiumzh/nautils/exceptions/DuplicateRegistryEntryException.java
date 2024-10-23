package net.sodiumzh.nautils.exceptions;

/**
 * Thrown when trying to register something but an entry of the same key is already existing.
 */
public class DuplicateRegistryEntryException extends RuntimeException {

	public DuplicateRegistryEntryException(String string) {
		super(string);
	}

	public static DuplicateRegistryEntryException duplicateValue(String newKey, String oldKey)
	{
		return new DuplicateRegistryEntryException(String.format("Duplicate keys for the same value: \"%s\" and \"%s\".", newKey, oldKey));
	}

	public static DuplicateRegistryEntryException registeredTwice(String key)
	{
		return new DuplicateRegistryEntryException(String.format("Key \"%s\" registered twice.", key));
	}

	private static final long serialVersionUID = -8840627795547230497L;
	
}
