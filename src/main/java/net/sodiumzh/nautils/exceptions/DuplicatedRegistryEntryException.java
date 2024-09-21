package net.sodiumzh.nautils.exceptions;

/**
 * Thrown when trying to register something but an entry of the same key is already existing.
 */
public class DuplicatedRegistryEntryException extends RuntimeException {

	public DuplicatedRegistryEntryException(String string) {
		super(string);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = -8840627795547230497L;
	
}
