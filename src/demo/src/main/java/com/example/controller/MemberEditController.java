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
import com.example.entity.Member;
import com.example.entity.Team;
import com.example.model.MemberModel;
import com.example.model.SessionModel;
import com.example.services.MemberService;
import com.example.services.TeamService;
import com.example.utils.StringUtil;

@Controller
public class MemberEditController {

	@Autowired
	protected SessionModel sessionModel;

	@Autowired
	private MemberService memberService;

	@Autowired
	private TeamService teamService;

/*
	@RequestMapping(value = "/member")
	public String MemberSearch(Model model, MemberListModel memberModel){
		try {
			model.addAttribute("Message","");

			List<Member> memberList = null;
			String steamid = memberModel.getSteamid();
			String sname = memberModel.getSname();
			String schild = memberModel.getSchild();
			if (!StringUtil.isNullOrEmpty(steamid) && !StringUtil.isNullOrEmpty(sname)) {
				if (!StringUtil.isNullOrEmpty(schild) && !StringUtil.isNullOrEmpty(schild)) {
					memberList = memberService.findAnyCondByAlls(Long.parseLong(steamid), sname);
				} else {
					memberList = memberService.findAnyCondByAll(Long.parseLong(steamid), sname);
				}
			} else if (!StringUtil.isNullOrEmpty(steamid)) {
				if (!StringUtil.isNullOrEmpty(schild) && !StringUtil.isNullOrEmpty(schild)) {
					memberList = memberService.findAnyCondByTeamids(Long.parseLong(steamid));
				} else {
					memberList = memberService.findAnyCondByTeamid(Long.parseLong(steamid));
				}
			} else if (!StringUtil.isNullOrEmpty(sname)) {
				memberList = memberService.findAnyCondByName(sname);
			} else {
				memberList = memberService.findAll();
			}

			List<MemberListDto> list = new ArrayList<>();
			if (!memberList.isEmpty())
			{
				for (Member member : memberList) {
					MemberListDto dto = new MemberListDto();
					dto.setMemberId(member.getId());
					dto.setName(member.getName());
					dto.setTeamName(teamService.find(member.getTeamid()).getName());

					list.add(dto);
				}
			}
			model.addAttribute("members", list);

			List<Team> teamList = teamService.findAll();
			List<TeamDto> teamDtoList = new ArrayList<>();
			if (!teamList.isEmpty())
			{
				for (Team team : teamList) {
					TeamDto teamDto = new TeamDto();
					teamDto.setId(team.getId());
					teamDto.setParentid(team.getParentid());
					teamDto.setName(team.getName());
					teamDtoList.add(teamDto);
				}
			}
			model.addAttribute("teamList", teamDtoList);
			model.addAttribute("conditions", memberModel);
			model.addAttribute("sessionModel", sessionModel);
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
		}
		return "memberList";
	}
*/

	void SetDefaultList(Model model)
	{
		List<Team> teamList = teamService.findAll();
		List<TeamDto> teamDtoList = new ArrayList<>();
		if (!teamList.isEmpty())
		{
			for (Team team : teamList) {
				TeamDto teamDto = new TeamDto();
				teamDto.setId(team.getId());
				teamDto.setParentid(team.getParentid());
				teamDto.setName(team.getName());
				teamDtoList.add(teamDto);
			}
		}
		model.addAttribute("teamList", teamDtoList);
	}

	@RequestMapping(value = "/memberEdit")
	public String EventController(Model model, MemberModel memberModel)  {
		try {
			// TODO とりあえず固定にしている
//			List<Member> memberList = memberService.findAll();
//			Member member = memberList.get(0);

			model.addAttribute("sessionModel", sessionModel);
			SetDefaultList(model);

			String id = memberModel.getId();
			if(!StringUtil.isNullOrEmpty(id))
			{
				Member member = memberService.find( Long.parseLong( id ) );

				memberModel.setName(member.getName());
				memberModel.setEmployeeid(Long.toString(member.getemployeeid()));
				memberModel.setPasswd(member.getPasswd());
				memberModel.setSteamid(Long.toString(member.getTeamid()));
				memberModel.setAuthoritytext(new String("2"));
			}

			model.addAttribute("conditions", memberModel);
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
		}
		return "memberEdit";
	}

	@RequestMapping(value = {"/registMember"})
	public String RegistMemberController(Model model, @Validated MemberModel memberModel, BindingResult result)  {
		try {
			model.addAttribute("sessionModel", sessionModel);
			SetDefaultList(model);

			if (result.hasErrors()) {
				model.addAttribute("conditions", memberModel);

				model.addAttribute("Message","メンバ編集に失敗しました");
				return "memberEdit";
			}

			Member member = new Member();
			String id = memberModel.getId();
			if(!StringUtil.isNullOrEmpty(id))
			{
				member.setId(Long.parseLong(id));
			}
			member.setemployeeid(Long.parseLong(memberModel.getEmployeeid()));
			member.setPasswd(StringUtil.sha256(memberModel.getPasswd()));
			member.setName(memberModel.getName());
			member.setTeamid(Long.parseLong(memberModel.getSteamid()));

			Member memberResult = memberService.save(member);
			if (memberResult == null)
			{
				if(id == null)
				{
					model.addAttribute("Message","メンバー登録に失敗しました");
					return "ng";
				}
				else
				{
					model.addAttribute("Message","メンバー更新に失敗しました");
					return "ng";
				}
			}

			return "forward:member";
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
			return "ng";
		}
	}

}
