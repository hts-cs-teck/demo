package com.example.model;

import org.hibernate.validator.constraints.NotBlank;

/**
 * チーム編集画面のFormModel
 */
public class TeamModel {

	/** id */
	private String id;

	/** parentid */
	private String parentid;

	/** rspid(一覧画面条件復元用spid) */
	private String rspid;

	/** rsname(一覧画面条件復元用sname) */
	private String rsname;

	/** eparentid(編集用) */
	private String eparentid;

	/** ename(編集用) */
	@NotBlank(message="チームを入力して下さい。")
	private String ename;

	/** 画面操作 */
	private String operation;

	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}

	public String getEparentid() {
		return eparentid;
	}

	public void setEparentid(String eparentid) {
		this.eparentid = eparentid;
	}

	public String getEname() {
		return ename;
	}

	public void setEname(String ename) {
		this.ename = ename;
	}

	public String getRspid() {
		return rspid;
	}

	public void setRspid(String rspid) {
		this.rspid = rspid;
	}

	public String getRsname() {
		return rsname;
	}

	public void setRsname(String rsname) {
		this.rsname = rsname;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getParentid() {
		return parentid;
	}

	public void setParentid(String parentid) {
		this.parentid = parentid;
	}

}
