package org.levigo.jadice.server.converterclient;

import java.util.Date;

import com.levigo.jadice.server.Node;

public class LogMessage {
	public enum Type {
		STATE,
		WARNING,
		ERROR,
		FATAL,
		SUB_NODE_CREATED
	}
	
	public final Date timestamp;
	
	public final Type type;
	
	public final Node node;
	
	public final String messageId;
	
	public final String message;
	
	public final Throwable cause;

	public LogMessage(Date timestamp, Type type, Node node, String messageId,
			String message, Throwable cause) {
		this.timestamp = timestamp;
		this.type = type;
		this.node = node;
		this.messageId = messageId;
		this.message = message;
		this.cause = cause;
	}

	
}
