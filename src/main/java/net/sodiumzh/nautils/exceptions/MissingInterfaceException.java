package net.sodiumzh.nautils.exceptions;

/**
 * Thrown when a method call requires the target object to implement a given interface but it doesn't.
 */
public class MissingInterfaceException extends Exception
{
	public MissingInterfaceException(String string) {
		super(string);
	}

	private static final long serialVersionUID = -1898183468926280899L;

	
	
}
