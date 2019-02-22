package com.example.model;

import java.util.HashMap;
import java.util.Map;

/**
 * 出欠登録画面のFormModel
 */
public class AttendanceModel {

	/** イベントID */
	private Long eventid;
	/** メンバーID */
	private Long memberid;
	/** 出欠 */
	private Map<Long, String> attendances = new HashMap<Long, String>();
	/** コメント */
	private String comment;
	/** 致知出欠 */
	private String bookAttendance;

	// ラジオボタンのリスト
	private Map<String, String> selectAttendance = new HashMap<String, String>() {{
		put("○", "出席");
		put("×", "欠席");
	}};
	// ラジオボタンのリスト
	private Map<String, String> selectBookAttendance = new HashMap<String, String>() {{
		put("○", "提出");
		put("×", "未提出");
	}};

	public Long getEventid() {
		return eventid;
	}
	public void setEventid(Long eventid) {
		this.eventid = eventid;
	}
	public Long getMemberid() {
		return memberid;
	}
	public void setMemberid(Long memberid) {
		this.memberid = memberid;
	}
	public Map<Long, String> getAttendances() {
		return attendances;
	}
	public void setAttendances(Map<Long, String> attendances) {
		this.attendances = attendances;
	}
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	public Map<String, String> getSelectAttendance() {
		return selectAttendance;
	}
	public void setSelectAttendance(Map<String, String> selectAttendance) {
		this.selectAttendance = selectAttendance;
	}
	public String getBookAttendance() {
		return bookAttendance;
	}
	public void setBookAttendance(String bookAttendance) {
		this.bookAttendance = bookAttendance;
	}
	public Map<String, String> getSelectBookAttendance() {
		return selectBookAttendance;
	}
	public void setSelectBookAttendance(Map<String, String> selectBookAttendance) {
		this.selectBookAttendance = selectBookAttendance;
	}

}
