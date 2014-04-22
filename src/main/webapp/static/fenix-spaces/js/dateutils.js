function nthdayOfTheWeek(when) {
	var checkpoint = when.clone();
	var whenDayOfWeek = checkpoint.isoWeekday();
	var month = checkpoint.month();
	checkpoint.date(1);
	checkpoint.isoWeekday(whenDayOfWeek)
	checkpoint.add("weeks", -1 * (checkpoint.month() - month));
	var i = 0;
	while (checkpoint.month() == month && !checkpoint.isSame(when)) {
		checkpoint.add("weeks", 1)
		i++;
	}
	return i;
}

/* get the nthdayOfTheWeek (fst,snd, tth, fth,last) of the weekday dayOfTheWeek in the month in when*/
function getNextNthdayOfWeek(when, nthdayOfTheWeek, dayOfTheWeek) {
	var checkpoint = when.clone();
	var whenDayOfWeek = checkpoint.isoWeekday();
	var month = checkpoint.month();
	checkpoint.date(1)
	checkpoint.isoWeekday(dayOfTheWeek)
	checkpoint.add("weeks", -1 * (checkpoint.month() - month));
	var i = nthdayOfTheWeek;
	if (i > 3) { // get last of month
		var lastDayOfMonth = checkpoint.endOf('month')
		lastDayOfMonth.isoWeekday(dayOfTheWeek);
		lastDayOfMonth.add('weeks', month - lastDayOfMonth.month());
		return lastDayOfMonth;
	} else {
		checkpoint.add("weeks", nthdayOfTheWeek);
		return checkpoint;
	}
}

function nthDayOfTheWeekLabel(when) {
	var nth = nthdayOfTheWeek(when)
	if (nth > 3) {
		return "last";
	}
	var labels = ["first", "second", "third", "fourth"]
	return labels[nth];
}

function dayOfWeekLabel(when) {
	return when.format("dddd");
};

function getMomentDateFormat() {
	return "DD/MM/YYYY";
}

function getMomentTimeFormat() {
	return "HH:mm";
}

function isAllDay() {
	return $("#allday").prop("checked");
}

function getMoment(date, time) {
	date.hour(time.hour())
	date.minute(time.minute())
	return date
}

function getStartMoment() {
	var startDate = moment($("#startdate").val(),getMomentDateFormat())
	var startTime = getStartTime()
	return getMoment(startDate, startTime)
}

function getEndMoment() {
	var endDate =  moment($("#enddate").val(), getMomentDateFormat())
	var endTime = getEndTime()
	return getMoment(endDate, endTime)
}

function getStartTime() {
	var when = $("#starttime").val()
	if (isAllDay()) {
		when = "00:00"
	}
	return moment(when, getMomentTimeFormat())
}

function getEndTime() {
	var when = $("#endtime").val()
	if (isAllDay()) {
		when = "23:59"
	}
	return moment(when, getMomentTimeFormat())
}

