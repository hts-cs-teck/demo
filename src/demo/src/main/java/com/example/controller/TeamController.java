package com.example.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.TeamDto;
import com.example.dto.TeamListDto;
import com.example.entity.Member;
import com.example.entity.Team;
import com.example.model.SessionModel;
import com.example.model.TeamListModel;
import com.example.model.TeamModel;
import com.example.services.MemberService;
import com.example.services.TeamService;
import com.example.utils.StringUtil;


@Controller
public class TeamController {

	private class TeamExcption extends Exception {
		TeamExcption(String message) {
			super(message);
		}
	}

	@Autowired
	protected SessionModel sessionModel;

	@Autowired
	private TeamService teamService;

	@Autowired
	private MemberService memberService;

	private boolean existTeam(Long id) {
		List<Team> list = teamService.findById(id);
		return list.size() == 0 ? false : true;
	}

	private boolean existChildren(Long id) {
		List<Team> list = teamService.findByParentid(id);
		return list.size() == 0 ? false : true;
	}

	private List<TeamDto> getSerachTeamList() {
		List<Team> steamList = teamService.findAll();
		List<TeamDto> steamDtoList = new ArrayList<>();
		if (!steamList.isEmpty())
		{
			for (Team team : steamList) {
				TeamDto teamDto = new TeamDto();
				teamDto.setId(team.getId());
				teamDto.setParentid(team.getParentid());
				teamDto.setName(team.getName());
				steamDtoList.add(teamDto);
			}
		}
		return steamDtoList;
	}

	private boolean existMember(Long id) {
		List<Member> list = memberService.findAnyCondByTeamid(id);
		return list.size() == 0 ? false : true;
	}

	private List<TeamListDto> getTeamList(String spid, String sname) {
		List<Team> teamList = new ArrayList<>();
		List<TeamListDto> teamDtoList = new ArrayList<>();
		if (!StringUtil.isNullOrEmpty(spid) || (StringUtil.isNullOrEmpty(spid) && StringUtil.isNullOrEmpty(sname))) {
			if (StringUtil.isNullOrEmpty(spid)) {
				teamList = teamService.findByParentidIsNull();
			} else {
				teamList = teamService.findById(Long.parseLong(spid));
			}
			for(Team team : teamList) {
				TeamDto teamDto = new TeamDto();
				teamDto.setId(team.getId());
				teamDto.setParentid(team.getParentid());
				teamDto.setName(team.getName());
				TeamListDto teamListDto = new TeamListDto();
				teamListDto.setTeam(teamDto);
				teamListDto.setExistChildren(existChildren(team.getId()));
				teamListDto.setExistMember(existMember(team.getId()));
				teamDtoList.add(teamListDto);

				teamListDto.setCteamList(rgetChildTeam(team.getId()));
			}
		} else {
			teamList = teamService.findByNameLike(sname);
			for(Team team : teamList) {
				TeamDto teamDto = new TeamDto();
				teamDto.setId(team.getId());
				teamDto.setParentid(team.getParentid());
				teamDto.setName(team.getName());
				TeamListDto p = new TeamListDto();
				p.setTeam(teamDto);
				p.setExistChildren(existChildren(team.getId()));
				p.setExistMember(existMember(team.getId()));
				p.setCteamList(new ArrayList<TeamListDto>());
				teamDtoList.add(p);
			}
		}
		return teamDtoList;
	}

	private List<TeamListDto> rgetChildTeam(Long parentId) {
		List<Team> teamList = new ArrayList<>();
		teamList = teamService.findByParentid(parentId);

		List<TeamListDto> teamDtoList = new ArrayList<>();
		for(Team team : teamList) {
			TeamDto teamDto = new TeamDto();
			teamDto.setId(team.getId());
			teamDto.setParentid(team.getParentid());
			teamDto.setName(team.getName());
			TeamListDto teamListDto = new TeamListDto();
			teamListDto.setTeam(teamDto);
			teamListDto.setExistChildren(existChildren(team.getId()));
			teamListDto.setExistMember(existMember(team.getId()));
			teamDtoList.add(teamListDto);

			teamListDto.setCteamList(rgetChildTeam(team.getId()));
		}
		return teamDtoList;
	}

	private boolean checkDuplication(String parentid, String teamname, String id) {
		List<Team> teamList = new ArrayList<Team>();
		if (StringUtil.isNullOrEmpty(parentid)) {
			teamList = teamService.findByParentidIsNull();
		} else {
			teamList = teamService.findByParentid(Long.valueOf(parentid));
		}
		if (teamList.isEmpty()) {
			return false;
		}

		for(Team team : teamList) {
			if (teamname.equals(team.getName())) {
				if (StringUtil.isNullOrEmpty(id)) {
					return true;
				}
				if (!id.equals(team.getId().toString())) {
					return true;
				}
			}
		}
		return false;
	}

	@RequestMapping(value = "/team")
	public String teamSearch(Model model, TeamListModel teamListModel){
		try {
			model.addAttribute("Message","");
			model.addAttribute("teamList", getTeamList(teamListModel.getSpid(), teamListModel.getSname()));
			model.addAttribute("steamList", getSerachTeamList());
			model.addAttribute("conditions", teamListModel);
			model.addAttribute("sessionModel", sessionModel);
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
		}
		return "teamList";
	}

	@RequestMapping(value = "/deleteTeam")
	public String deleteTeamController(Model model, TeamModel teamModel)  {
		ERROR : try {
			// 削除チェック処理
			// 排他を考慮して、他端末で変更されていないかのチェック

			// チーム状態
			List<Team> teamList = new ArrayList<Team>();
			if (StringUtil.isNullOrEmpty(teamModel.getParentid())) {
				teamList = teamService.findByIdAndParentidIsNull(Long.valueOf(teamModel.getId()));
			} else {
				teamList = teamService.findByIdAndParentid(Long.valueOf(teamModel.getId()), Long.valueOf(teamModel.getParentid()));
			}
			if (teamList.isEmpty()) {
				// 排他エラー
				model.addAttribute("Message","他端末にて編集対象のチーム情報が変更されています。");
				break ERROR;
			}

			// チーム状態
			if (existMember(Long.valueOf(teamModel.getId()))) {
				// 排他エラー
				model.addAttribute("Message","他端末にて編集対象のチーム情報が変更されています。");
				break ERROR;
			}

			// 削除処理
			teamService.delete(Long.valueOf(teamModel.getId()));

		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
		} finally {
			// 一覧画面の表示情報の設定
			model.addAttribute("sessionModel", sessionModel);
			model.addAttribute("teamList", getTeamList(teamModel.getRspid(), teamModel.getRsname()));
			model.addAttribute("steamList", getSerachTeamList());
			model.addAttribute("conditions", new TeamListModel(teamModel.getRspid(), teamModel.getRsname()));
		}
		return "teamList";
	}
	@RequestMapping(value = "/teamEdit")
	public String teamEditController(Model model, TeamModel teamModel)  {
		try {

			model.addAttribute("sessionModel", sessionModel);

			// 排他チェック(同一ID,PARENTIDで存在していれば一旦OKとする)
			List<Team> teamList = new ArrayList<Team>();
			if (!StringUtil.isNullOrEmpty(teamModel.getId())) {
				if (StringUtil.isNullOrEmpty(teamModel.getParentid())) {
					teamList = teamService.findByIdAndParentidIsNull(Long.valueOf(teamModel.getId()));
				} else {
					teamList = teamService.findByIdAndParentid(Long.valueOf(teamModel.getId()), Long.valueOf(teamModel.getParentid()));
				}
				if (teamList.isEmpty()) {
					// 排他エラー
					model.addAttribute("Message","他端末にて編集対象のチーム情報が変更されています。");
					model.addAttribute("teamList", getTeamList(teamModel.getRspid(), teamModel.getRsname()));
					model.addAttribute("steamList", getSerachTeamList());
					model.addAttribute("conditions", new TeamListModel(teamModel.getRspid(), teamModel.getRsname()));

					return "teamList";
				}
			}

			model.addAttribute("teamList", getSerachTeamList());
			model.addAttribute("team", teamModel);
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
			model.addAttribute("teamList", getTeamList(teamModel.getRspid(), teamModel.getRsname()));
			model.addAttribute("steamList", getSerachTeamList());
			model.addAttribute("conditions", new TeamListModel(teamModel.getRspid(), teamModel.getRsname()));

			return "teamList";
		}
		return "teamEdit";
	}

	@RequestMapping(value = {"/registTeam"})
	public String registTeamController(Model model, @Validated TeamModel teamModel, BindingResult result)  {
		try {
			model.addAttribute("sessionModel", sessionModel);

			if ("return".equals(teamModel.getOperation())) {
				// 戻るボタン時
				model.addAttribute("teamList", getTeamList(teamModel.getRspid(), teamModel.getRsname()));
				model.addAttribute("steamList", getSerachTeamList());
				model.addAttribute("conditions", new TeamListModel(teamModel.getRspid(), teamModel.getRsname()));

				return "teamList";
			}

			// バリデーションチェック
			if (result.hasErrors()) {
				throw new TeamExcption("入力内容に誤りがあります。");
			}

			// 排他チェック(親チームが存在するか)
			if (!StringUtil.isNullOrEmpty(teamModel.getEparentid())) {
				if (!existTeam(Long.valueOf(teamModel.getEparentid()))) {
					throw new TeamExcption("他端末にて編集対象のチーム情報が変更されています。");
				}
			}
			// 重複チェック(同一階層内に自分以外の同じチームが存在しないか)
			if (checkDuplication(teamModel.getEparentid(), teamModel.getEname(), teamModel.getId())) {
				throw new TeamExcption("同一のチーム名が存在します。");
			}

			// 登録・更新
			Team entryTeam = new Team();
			if (!StringUtil.isNullOrEmpty(teamModel.getId())) {
				entryTeam.setId(Long.parseLong(teamModel.getId()));
			}
			if (!StringUtil.isNullOrEmpty(teamModel.getEparentid())) {
				entryTeam.setParentid(Long.parseLong(teamModel.getEparentid()));
			}
			entryTeam.setName(teamModel.getEname());
			if (teamService.save(entryTeam) == null) {
				if (StringUtil.isNullOrEmpty(teamModel.getId())) {
					// 新規
					throw new TeamExcption("チームの登録に失敗しました。");
				} else {
					// 更新
					throw new TeamExcption("チームの更新に失敗しました。");
				}
			};

			model.addAttribute("teamList", getTeamList(teamModel.getRspid(), teamModel.getRsname()));
			model.addAttribute("steamList", getSerachTeamList());
			model.addAttribute("conditions", new TeamListModel(teamModel.getRspid(), teamModel.getRsname()));
			return "teamList";
		} catch (TeamExcption e) {
			model.addAttribute("Message",e.getMessage());
			model.addAttribute("teamList", getSerachTeamList());
			teamModel.setEparentid(teamModel.getEparentid());
			model.addAttribute("teamModel", teamModel);
			return "teamEdit";
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
			model.addAttribute("teamList", getSerachTeamList());
			teamModel.setParentid(teamModel.getEparentid());
			model.addAttribute("teamModel", teamModel);
			return "teamEdit";
		}
	}
}
