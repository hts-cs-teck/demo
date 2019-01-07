package com.example.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.example.dto.EventDetailDto;
import com.example.entity.EventAttendance;
import com.example.entity.EventComment;
import com.example.entity.EventDate;
import com.example.entity.Member;
import com.example.model.EventDetailModel;
import com.example.model.SessionModel;
import com.example.services.EventAttendanceService;
import com.example.services.EventCommentService;
import com.example.services.EventDateService;
import com.example.services.MemberService;

@Controller
public class EventDetailController {

	@Autowired
	protected SessionModel sessionModel;

	@Autowired
	private EventDateService eventDateService;

	@Autowired
	private MemberService memberService;

	@Autowired
	private EventAttendanceService eventAttendanceService;

	@Autowired
	private EventCommentService eventCommentService;

	@RequestMapping(value = "/eventDetail")
	public String index(Model model, EventDetailModel eventDetailModel)  {
		// メンバーをすべて取得
		List<Member> memberList = memberService.findAll();

		// イベントに対する日付を取得
		List<EventDate> eventDateList = eventDateService.findAnyCondByEventid(eventDetailModel.getEventid());

		// 各メンバーの出欠、コメントを取得
		boolean isRegist = false;
		List<EventDetailDto> eventDetailList = new ArrayList<>();
		//イベント日付取得
		String csvText = "";

		for (EventDate eventDate : eventDateList) {
				csvText = csvText + ","+eventDate.getStartDate();
		}
		csvText = csvText + ",致知読書感想文,コメント";
		csvText = csvText + "\n";
		for (Member member : memberList) {
			// メンバー名
			csvText = csvText + member.getName();

			// 出欠の取得
			List<String> attendanceList = new ArrayList<>();
			for (EventDate eventDate : eventDateList) {
				EventAttendance eventAttendance = eventAttendanceService.findByPK(member.getId(), eventDate.getId());
				if (eventAttendance != null) {
					attendanceList.add(eventAttendance.getAttendance());
					csvText = csvText + ","+eventAttendance.getAttendance();
				} else {
					csvText = csvText + ",";
					break;
				}
			}
			if (attendanceList.isEmpty()) {
				// 出欠対象のメンバーでない
				continue;
			}

			// 登録メンバに含まれているか
			if (member.getId().equals(sessionModel.getId())) {
				isRegist = true;
			}

			// DTOの設定
			EventDetailDto dto = new EventDetailDto();
			// メンバー名
			dto.setName(member.getName());
			// 出欠
			dto.setAttendanceList(attendanceList);
			// コメント
			EventComment eventComment = eventCommentService.findByPK(member.getId(), eventDetailModel.getEventid());
			if (eventComment != null) {
				dto.setComment(eventComment.getComment());
				dto.setBookAttendance(eventComment.getBookattendance());
				if(eventComment.getBookattendance() == null){
					csvText = csvText + ",";
				}else{
					csvText = csvText + ","+eventComment.getBookattendance();
				}

				if(eventComment.getComment() == null){
					csvText = csvText + ",";
				}else{
					csvText = csvText + ","+eventComment.getComment();
				}


			}

			eventDetailList.add(dto);
			csvText = csvText + "\n";
		}

		model.addAttribute("eventDateList", eventDateList);
		model.addAttribute("eventDetailList", eventDetailList);
		model.addAttribute("isRegist", isRegist);
		model.addAttribute("csvText", csvText);

		model.addAttribute("sessionModel", sessionModel);

		return "eventDetail";
	}

	@RequestMapping(value = "/transitionAttendance")
	public String transitionAttendance(Model model, EventDetailModel eventDetailModel) {
		return "forward:/attendance?eventId=" + eventDetailModel.getEventid() + "&memberId=" + sessionModel.getId();
	}

	@RequestMapping(value = "/csvOutput")
	public String csvOutput(HttpServletResponse response, String csvText)
	{


		//文字コードと出力するCSVファイル名を設定
        response.setContentType(MimeTypeUtils.APPLICATION_OCTET_STREAM_VALUE + ";charset=sjis");
        response.setHeader("Content-Disposition", "attachment; filename=\"ouput.csv\"");


		try (PrintWriter pw = response.getWriter()) {


                //CSVファイルに書き込み
                pw.print(csvText);

		} catch (IOException e) {
            e.printStackTrace();
        }

		//model.addAttribute("sessionModel", sessionModel);

		return "eventDetail";
	}

}
