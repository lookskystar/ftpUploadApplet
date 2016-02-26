package com.image.applet.ftp;

import com.image.applet.util.FTPConst;

public class FTPTransferException extends Exception {
	
	/**
	 * 错误代码
	 */
	private int errorCode = FTPConst.ERROR_OTHER;
	
	private static final long serialVersionUID = -506998980616096182L;

	public FTPTransferException() {
		super();
	}
	
	public FTPTransferException(int errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public FTPTransferException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FTPTransferException(int errorCode, String message, Throwable cause) {
		super(message, cause);
		this.errorCode = errorCode;
	}

	public FTPTransferException(String message) {
		super(message);
	}
	
	public FTPTransferException(int errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

	public FTPTransferException(Throwable cause) {
		super(cause);
	}
	
	public FTPTransferException(int errorCode, Throwable cause) {
		super(cause);
		this.errorCode = errorCode;
	}

	/**
	 * @return the errorCode
	 */
	public int getErrorCode() {
		return errorCode;
	}

	/**
	 * @param errorCode the errorCode to set
	 */
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	public String getMessage() {
		return FTPConst.getMessageByCode(errorCode) + super.getMessage();
	}
}
