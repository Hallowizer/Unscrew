package com.hallowizer.unscrew.api;

import lombok.NoArgsConstructor;

@NoArgsConstructor
public class TransformerException extends RuntimeException {
	private static final long serialVersionUID = -5187176269860701186L;
	
	public TransformerException(String message) {
		super(message);
	}
	
	public TransformerException(Throwable cause) {
		super(cause);
	}
	
	public TransformerException(String message, Throwable cause) {
		super(message, cause);
	}
}
