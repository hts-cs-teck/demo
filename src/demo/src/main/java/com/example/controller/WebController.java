package com.example.controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.EventDto;
import com.example.dto.TeamDto;
import com.example.entity.Event;
import com.example.entity.EventAttendance;
import com.example.entity.EventDate;
import com.example.entity.Member;
import com.example.entity.Team;
import com.example.entity.pk.EventAttendancePK;
import com.example.model.EventModel;
import com.example.model.LoginChkModel;
import com.example.model.SessionModel;
import com.example.services.EventAttendanceService;
import com.example.services.EventDateService;
import com.example.services.EventService;
import com.example.services.MemberService;
import com.example.services.TeamService;
import com.example.utils.StringUtil;

@Controller
public class WebController {

	@Autowired
	protected SessionModel sessionModel;

	@Autowired
	TeamService teamService;

	@Autowired
	EventService eventService;

	@Autowired
	EventDateService eventDateService;

	@Autowired
	MemberService memberService;

	@Autowired
	EventAttendanceService eventAttendanceService;

	@RequestMapping(value = "/login")
	public String LoginController(LoginChkModel loginChkModel)  {
		return "login";
	}

	@RequestMapping(value = "/loginchk")
	public String LoginchkController(Model model, @Validated LoginChkModel loginChkModel, BindingResult result)  {
		try {
			if (result.hasErrors()) {
				return "login";
			}

			List<Member> members = memberService.findAnyCondByEmployeeid(Long.parseLong(loginChkModel.getEmployeeid()));

			if (members == null)
			{
				model.addAttribute("Message","ユーザIDもしくはパスワードが間違っています");
				return "login";
			}
			Member member = members.get(0);

			boolean checkPasswd = false;
			if (member.getPasswd().length() == 64) {
				// 暗号化あり
				checkPasswd = member.getPasswd().equals(StringUtil.sha256(loginChkModel.getPasswd()));
			} else {
				// 暗号化なし
				checkPasswd = member.getPasswd().equals(loginChkModel.getPasswd());
			}
			if (!checkPasswd)
			{
				model.addAttribute("Message","ユーザIDもしくはパスワードが間違っています");
				return "login";
			}

			sessionModel.setId(member.getId());
			sessionModel.setSimei(member.getName());

			model.addAttribute("sessionModel", sessionModel);

			return "forward:eventList";
		} catch (Exception e) {
			model.addAttribute("Message","ユーザIDもしくはパスワードが間違っています");
			return "login";
		}
	}

	@RequestMapping(value = "/sample")
	public String SampleController()  {
		return "sample";
	}

	@RequestMapping(value = "/logout")
	public String LogoutController(LoginChkModel loginChkModel, HttpSession session) {
		// dummy spring security を使うべき ...
		session.invalidate();
		return "login";
	}

	boolean SetDefaultList(Model model)
	{
		List<EventDto> eventDtoList = new ArrayList<>();
		List<Member> memberListDB = memberService.findAll();
		for (Member memberDB : memberListDB){

			Team team = teamService.find(memberDB.getTeamid());
			if(team == null)
			{
				model.addAttribute("Message","登録画面表示失敗");
				return false;
			}

			EventDto eventDto = new EventDto();
			eventDto.setTeam(team.getName());
			eventDto.setName(memberDB.getName());
//			eventDto.setSelected(hit);
			eventDtoList.add(eventDto);
		}
		model.addAttribute("memberList", eventDtoList);

		// 所属一覧
		List<Team> teamList = teamService.findAll();
		List<TeamDto> teamDtoList = new ArrayList<>();
		if (!teamList.isEmpty())
		{
			for (Team team : teamList) {

				TeamDto teamDto = new TeamDto();
				teamDto.setName(team.getName());
				teamDtoList.add(teamDto);
			}
		}
		model.addAttribute("teamList", teamDtoList);

		return true;
	}

	@RequestMapping(value = "/event")
	public String EventController(Model model, EventModel eventModel, BindingResult result)  {
		try {
			model.addAttribute("sessionModel", sessionModel);
			model.addAttribute("Message","");

			if(!SetDefaultList(model))
			{
				model.addAttribute("Message","登録画面表示失敗");
				return "ngEvent";
			}

			String id = eventModel.getId();
			if(!StringUtil.isNullOrEmpty(id))
			{
				Event eventResult = eventService.find(Long.parseLong(id));

				// イベント日付 登録結果の参照
				List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(Long.parseLong(id));
				List<String> strDateListResult = new ArrayList<>();
				String strDateResult = new String();
				List<Long> memberList = new ArrayList<>();
				if (!eventDateList.isEmpty())
				{
					for (EventDate eventDate : eventDateList) {

						String dateSpan = formatDateSpan(eventDate.getStartDate(), eventDate.getEndDate());
						strDateListResult.add(dateSpan);
						// 連結した文字列を保持
						if(strDateResult.length() == 0)
						{
							strDateResult = dateSpan;
						}
						else
						{
							strDateResult = strDateResult + "," + dateSpan;
						}


						// メンバ 登録結果の参照
						List<EventAttendance> eventAttendanceList = eventAttendanceService.findAll();
						for (EventAttendance eventAttendance : eventAttendanceList) {
							Long eventdateid = eventAttendance.getEventAttendancePK().getEventdateid();
							Long memberid = eventAttendance.getEventAttendancePK().getMemberid();
							if(eventdateid.equals(eventDate.getId()))
							{
								memberList.add(memberid);
							}
						}
					}
				}

				List<Member> memberListDB = memberService.findAll();
				List<EventDto> eventDtoListSelect = new ArrayList<>();
				for (Member memberDB : memberListDB){
					boolean hit = false;
					for (Long member : memberList){
						if(memberDB.getId().equals(member)){
							hit = true;
							break;
						}
					}

					Team team = teamService.find(memberDB.getTeamid());
					if(team == null)
					{
						model.addAttribute("Message","登録画面表示失敗");
						return "ngEvent";
					}

					if(hit)
					{
						EventDto eventDto = new EventDto();
						eventDto.setTeam(team.getName());
						eventDto.setName(memberDB.getName());
	//					eventDto.setSelected(hit);
						eventDtoListSelect.add(eventDto);
					}

				}

				eventModel.setName(eventResult.getName());
				model.addAttribute("eventDateList", strDateListResult);
				eventModel.setDatelisttext(strDateResult);
				model.addAttribute("memberlistselect", eventDtoListSelect);
			}
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
		}
		return "event";
	}

	@RequestMapping(value = {"/registEvent"})
	public String RegistEventController(Model model, @Validated EventModel eventModel, BindingResult result)  {
		try {
			model.addAttribute("sessionModel", sessionModel);

			if(!SetDefaultList(model))
			{
				model.addAttribute("Message","例外発生");
				return "ngEvent";
			}

			if (result.hasErrors()) {
				String strEventDateList = eventModel.getDatelisttext();
				if(!StringUtil.isNullOrEmpty(strEventDateList))
				{
					String[] strEventDate = strEventDateList.split(",");
					model.addAttribute("eventDateList", strEventDate);
				}

				List<EventDto> eventDtoListSelect = new ArrayList<>();
				String[] memberList = eventModel.getMemberlist();
				if(memberList != null && memberList.length>0)
				{
					for (String member : memberList) {
						List<Member> memberListDB = memberService.findByName(member);
						if (memberListDB.size()!=1)
						{
							model.addAttribute("Message","例外発生");
							return "ngEvent";
						}

						Team team = teamService.find(memberListDB.get(0).getTeamid());
						if(team == null)
						{
							model.addAttribute("Message","例外発生");
							return "ngEvent";
						}

						EventDto eventDto = new EventDto();
						eventDto.setTeam(team.getName());
						eventDto.setName(memberListDB.get(0).getName());
						eventDtoListSelect.add(eventDto);
					}
					model.addAttribute("memberlistselect", eventDtoListSelect);
				}

				model.addAttribute("Message","イベント編集に失敗しました");
				return "event";
			}

			// イベントの登録or更新
			Event event = new Event();
			String id = eventModel.getId();
			if(id != "")
			{
				event.setId(Long.parseLong(id));
			}
			event.setName(eventModel.getName());
			Event eventResult = eventService.save(event);
			if (eventResult == null)
			{
				if(id == null)
				{
					model.addAttribute("Message","イベント登録に失敗しました");
					return "ngEvent";
				}
				else
				{
					model.addAttribute("Message","イベント更新に失敗しました");
					return "ngEvent";
				}
			}

			// 候補日の登録&削除
			{
				String strEventDateList = eventModel.getDatelisttext();
				String[] strEventDate = strEventDateList.split(",");

				// 現在の候補日取得
				List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(eventResult.getId());

				// 新規候補日の登録
				for(int i=0;i<strEventDate.length;i++)
				{
					boolean hit = false;
					for (EventDate eventDate : eventDateList) {
						String dateSpan = formatDateSpan(eventDate.getStartDate(), eventDate.getEndDate());
						// 登録済み
						if(strEventDate[i].equals(dateSpan))
						{
							hit = true;
							continue;
						}
					}
					// 登録済み
					if(hit == true)
					{
						continue;
					}

					// 未登録のため登録
					EventDate newEventDate = new EventDate();
					newEventDate.setEventid(eventResult.getId());
					newEventDate.setStartDate(toDateSpan(strEventDate[i])[0]);
					newEventDate.setEndDate(toDateSpan(strEventDate[i])[1]);
					EventDate eventDateResult = eventDateService.save(newEventDate);
					if (eventDateResult == null)
					{
						if(id == null)
						{
							model.addAttribute("Message","イベント登録に失敗しました");
							return "ngEvent";
						}
						else
						{
							model.addAttribute("Message","イベント更新に失敗しました");
							return "ngEvent";
						}
					}

					// 出席情報登録
					String[] memberList = eventModel.getMemberlist();
					for (String member : memberList) {
						List<Member> memberListDB = memberService.findByName(member);
						if (memberListDB.size()!=1)
						{
							if(id == null)
							{
								model.addAttribute("Message","イベント登録に失敗しました");
								return "ngEvent";
							}
							else
							{
								model.addAttribute("Message","イベント更新に失敗しました");
								return "ngEvent";
							}
						}
						Long memberId = memberListDB.get(0).getId();

						EventAttendance eventAttendance = new EventAttendance();
						EventAttendancePK eventAttendancePK = new EventAttendancePK();
						eventAttendancePK.setMemberid(memberId);
						eventAttendancePK.setEventdateid(eventDateResult.getId());
						eventAttendance.setEventAttendancePK(eventAttendancePK);
						EventAttendance eventAttendanceResult = eventAttendanceService.save(eventAttendance);
						if (eventAttendanceResult == null)
						{
							if(id == null)
							{
								model.addAttribute("Message","イベント登録に失敗しました");
								return "ngEvent";
							}
							else
							{
								model.addAttribute("Message","イベント更新に失敗しました");
								return "ngEvent";
							}
						}
					}
				}

				// 既存候補日の削除
				for (EventDate eventDate : eventDateList) {
					String dateSpan = formatDateSpan(eventDate.getStartDate(), eventDate.getEndDate());

					boolean hit = false;
					for(int i=0;i<strEventDate.length;i++)
					{
						// 登録済み
						if(strEventDate[i].equals(dateSpan))
						{
							hit = true;
							continue;
						}
					}
					// 登録済み
					if(hit == true)
					{
						continue;
					}

					// 出席情報削除
					List<EventAttendance> eventAttendanceList = eventAttendanceService.findAll();
					for (EventAttendance eventAttendance : eventAttendanceList) {
						Long eventdateid = eventAttendance.getEventAttendancePK().getEventdateid();
						if(eventdateid.equals(eventDate.getId()))
						{
							eventAttendanceService.deleteByPK(eventAttendance.getEventAttendancePK());
						}
					}

					// イベント日付削除
					eventDateService.delete(eventDate.getId());
				}
			}

			// メンバの登録&削除
			String[] memberList = eventModel.getMemberlist();
			{
				// 新規追加
				List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(eventResult.getId());

				for (String member : memberList) {
					List<Member> memberListDB = memberService.findByName(member);
					if (memberListDB.size()!=1)
					{
						if(id == null)
						{
							model.addAttribute("Message","イベント登録に失敗しました");
							return "ngEvent";
						}
						else
						{
							model.addAttribute("Message","イベント更新に失敗しました");
							return "ngEvent";
						}
					}
					Long memberId = memberListDB.get(0).getId();

					// 新規出席情報の追加
					for (EventDate eventDate : eventDateList) {

						EventAttendance eventAttendanceResult = eventAttendanceService.findByPK(memberId, eventDate.getId());
						if (eventAttendanceResult == null)
						{
							EventAttendance eventAttendance = new EventAttendance();
							EventAttendancePK eventAttendancePK = new EventAttendancePK();
							eventAttendancePK.setMemberid(memberId);
							eventAttendancePK.setEventdateid(eventDate.getId());
							eventAttendance.setEventAttendancePK(eventAttendancePK);
							eventAttendanceResult = eventAttendanceService.save(eventAttendance);
							if (eventAttendanceResult == null)
							{
								if(id == null)
								{
									model.addAttribute("Message","イベント登録に失敗しました");
									return "ngEvent";
								}
								else
								{
									model.addAttribute("Message","イベント更新に失敗しました");
									return "ngEvent";
								}
							}
						}
					}
				}

				// 削除
				List<EventAttendance> eventAttendanceList = eventAttendanceService.findAll();
				for (EventAttendance eventAttendance : eventAttendanceList) {
					Long eventdateid = eventAttendance.getEventAttendancePK().getEventdateid();
					Long memberid = eventAttendance.getEventAttendancePK().getMemberid();

					// 処理対象イベントの情報かチェック
					boolean hit = false;
					for (EventDate eventDate : eventDateList) {
						if(eventdateid.equals(eventDate.getId()))
						{
							hit = true;
							break;
						}
					}
					if(!hit)
					{
						continue;
					}

					hit = false;
					for (String member : memberList) {
						List<Member> memberListDB = memberService.findByName(member);
						if (memberListDB.size()!=1)
						{
							if(id == null)
							{
								model.addAttribute("Message","イベント登録に失敗しました");
								return "ngEvent";
							}
							else
							{
								model.addAttribute("Message","イベント更新に失敗しました");
								return "ngEvent";
							}
						}
						if(memberid.equals(memberListDB.get(0).getId()))
						{
							hit = true;
							break;
						}
					}
					if(!hit)
					{
						eventAttendanceService.delete(memberid, eventdateid);
						continue;
					}
				}
			}

			// イベント日付 登録結果の参照
			List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(eventResult.getId());
			List<String> strDateListResult = new ArrayList<>();
			String strDateResult = new String();
			if (!eventDateList.isEmpty())
			{
				for (EventDate eventDate : eventDateList) {
					String dateSpan = formatDateSpan(eventDate.getStartDate(), eventDate.getEndDate());
					strDateListResult.add(dateSpan);

					// 連結した文字列を保持
					if(strDateResult.length() == 0)
					{
						strDateResult = dateSpan;
					}
					else
					{
						strDateResult = strDateResult + "," + dateSpan;
					}
				}
			}

			// メンバ 登録結果の参照
			List<EventDto> eventDtoListSelect = new ArrayList<>();
			List<Member> memberListDB = memberService.findAll();
			for (Member memberDB : memberListDB){
				boolean hit = false;
				for (String member : memberList){
					if(memberDB.getName().equals(member)){
						hit = true;
						break;
					}
				}

				Team team = teamService.find(memberDB.getTeamid());
				if(team == null)
				{
					model.addAttribute("Message","登録画面表示失敗");
					return "ngEvent";
				}

				if(hit)
				{
					EventDto eventDto = new EventDto();
					eventDto.setTeam(team.getName());
					eventDto.setName(memberDB.getName());
//					eventDto.setSelected(hit);
					eventDtoListSelect.add(eventDto);
				}

			}

			model.addAttribute("eventDateList", strDateListResult);
			model.addAttribute("datelisttext", strDateResult);
			model.addAttribute("memberlistselect", eventDtoListSelect);

			return "event";
		} catch (Exception e) {
			model.addAttribute("Message","例外発生");
			return "ngEvent";
		}
	}
	public static String formatDateSpan(Date from, Date to) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
		return dateFormat.format(from) + "～" + dateFormat.format(to);
	}
	public static Date toDate(String str) {
		String[] formats = {
			"yyyy/MM/dd HH:mm",
			"yyyy/MM/dd",
			"MM/dd",
			"HH:mm"
		};
		for (String format : formats) {
			try {
				return (new SimpleDateFormat(format)).parse(str);
			} catch(Exception e) {}
		}
		throw new IllegalArgumentException("日時形式エラー");
	}
	public static Date[] toDateSpan(String str) {
		Date[] dateSpan = new Date[2];
		dateSpan[0] = toDate(str.split("～")[0]);
		dateSpan[1] = toDate(str.split("～")[1]);
		return dateSpan;
	}

}
