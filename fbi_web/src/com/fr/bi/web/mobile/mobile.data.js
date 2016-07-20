if (!window.FR) {
    window.FR = {};
};

FR.serverURL = document.location.href;
FR.servletURL = "";

FR.i18n = BI.i18n = {
    Monday: 'Monday',
    Tuesday: 'Tuesday',
    Wednesday: 'Wednesday',
    Thursday: 'Thursday',
    Friday: 'Friday',
    Saturday: 'Saturday',
    Sunday: 'Sunday',
    Mon: 'Mon',
    Tue: 'Tue',
    Wed: 'Wed',
    Thu: 'Thu',
    Fri: 'Fri',
    Sat: 'Sat',
    Sun: 'Sun',
    January: 'January',
    February: 'February',
    March: 'March',
    April: 'April',
    May: 'May',
    June: 'June',
    July: 'July',
    August: 'August',
    September: 'September',
    October: 'October',
    November: 'November',
    December: 'December',
    Jan: 'Jan',
    Feb: 'Feb',
    Mar: 'Mar',
    Apr: 'Apr',
    Short_May: 'Short_May',
    Jun: 'Jun',
    Jul: 'Jul',
    Aug: 'Aug',
    Sep: 'Sep',
    Oct: 'Oct',
    Nov: 'Nov',
    Dec: 'Dec',
    Prev_Year: 'Prev_Year',
    Prev_Month: 'Prev_Month',
    Next_Month: 'Next_Month',
    Next_Year: 'Next_Year',
    Select_Date: 'Select_Date',
    Drag_To_Move: 'Drag_To_Move',
    Today: 'Today',
    OK: 'OK',
    Clear: 'Clear',
    Cancel: 'Cancel',
    First_Of_Week: 'First_Of_Week',
    Close: 'Close',
    Click_To_Change_Value: 'Click_To_Change_Value',
    Week: 'Week',
    Time: 'Time'

};

BI.i18nText = function (key) {
    return key;
};