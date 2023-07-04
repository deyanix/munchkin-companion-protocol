package com.recadel.sjp.exception;

public class SjpException extends Exception {
	public SjpException() {
	}

	public SjpException(String message) {
		super(message);
	}

	public SjpException(String message, Throwable cause) {
		super(message, cause);
	}

	public SjpException(Throwable cause) {
		super(cause);
	}
}
