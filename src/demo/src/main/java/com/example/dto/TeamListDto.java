package com.example.dto;

import java.io.Serializable;
import java.util.List;

public class TeamListDto implements Serializable {

	/** チーム */
	private TeamDto team;
	/** 子チームリスト */
	private List<TeamListDto> cteamList;
	/** 子有無 */
	private boolean existChildren = false;
	/** メンバ有無 */
	private boolean existMember = false;
	public boolean isExistMember() {
		return existMember;
	}
	public void setExistMember(boolean existMember) {
		this.existMember = existMember;
	}
	public boolean isExistChildren() {
		return existChildren;
	}
	public void setExistChildren(boolean existChildren) {
		this.existChildren = existChildren;
	}
	public TeamDto getTeam() {
		return team;
	}
	public void setTeam(TeamDto team) {
		this.team = team;
	}
	public List<TeamListDto> getCteamList() {
		return cteamList;
	}
	public void setCteamList(List<TeamListDto> cteamList) {
		this.cteamList = cteamList;
	}
}
