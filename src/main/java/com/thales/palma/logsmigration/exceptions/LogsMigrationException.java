package com.thales.palma.logsmigration.exceptions;

public class LogsMigrationException extends Exception {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public LogsMigrationException(){
		super();
	}
	
	public LogsMigrationException(String msg){
		super(msg);
	}
	
	/**
     * @param cause
     */
    public LogsMigrationException(Throwable cause) {
        super(cause);
    }

    /**
     * @param message
     * @param cause
     */
    public LogsMigrationException(String message, Throwable cause) {
        super(message, cause);
    }
	
	

}
