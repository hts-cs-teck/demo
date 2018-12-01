package com.example.model;

import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotBlank;

/**
 * イベント画面のFormModel
 */
public class EventModel {

	/** ID */
	private String id;

	/** イベント名 */
	@NotBlank(message="イベント名を入力してください")
	private String name;

	/** 日付 */
	@NotBlank(message="候補日を選択してください")
	private String datelisttext;

	/** メンバ */
	@NotNull(message="メンバを選択してください")
	private String[] memberlist;

	/** 所属 */
	private String[] teamlist;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDatelisttext() {
		return datelisttext;
	}

	public void setDatelisttext(String datelisttext) {
		this.datelisttext = datelisttext;
	}

	public String[] getMemberlist() {
		return memberlist;
	}

	public void setMemberlist(String[] memberlist) {
		this.memberlist = memberlist;
	}

	public String[] getTeamlist() {
		return teamlist;
	}

	public void setTeamlist(String[] teamlist) {
		this.teamlist = teamlist;
	}	
}
