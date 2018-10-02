package com.example.model;

/**
 * チーム一覧画面のFormModel
 */
public class TeamListModel {

	/** チームID */
	private String id;

	/** 親チームID */
	private String spid;

	/** チーム名 */
	private String sname;

	public TeamListModel() {
	}

	public TeamListModel(String spid, String sname) {
		setSpid(spid);
		setSname(sname);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSpid() {
		return spid;
	}

	public void setSpid(String spid) {
		this.spid = spid;
	}

	public String getSname() {
		return sname;
	}

	public void setSname(String sname) {
		this.sname = sname;
	}
}
