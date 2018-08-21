package com.example.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.dto.AttendanceDto;
import com.example.entity.Event;
import com.example.entity.EventAttendance;
import com.example.entity.EventComment;
import com.example.entity.EventDate;
import com.example.entity.Member;
import com.example.entity.pk.EventAttendancePK;
import com.example.entity.pk.EventCommentPK;
import com.example.model.AttendanceModel;
import com.example.model.SessionModel;
import com.example.services.EventAttendanceService;
import com.example.services.EventCommentService;
import com.example.services.EventDateService;
import com.example.services.EventService;
import com.example.services.MemberService;

@Controller
public class AttendanceController {

	@Autowired
	protected SessionModel sessionModel;

	@Autowired
	private EventService eventService;

	@Autowired
	private EventDateService eventDateService;

	@Autowired
	private EventCommentService eventCommentService;

	@Autowired
	private EventAttendanceService eventAttendanceService;

	@Autowired
	private MemberService memberService;

	@RequestMapping(value = "/attendance")
	public String index(Model model, @RequestParam Long eventId, @RequestParam Long memberId,  AttendanceModel attendanceModel)  {
		attendanceModel.setMemberid(memberId);
		attendanceModel.setEventid(eventId);

		// メンバーの名前を取得
		Member member = memberService.find(attendanceModel.getMemberid());

		// イベント名を取得
		Event event = eventService.find(attendanceModel.getEventid());
		// イベントに対する日付を取得
		List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(attendanceModel.getEventid());
		// イベントに対するコメントを取得
		EventComment eventComment = eventCommentService.findByPK(attendanceModel.getMemberid(), attendanceModel.getEventid());

		// 必要な情報をDTOに詰める
		AttendanceDto dto = new AttendanceDto();
		dto.setEventName(event.getName());
		dto.setEventStartDate(eventDateList.get(0).getStartDate());
		dto.setEventEndDate(eventDateList.get(0).getEndDate());
		if(eventComment != null)
		{
			dto.setEventComment(eventComment.getComment());
		}
		dto.setMemberName(member.getName());
		dto.setEventid(attendanceModel.getEventid());
		dto.setMemberid(attendanceModel.getMemberid());

		List<EventAttendance> eventAttendances = eventAttendanceService.findByMemberIdAndEventId(attendanceModel.getMemberid(), attendanceModel.getEventid());
		for (EventAttendance attendance : eventAttendances) {
			attendanceModel.getAttendances().put(attendance.getEventAttendancePK().getEventdateid(), attendance.getAttendance());
		}
		if(eventComment != null)
		{
			attendanceModel.setComment(eventComment.getComment());
			attendanceModel.setBookAttendance(eventComment.getBookattendance());
		}
		model.addAttribute("attendance", dto);
		model.addAttribute("eventDateList", eventDateList);

		model.addAttribute("sessionModel", sessionModel);

		return "attendance";
	}

	@RequestMapping(value = "/regist")
	public String regist(Model model, AttendanceModel attendanceModel) {

		// 出欠情報の登録
		for (Long eventDateId : attendanceModel.getAttendances().keySet()) {
			// 出欠PKの設定
			EventAttendancePK eventAttendancePK = new EventAttendancePK();
			eventAttendancePK.setMemberid(attendanceModel.getMemberid());
			eventAttendancePK.setEventdateid(eventDateId);
			// 出欠の設定
			EventAttendance eventAttendance = new EventAttendance();
			eventAttendance.setEventAttendancePK(eventAttendancePK);
			eventAttendance.setAttendance(attendanceModel.getAttendances().get(eventDateId));

			// 出欠の登録
			eventAttendanceService.save(eventAttendance);
		}

		// コメントPKの設定
		EventCommentPK eventCommentPK = new EventCommentPK();
		eventCommentPK.setMemberid(attendanceModel.getMemberid());
		eventCommentPK.setEventid(attendanceModel.getEventid());
		// コメントの設定
		EventComment eventComment = new EventComment();
		eventComment.setEventcommentPK(eventCommentPK);
		eventComment.setComment(attendanceModel.getComment());
		eventComment.setBookattendance(attendanceModel.getBookAttendance());

		// コメントの登録
		eventCommentService.save(eventComment);

		model.addAttribute("sessionModel", sessionModel);

		// 画面遷移
		return "forward:/attendance?eventId=" + attendanceModel.getEventid() + "&memberId=" + attendanceModel.getMemberid();
	}
}
