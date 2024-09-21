package net.sodiumzh.nautils.exceptions;

/**
 * Thrown on calling unimplemented code. 
 * Used to label methods that haven't been implemented yet to prevent accident silent calling which may cause hidden bugs.
 */
public class UnimplementedException extends RuntimeException {

	public UnimplementedException(String s)
	{
		super(s);
	}
	
	public UnimplementedException()
	{
		this("Attempting to call unimplemented class/function");
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = 4279326289914680096L;

	public static UnimplementedException forClass()
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length > 2)
		{
			return new UnimplementedException("Class unimplemented: "
					+ stacktrace[2].getClassName());
		}
		else throw new RuntimeException();
	}
	
	public static UnimplementedException forMethod()
	{
		StackTraceElement[] stacktrace = Thread.currentThread().getStackTrace();
		if (stacktrace.length > 2)
		{
			return new UnimplementedException("Class unimplemented: "
					+ stacktrace[2].getClassName() + "::" + stacktrace[2].getMethodName());
		}
		else throw new RuntimeException();
	}
	
}
