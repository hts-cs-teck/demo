package com.example.entity;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import com.example.entity.pk.EventCommentPK;


@Entity
@Table(name="eventcomment")
public class EventComment {

	@EmbeddedId
	private EventCommentPK eventcommentPK;

	@Column(name="comment")
	private String comment;

	@Column(name="bookattendance")
	private String bookattendance;


	public EventCommentPK getEventcommentPK() {
		return eventcommentPK;
	}

	public void setEventcommentPK(EventCommentPK eventcommentPK) {
		this.eventcommentPK = eventcommentPK;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getBookattendance() {
		return bookattendance;
	}

	public void setBookattendance(String bookattendance) {
		this.bookattendance = bookattendance;
	}

}