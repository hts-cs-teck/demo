package com.example.dto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class EventListDto implements Serializable {

	/** イベントID */
	private Long eventid;
	/** 日付 */
	private List<String> eventDate;
	/** イベント名 */
	private String eventName;


	public List<String> getEventDate() {
		return eventDate;
	}
	public void setEventDate(List<Date> eventDate) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm");
		List<String> tempDateList = new ArrayList<String>();

		for (Date date : eventDate) {
			if(date != null){
				String tempDate = sdf.format(date);
				tempDateList.add(tempDate);
			}
		}
		this.eventDate = tempDateList;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public Long getEventid() {
		return eventid;
	}
	public void setEventid(Long eventid) {
		this.eventid = eventid;
	}


}
