package org.lazan.t5.cometddemo.model;

import java.util.Date;

public class ChatMessage {
	private String fromUser;
	private String toUser;
	private String message;
	private Date date;
	
	public ChatMessage(String fromUser, String toUser, String message, Date date) {
		super();
		this.fromUser = fromUser;
		this.toUser = toUser;
		this.message = message;
		this.date = date;
	}

	public String getFromUser() {
		return fromUser;
	}

	public String getToUser() {
		return toUser;
	}

	public String getMessage() {
		return message;
	}

	public Date getDate() {
		return date;
	}
}
