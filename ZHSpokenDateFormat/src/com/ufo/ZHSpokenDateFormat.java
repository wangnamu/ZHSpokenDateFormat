package com.ufo;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ZHSpokenDateFormat {

    private static final HashMap<String, String> ZH_MAP_STR = new HashMap() {{
        put("零", "0");
        put("一", "1");
        put("二", "2");
        put("三", "3");
        put("四", "4");
        put("五", "5");
        put("六", "6");
        put("七", "7");
        put("八", "8");
        put("九", "9");
        put("十", "10");
    }};

    /**
     * 解析结果
     */
    public static class AnalyzeResult {

        // 是否有日期格式匹配
        private boolean matchDate;
        // 开始时间
        private String beginDate;
        // 结束时间
        private String endDate;
        // 去除日期字符串后剩余的字符串部分
        private String remainingPart;

        public AnalyzeResult(boolean matchDate, String beginDate, String endDate, String remainingPart) {
            this.matchDate = matchDate;
            this.beginDate = beginDate;
            this.endDate = endDate;
            this.remainingPart = remainingPart;
        }

        public AnalyzeResult(boolean matchDate, String remainingPart) {
            this.matchDate = matchDate;
            this.remainingPart = remainingPart;
        }

        public boolean isMatchDate() {
            return matchDate;
        }

        public void setMatchDate(boolean matchDate) {
            this.matchDate = matchDate;
        }

        public String getBeginDate() {
            return beginDate;
        }

        public void setBeginDate(String beginDate) {
            this.beginDate = beginDate;
        }

        public String getEndDate() {
            return endDate;
        }

        public void setEndDate(String endDate) {
            this.endDate = endDate;
        }

        public String getRemainingPart() {
            return remainingPart;
        }

        public void setRemainingPart(String remainingPart) {
            this.remainingPart = remainingPart;
        }
    }


    /**
     * 匹配日期结果
     */
    private static class MatchDateResult {

        private boolean match;
        private String begin;
        private String end;
        private String fullMatchStr;

        public MatchDateResult() {
        }

        public MatchDateResult(boolean match) {
            this.match = match;
        }

        public MatchDateResult(String begin, String end, String fullMatchStr) {
            this.begin = begin;
            this.end = end;
            this.fullMatchStr = fullMatchStr;
            this.match = true;
        }

        public MatchDateResult(boolean match, String begin, String end, String fullMatchStr) {
            this.match = match;
            this.begin = begin;
            this.end = end;
            this.fullMatchStr = fullMatchStr;
        }

        public boolean isMatch() {
            return match;
        }

        public void setMatch(boolean match) {
            this.match = match;
        }

        public String getBegin() {
            return begin;
        }

        public void setBegin(String begin) {
            this.begin = begin;
        }

        public String getEnd() {
            return end;
        }

        public void setEnd(String end) {
            this.end = end;
        }

        public String getFullMatchStr() {
            return fullMatchStr;
        }

        public void setFullMatchStr(String fullMatchStr) {
            this.fullMatchStr = fullMatchStr;
        }

        public static MatchDateResult createNone() {
            return new MatchDateResult(false);
        }

    }


    public static AnalyzeResult analyzeStr(String str) {

        Pattern pattern = Pattern.compile(
                "(从?(?<beginField>[\\S\\s]*)到(?<endField>[\\S\\s]*))"
        );

        Matcher matcher = pattern.matcher(str);
        if (matcher.find()) {

            String beginField = matcher.group("beginField");
            String endField = matcher.group("endField");

            if (beginField != null && endField != null) {
                AnalyzeResult beginAnalyzeResult = analyze(beginField);
                AnalyzeResult endAnalyzeResult = analyze(endField);

                if (beginAnalyzeResult.isMatchDate() && endAnalyzeResult.isMatchDate()) {

                    String begin = beginAnalyzeResult.getBeginDate();
                    String end = endAnalyzeResult.getEndDate();
                    String remainingPart = null;

                    if (!"".equals(beginAnalyzeResult.getRemainingPart())) {
                        remainingPart = beginAnalyzeResult.getRemainingPart();
                        if (remainingPart.startsWith("从") || remainingPart.endsWith("从")) {
                            remainingPart = remainingPart.replace("从", "");
                        }
                    }

                    if (!"".equals(endAnalyzeResult.getRemainingPart())) {
                        remainingPart = endAnalyzeResult.getRemainingPart();
                        if (remainingPart.startsWith("从") || remainingPart.endsWith("从")) {
                            remainingPart = remainingPart.replace("从", "");
                        }
                    }

                    return new AnalyzeResult(true, begin, end, remainingPart);

                }

            }

        }

        return analyze(str);


    }

    /**
     * 语义解析
     *
     * @param str
     * @return
     */
    private static AnalyzeResult analyze(String str) {

        boolean matchDate = false;
        String input = str;
        String beginYear = null;
        String endYear = null;
        String beginMonth = null;
        String endMonth = null;
        String beginDay = null;
        String endDay = null;

        //年
        MatchDateResult matchYear = matchYear(input);
        if (matchYear.isMatch()) {

            matchDate = true;

            input = input.replace(matchYear.getFullMatchStr(), "");
            String[] arrBegin = matchYear.getBegin().split("-");
            String[] arrEnd = matchYear.getEnd().split("-");
            beginYear = arrBegin[0];
            beginMonth = arrBegin[1];
            beginDay = arrBegin[2];
            endYear = arrEnd[0];
            endMonth = arrEnd[1];
            endDay = arrEnd[2];
        }

        if (beginYear == null && endYear == null) {
            beginYear = getYearNow();
            endYear = getYearNow();
        }

        //季度
        MatchDateResult matchQuarterly = matchQuarterly(input, beginYear);
        if (matchQuarterly.isMatch()) {

            matchDate = true;

            input = input.replace(matchQuarterly.getFullMatchStr(), "");
            String[] arrBegin = matchQuarterly.getBegin().split("-");
            String[] arrEnd = matchQuarterly.getEnd().split("-");
            beginYear = arrBegin[0];
            beginMonth = arrBegin[1];
            beginDay = arrBegin[2];
            endYear = arrEnd[0];
            endMonth = arrEnd[1];
            endDay = arrEnd[2];
        }


        //月
        MatchDateResult matchMonth = matchMonth(input, beginYear);
        if (matchMonth.isMatch()) {

            matchDate = true;

            input = input.replace(matchMonth.getFullMatchStr(), "");
            String[] arrBegin = matchMonth.getBegin().split("-");
            String[] arrEnd = matchMonth.getEnd().split("-");
            beginYear = arrBegin[0];
            beginMonth = arrBegin[1];
            beginDay = arrBegin[2];
            endYear = arrEnd[0];
            endMonth = arrEnd[1];
            endDay = arrEnd[2];
        }

        if (beginMonth == null && endMonth == null) {
            beginMonth = getMonthNow();
            endMonth = getMonthNow();
        }


        //天
        MatchDateResult matchDay = matchDay(input, beginYear, beginMonth);
        if (matchDay.isMatch()) {

            matchDate = true;

            input = input.replace(matchDay.getFullMatchStr(), "");
            String[] arrBegin = matchDay.getBegin().split("-");
            String[] arrEnd = matchDay.getEnd().split("-");
            beginYear = arrBegin[0];
            beginMonth = arrBegin[1];
            beginDay = arrBegin[2];
            endYear = arrEnd[0];
            endMonth = arrEnd[1];
            endDay = arrEnd[2];
        }


        //星期
        MatchDateResult matchWeek = matchWeek(input, beginYear, beginMonth);
        if (matchWeek.isMatch()) {

            matchDate = true;

            input = input.replace(matchWeek.getFullMatchStr(), "");
            String[] arrBegin = matchWeek.getBegin().split("-");
            String[] arrEnd = matchWeek.getEnd().split("-");
            beginYear = arrBegin[0];
            beginMonth = arrBegin[1];
            beginDay = arrBegin[2];
            endYear = arrEnd[0];
            endMonth = arrEnd[1];
            endDay = arrEnd[2];
        }


        if (matchDate) {
            return new AnalyzeResult(true,
                    beginYear + "-" + beginMonth + "-" + beginDay,
                    endYear + "-" + endMonth + "-" + endDay,
                    input);
        } else {
            return new AnalyzeResult(false, input);
        }


    }


    /**
     * 匹配年
     *
     * @param input
     * @return
     */
    private static MatchDateResult matchYear(String input) {
        Pattern pattern = Pattern.compile(
                "((?<yearStd>\\d{2,4}|[零一二三四五六七八九]{2,4})[-|\\/|年|\\.|\\s])|"
                        + "((?<yearOral>[去前今本明后]|(前|近|最近)[一二两三四五六七八九123456789])年)"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            String yearStd = matcher.group("yearStd");
            String yearOral = matcher.group("yearOral");

            if (yearStd != null) {
                String year = formatDateField(yearStd, "year");
                return new MatchDateResult(year + "-01-01", year + "-12-31", matcher.group());
            }

            if (yearOral != null) {
                String nowYear = getYearNow();
                String begin = nowYear;
                String end = nowYear;
                if (!isPeriod(yearOral, "year")) {
                    if (yearOral.contains("去")) {
                        begin = dateCalculation(nowYear, null, null, "year", -1);
                    } else if (yearOral.contains("前")) {
                        begin = dateCalculation(nowYear, null, null, "year", -2);
                    } else if (yearOral.contains("明")) {
                        end = dateCalculation(nowYear, null, null, "year", 1);
                    } else if (yearOral.contains("后")) {
                        end = dateCalculation(nowYear, null, null, "year", 2);
                    } else {

                    }
                } else {
                    int amount = -getAmount(yearOral);
                    begin = dateCalculation(nowYear, null, null, "year", amount);
                }
                return new MatchDateResult(begin + "-01-01", end + "-12-31", matcher.group());
            }

        }

        return MatchDateResult.createNone();
    }


    /**
     * 匹配季度
     *
     * @param input
     * @param year
     * @return
     */
    private static MatchDateResult matchQuarterly(String input, String year) {
        Pattern pattern = Pattern.compile(
                "(?<quarterly>(([这本上下]|(前|近|最近)[一二两三四五六七八九123456789])|(第?[一二三四1234]))个?季度)"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {

            String quarterly = matcher.group("quarterly");

            String nowMonth = getMonthNow();
            String begin = nowMonth;
            String end = nowMonth;
            String tagBeginYear = year;
            String tagEndYear = year;
            if (!isPeriod(quarterly, "quarterly")) {
                if (quarterly.contains("上")) {
                    int tagQ = getQuarterlyNow() - 1;
                    //当年
                    if (tagQ >= 1) {
                        String[] BEMonth = getMonthByQuarterly(tagQ);
                        begin = BEMonth[0];
                        end = BEMonth[1];
                    }
                    //上一年
                    else {
                        String[] BEMonth = getMonthByQuarterly(4);
                        begin = BEMonth[0];
                        end = BEMonth[1];
                        tagBeginYear = dateCalculation(tagBeginYear, null, null, "year", -1);
                    }
                } else if (quarterly.contains("下")) {
                    int tagQ = getQuarterlyNow() + 1;
                    //当年
                    if (tagQ <= 4) {
                        String[] BEMonth = getMonthByQuarterly(tagQ);
                        begin = BEMonth[0];
                        end = BEMonth[1];
                    }
                    //下一年
                    else {
                        String[] BEMonth = getMonthByQuarterly(1);
                        begin = BEMonth[0];
                        end = BEMonth[1];
                        tagEndYear = dateCalculation(tagEndYear, null, null, "year", 1);
                    }
                } else if (quarterly.contains("第") || getAmount(quarterly) > 0) {
                    int tagQ = getAmount(quarterly);
                    String[] BEMonth = getMonthByQuarterly(tagQ);
                    begin = BEMonth[0];
                    end = BEMonth[1];
                } else {
                    int nowQ = getQuarterlyNow();
                    String[] BEMonth = getMonthByQuarterly(nowQ);
                    begin = BEMonth[0];
                    end = BEMonth[1];
                }
            } else {
                int amount = -getAmount(quarterly);
                String[] arr = dateCalculationFull(tagBeginYear, begin, null, "month", amount * 3);
                tagBeginYear = arr[0];
                begin = arr[1];
            }
            return new MatchDateResult(getFirstDayOfMonth(tagBeginYear, begin),
                    getLastDayOfMonth(tagEndYear, end), matcher.group());
        }

        return MatchDateResult.createNone();
    }


    private static MatchDateResult matchMonth(String input, String year) {
        Pattern pattern = Pattern.compile(
                "((?<monthStd>0?[1-9]|1[0-2]|[一二三四五六七八九]|十[一二]?)[-|\\/|月|\\.|\\s]份?)|"
                        + "(?<monthOral>((([这本上下]|(上|前|近|最近)[半一二两三四五六七八九123456789])个?月)|(上|下|近|最近)半年))"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {

            String monthStd = matcher.group("monthStd");
            String monthOral = matcher.group("monthOral");

            if (monthStd != null) {
                String month = formatDateField(monthStd, "month");
                return new MatchDateResult(getFirstDayOfMonth(year, month), getLastDayOfMonth(year, month), matcher.group());
            }

            if (monthOral != null) {

                String nowMonth = getMonthNow();
                String begin = nowMonth;
                String end = nowMonth;
                String tagBeginYear = year;
                String tagEndYear = year;

                if (monthOral.contains("年")) {
                    if (monthOral.contains("上")) {
                        begin = "01";
                        end = "06";
                    } else if (monthOral.contains("下")) {
                        begin = "07";
                        end = "12";
                    } else {
                        String[] arr = dateCalculationFull(tagBeginYear, begin, null, "month", -6);
                        tagBeginYear = arr[0];
                        begin = arr[1];
                    }
                } else {
                    if (monthOral.contains("半")) {
                        String[] arr = dateCalculationFull(tagBeginYear, null, null, "week", -2);
                        return new MatchDateResult(arr[0] + "-" + arr[1] + "-" + arr[2],
                                tagEndYear + "-" + end + "-" + getDayNow(), matcher.group());
                    } else {
                        if (!isPeriod(monthOral, "month")) {
                            if (monthOral.contains("上")) {
                                String[] arr = dateCalculationFull(tagBeginYear, null, null, "month", -1);
                                tagBeginYear = arr[0];
                                tagEndYear = arr[0];
                                begin = arr[1];
                                end = arr[1];
                            } else if (monthOral.contains("下")) {
                                String[] arr = dateCalculationFull(tagBeginYear, null, null, "month", 1);
                                tagBeginYear = arr[0];
                                tagEndYear = arr[0];
                                begin = arr[1];
                                end = arr[1];
                            } else {

                            }
                        } else {
                            int amount = -getAmount(monthOral);
                            String[] arr = dateCalculationFull(tagBeginYear, begin, null, "month", amount);
                            tagBeginYear = arr[0];
                            begin = arr[1];
                        }
                    }
                }

                return new MatchDateResult(getFirstDayOfMonth(tagBeginYear, begin),
                        getLastDayOfMonth(tagEndYear, end), matcher.group());
            }

        }

        return MatchDateResult.createNone();
    }


    /**
     * 匹配星期
     *
     * @param input
     * @param year
     * @param month
     * @return
     */
    private static MatchDateResult matchWeek(String input, String year, String month) {
        Pattern pattern = Pattern.compile(
                "((?<week>[这本上下]|(前|近|最近)[一二两三四五六七八九123456789])个?(周|礼拜|星期))"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {

            String week = matcher.group("week");

            if (week != null) {

                String tagBeginYear = year;
                String tagEndYear = year;

                String tagBeginMonth = month;
                String tagEndMonth = month;

                String nowDay = getDayNow();
                String begin = nowDay;
                String end = nowDay;

                if (!isPeriod(week, "week")) {
                    if (week.contains("上")) {
                        String[][] arr = getWeek(tagBeginYear, tagBeginMonth, nowDay, -1);
                        tagBeginYear = arr[0][0];
                        tagEndYear = arr[1][0];
                        tagBeginMonth = arr[0][1];
                        tagEndMonth = arr[1][1];
                        begin = arr[0][2];
                        end = arr[1][2];
                    } else if (week.contains("下")) {
                        String[][] arr = getWeek(tagBeginYear, tagBeginMonth, nowDay, 1);
                        tagBeginYear = arr[0][0];
                        tagEndYear = arr[1][0];
                        tagBeginMonth = arr[0][1];
                        tagEndMonth = arr[1][1];
                        begin = arr[0][2];
                        end = arr[1][2];
                    } else {
                        String[][] arr = getWeek(tagBeginYear, tagBeginMonth, nowDay, 0);
                        tagBeginYear = arr[0][0];
                        tagEndYear = arr[1][0];
                        tagBeginMonth = arr[0][1];
                        tagEndMonth = arr[1][1];
                        begin = arr[0][2];
                        end = arr[1][2];
                    }
                } else {
                    int amount = -getAmount(week);
                    String[][] arr = getWeek(tagBeginYear, tagBeginMonth, nowDay, amount);
                    tagBeginYear = arr[0][0];
                    tagEndYear = arr[1][0];
                    tagBeginMonth = arr[0][1];
                    tagEndMonth = arr[1][1];
                    begin = arr[0][2];
                    end = arr[1][2];
                }

                return new MatchDateResult(tagBeginYear + "-" + tagBeginMonth + "-" + begin,
                        tagEndYear + "-" + tagEndMonth + "-" + end, matcher.group());
            }

        }

        return MatchDateResult.createNone();
    }


    /**
     * 匹配天
     *
     * @param input
     * @param year
     * @param month
     * @return
     */
    private static MatchDateResult matchDay(String input, String year, String month) {
        Pattern pattern = Pattern.compile(
                "((?<dayStd>0?[1-9]|[0-2]\\d|3[0-1]|[一二三四五六七八九]|十[一二三四五六七八九]?|二十[一二三四五六七八九]?|三十[一]?)[-|\\/|日|号｜\\.|\\s])|"
                        + "(?<dayOral>(([上中下]旬)|(现在)|(([昨今前明后]|(前|近|最近)[一二两三四五六七八九123456789]个?)[天日])|([上下这]?个?(星期|礼拜|周)[一二三四五六七天日1234567])))"
        );

        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {

            String dayStd = matcher.group("dayStd");
            String dayOral = matcher.group("dayOral");

            if (dayStd != null) {
                String day = formatDateField(dayStd, "day");
                return new MatchDateResult(year + "-" + month + "-" + day,
                        year + "-" + month + "-" + day, matcher.group());
            }

            if (dayOral != null) {

                String tagBeginYear = year;
                String tagEndYear = year;

                String tagBeginMonth = month;
                String tagEndMonth = month;

                String nowDay = getDayNow();
                String begin = nowDay;
                String end = nowDay;

                if (!isPeriod(dayOral, "day")) {
                    if (dayOral.contains("昨")) {
                        String[] arr = dateCalculationFull(tagBeginYear, tagBeginMonth, begin, "day", -1);
                        tagBeginYear = arr[0];
                        tagEndYear = arr[0];
                        tagBeginMonth = arr[1];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                        end = arr[2];
                    } else if (dayOral.contains("前")) {
                        String[] arr = dateCalculationFull(tagBeginYear, tagBeginMonth, begin, "day", -2);
                        tagBeginYear = arr[0];
                        tagEndYear = arr[0];
                        tagBeginMonth = arr[1];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                        end = arr[2];
                    } else if (dayOral.contains("明")) {
                        String[] arr = dateCalculationFull(tagBeginYear, tagBeginMonth, begin, "day", 1);
                        tagBeginYear = arr[0];
                        tagEndYear = arr[0];
                        tagBeginMonth = arr[1];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                        end = arr[2];
                    } else if (dayOral.contains("后")) {
                        String[] arr = dateCalculationFull(tagBeginYear, tagBeginMonth, begin, "day", 2);
                        tagBeginYear = arr[0];
                        tagEndYear = arr[0];
                        tagBeginMonth = arr[1];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                        end = arr[2];
                    } else if (dayOral.contains("今") || dayOral.equals("现在")) {
                        tagBeginYear = getYearNow();
                        tagEndYear = getYearNow();
                        tagBeginMonth = getMonthNow();
                        tagBeginMonth = getMonthNow();
                        begin = nowDay;
                        end = nowDay;
                    } else {
                        String[] arr;
                        if (dayOral.contains("上")) {
                            arr = getDayOfWeek(tagBeginYear, tagBeginMonth, begin, -1, dayOral);
                        } else if (dayOral.contains("下")) {
                            arr = getDayOfWeek(tagBeginYear, tagBeginMonth, begin, 1, dayOral);
                        } else {
                            arr = getDayOfWeek(tagBeginYear, tagBeginMonth, begin, 0, dayOral);
                        }
                        tagBeginYear = arr[0];
                        tagEndYear = arr[0];
                        tagBeginMonth = arr[1];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                        end = arr[2];
                    }
                } else {
                    if (dayOral.contains("旬")) {
                        if (dayOral.contains("上旬")) {
                            begin = "01";
                            end = "10";
                        } else if (dayOral.contains("中旬")) {
                            begin = "11";
                            end = "20";
                        } else {
                            begin = "21";
                            end = getLastDayOfMonth(tagBeginYear, tagBeginMonth).split("-")[2];
                        }
                    } else {
                        int amount = -getAmount(dayOral);
                        String[] arr = dateCalculationFull(tagBeginYear, tagBeginMonth, begin, "day", amount);
                        tagBeginYear = arr[0];
                        tagBeginMonth = arr[1];
                        begin = arr[2];
                    }
                }
                return new MatchDateResult(tagBeginYear + "-" + tagBeginMonth + "-" + begin,
                        tagEndYear + "-" + tagEndMonth + "-" + end, matcher.group());

            }

        }

        return MatchDateResult.createNone();
    }


    /**
     * 格式化年、月、日
     *
     * @param field
     * @return
     */
    private static String formatDateField(String field, String type) {
        if (field == null) {
            return null;
        }
        Pattern pattern = Pattern.compile("^[\u4E00-\u9FA5]{" + field.length() + "}$");
        Matcher matcher = pattern.matcher(field);
        String result;
        if (matcher.matches()) {
            result = zhToNum(field, type);
        } else {
            result = zeroPadding(field, type);
        }
        return result;
    }


    /**
     * 中文转数字
     *
     * @param zh
     * @return
     */
    private static String zhToNum(String zh, String type) {
        if (zh == null) {
            return null;
        }

        if ("year".equals(type)) {
            String result;
            if (zh.length() == 2) {
                String pre = getYearNow().substring(0, 2);
                result = pre + replaceChineseChar(zh);
            } else if (zh.length() == 3) {
                String pre = getYearNow().substring(0, 2);
                result = pre + replaceChineseChar(zh.substring(1, 3));
            } else {
                result = replaceChineseChar(zh);
            }
            return result;
        }

        if ("month".equals(type)) {
            String result;
            if (zh.length() == 1) {
                result = "0" + replaceChineseChar(zh);
            } else {
                result = "1" + replaceChineseChar(zh.substring(1, 2));
            }
            return result;
        }

        if ("day".equals(type)) {
            String result;
            if (zh.length() == 1) {
                result = "0" + replaceChineseChar(zh);
            } else if (zh.length() == 2) {
                if (zh.startsWith("十")) {
                    result = "1" + replaceChineseChar(zh.substring(1, 2));
                } else {
                    result = replaceChineseChar(zh.substring(0, 1)) + "0";
                }
            } else {
                result = replaceChineseChar(zh.substring(0, 1) + zh.substring(2, 3));
            }
            return result;
        }


        return null;
    }

    /**
     * 日期格式内容格式化(补0)
     *
     * @param num
     * @return
     */
    private static String zeroPadding(String num, String type) {
        if (num == null) {
            return null;
        }

        if ("year".equals(type)) {
            String result;
            if (num.length() == 2) {
                String pre = getYearNow().substring(0, 2);
                result = pre + num;
            } else if (num.length() == 3) {
                String pre = getYearNow().substring(0, 2);
                result = pre + num.substring(1, 3);
            } else {
                result = num;
            }
            return result;
        }

        if ("month".equals(type)) {
            String result;
            if (num.length() == 1) {
                result = "0" + num;
            } else {
                result = num;
            }
            return result;
        }

        if ("day".equals(type)) {
            String result;
            if (num.length() == 1) {
                result = "0" + num;
            } else {
                result = num;
            }
            return result;
        }

        return null;
    }

    /**
     * 汉字数字替换成阿拉伯数字
     *
     * @param s
     * @return
     */
    private static String replaceChineseChar(String s) {
        char[] chars = s.toCharArray();
        StringBuilder sb = new StringBuilder();
        for (char c : chars) {
            sb.append(ZH_MAP_STR.get(String.valueOf(c)));
        }
        return sb.toString();
    }


    /**
     * 获取当前时间
     *
     * @return
     */
    private static String[] getNow() {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String dateStr = dateFormat.format(System.currentTimeMillis());
        return dateStr.split("-");
    }

    /**
     * 获取本年
     *
     * @return
     */
    private static String getYearNow() {
        return getNow()[0];
    }

    /**
     * 获取本月
     *
     * @return
     */
    private static String getMonthNow() {
        return getNow()[1];
    }

    /**
     * 获取本天
     *
     * @return
     */
    private static String getDayNow() {
        return getNow()[2];
    }

    /**
     * 获取本季度
     *
     * @return
     */
    private static int getQuarterlyNow() {
        int monthNow = Integer.parseInt(getMonthNow());
        if (monthNow >= 1 && monthNow <= 3) {
            return 1;
        } else if (monthNow >= 4 && monthNow <= 6) {
            return 2;
        } else if (monthNow >= 7 && monthNow <= 9) {
            return 3;
        } else {
            return 4;
        }
    }

    /**
     * 根据季度获取月
     *
     * @param quarterly
     * @return
     */
    private static String[] getMonthByQuarterly(int quarterly) {
        String begin = null;
        String end = null;
        switch (quarterly) {
            case 1:
                begin = "1";
                end = "3";
                break;
            case 2:
                begin = "4";
                end = "6";
                break;
            case 3:
                begin = "7";
                end = "9";
                break;
            case 4:
                begin = "10";
                end = "12";
                break;
            default:
                break;
        }
        return new String[]{begin, end};
    }


    /**
     * 获取月的最后一天
     *
     * @param year
     * @param month
     * @return
     */
    private static String getLastDayOfMonth(String year, String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }


    /**
     * 获取月的第一天
     *
     * @param year
     * @param month
     * @return
     */
    private static String getFirstDayOfMonth(String year, String month) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, Integer.parseInt(year));
        calendar.set(Calendar.MONTH, Integer.parseInt(month) - 1);
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getMinimum(Calendar.DATE));
        return new SimpleDateFormat("yyyy-MM-dd").format(calendar.getTime());
    }

    /**
     * 获取某一周
     *
     * @param dYear
     * @param dMonth
     * @param dDay
     * @param amount
     * @return
     */
    private static String[][] getWeek(String dYear, String dMonth, String dDay, int amount) {

        String yyyy = dYear == null ? getYearNow() : dYear;
        String MM = dMonth == null ? getMonthNow() : dMonth;
        String dd = dDay == null ? getDayNow() : dDay;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(yyyy), Integer.parseInt(MM) - 1, Integer.parseInt(dd));

        calendar.add(Calendar.WEEK_OF_MONTH, amount);

        calendar.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);

        String[] begin = new String[]{String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH) + 1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))};

        calendar.add(Calendar.DATE, 6);

        String[] end = new String[]{String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH) + 1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))};

        return new String[][]{begin, end};
    }


    /**
     * 获取星期几的实际日期
     *
     * @param dYear
     * @param dMonth
     * @param dDay
     * @param amount
     * @param dayOfWeek
     * @return
     */
    private static String[] getDayOfWeek(String dYear, String dMonth, String dDay, int amount, String dayOfWeek) {
        String yyyy = dYear == null ? getYearNow() : dYear;
        String MM = dMonth == null ? getMonthNow() : dMonth;
        String dd = dDay == null ? getDayNow() : dDay;

        Calendar calendar = Calendar.getInstance();
        calendar.set(Integer.parseInt(yyyy), Integer.parseInt(MM) - 1, Integer.parseInt(dd));

        calendar.add(Calendar.WEEK_OF_MONTH, amount);
        calendar.set(Calendar.DAY_OF_WEEK, zhDayOfWeek(dayOfWeek));

        return new String[]{String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH) + 1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))};
    }


    /**
     * 周几中英格式转换
     *
     * @param dayOfWeek
     * @return
     */
    private static int zhDayOfWeek(String dayOfWeek) {
        if (dayOfWeek.contains("一") || dayOfWeek.contains("1")) {
            return Calendar.MONDAY;
        } else if (dayOfWeek.contains("二") || dayOfWeek.contains("2")) {
            return Calendar.TUESDAY;
        } else if (dayOfWeek.contains("三") || dayOfWeek.contains("3")) {
            return Calendar.WEDNESDAY;
        } else if (dayOfWeek.contains("四") || dayOfWeek.contains("4")) {
            return Calendar.THURSDAY;
        } else if (dayOfWeek.contains("五") || dayOfWeek.contains("5")) {
            return Calendar.FRIDAY;
        } else if (dayOfWeek.contains("六") || dayOfWeek.contains("6")) {
            return Calendar.SATURDAY;
        } else {
            return Calendar.SUNDAY;
        }
    }

    /**
     * 是否是时间段
     *
     * @param str
     * @param type
     * @return
     */
    private static boolean isPeriod(String str, String type) {
        if (str == null) {
            return false;
        }

        if ("year".equals(type)) {
            String[] arr = {"前", "近", "最近"};
            for (String item : arr) {
                if (!item.equals(str) && str.contains(item)) {
                    return true;
                }
            }
        } else if ("quarterly".equals(type)) {
            String[] arr = {"前", "近", "最近"};
            for (String item : arr) {
                if (!item.equals(str) && str.contains(item)) {
                    return true;
                }
            }
        } else if ("month".equals(type)) {
            String[] arr = {"上半", "下半", "前半", "后半", "前", "近", "最近"};
            for (String item : arr) {
                if (!item.equals(str) && str.contains(item)) {
                    return true;
                }
            }
        } else if ("week".equals(type)) {
            String[] arr = {"前", "近", "最近"};
            for (String item : arr) {
                if (!item.equals(str) && str.contains(item)) {
                    return true;
                }
            }
        } else if ("day".equals(type)) {
            String[] arr = {"前", "近", "最近"};
            for (String item : arr) {
                if (!item.equals(str) && str.contains(item)) {
                    return true;
                }
            }
            if ("上旬".equals(str) || "中旬".equals(str) || "下旬".equals(str)) {
                return true;
            }
        }

        return false;
    }


    /**
     * 日期计算
     *
     * @param dYear
     * @param dMonth
     * @param dDay
     * @param type
     * @param amount
     * @return
     */
    private static String dateCalculation(String dYear, String dMonth, String dDay, String type, int amount) {
        Calendar calendar = Calendar.getInstance();
        String yyyy = dYear == null ? getYearNow() : dYear;
        String MM = dMonth == null ? getMonthNow() : dMonth;
        String dd = dDay == null ? getDayNow() : dDay;
        calendar.set(Integer.parseInt(yyyy), Integer.parseInt(MM) - 1, Integer.parseInt(dd));

        if ("year".equals(type)) {
            calendar.add(Calendar.YEAR, amount);
            return String.valueOf(calendar.get(Calendar.YEAR));
        } else if ("month".equals(type)) {
            calendar.add(Calendar.MONTH, amount);
            return String.valueOf(calendar.get(Calendar.MONTH) + 1);
        } else if ("week".equals(type)) {
            calendar.add(Calendar.WEEK_OF_MONTH, amount);
            return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, amount);
            return String.valueOf(calendar.get(Calendar.DAY_OF_MONTH));
        }

    }

    /**
     * 日期计算
     *
     * @param dYear
     * @param dMonth
     * @param dDay
     * @param type
     * @param amount
     * @return
     */
    private static String[] dateCalculationFull(String dYear, String dMonth, String dDay, String type, int amount) {
        Calendar calendar = Calendar.getInstance();
        String yyyy = dYear == null ? getYearNow() : dYear;
        String MM = dMonth == null ? getMonthNow() : dMonth;
        String dd = dDay == null ? getDayNow() : dDay;
        calendar.set(Integer.parseInt(yyyy), Integer.parseInt(MM) - 1, Integer.parseInt(dd));

        if ("year".equals(type)) {
            calendar.add(Calendar.YEAR, amount);
        } else if ("month".equals(type)) {
            calendar.add(Calendar.MONTH, amount);
        } else if ("week".equals(type)) {
            calendar.add(Calendar.WEEK_OF_MONTH, amount);
        } else {
            calendar.add(Calendar.DAY_OF_MONTH, amount);
        }

        return new String[]{String.valueOf(calendar.get(Calendar.YEAR)),
                String.valueOf(calendar.get(Calendar.MONTH) + 1),
                String.valueOf(calendar.get(Calendar.DAY_OF_MONTH))};

    }


    /**
     * 获取计算单位
     *
     * @param str
     * @return
     */
    private static int getAmount(String str) {
        int amount = 0;
        if (str.contains("1") || str.contains("一")) {
            amount = 1;
        } else if (str.contains("2") || str.contains("二") || str.contains("两")) {
            amount = 2;
        } else if (str.contains("3") || str.contains("三")) {
            amount = 3;
        } else if (str.contains("4") || str.contains("四")) {
            amount = 4;
        } else if (str.contains("5") || str.contains("五")) {
            amount = 5;
        } else if (str.contains("6") || str.contains("六")) {
            amount = 6;
        } else if (str.contains("7") || str.contains("七")) {
            amount = 7;
        } else if (str.contains("8") || str.contains("八")) {
            amount = 8;
        } else if (str.contains("9") || str.contains("九")) {
            amount = 9;
        } else {
        }
        return amount;
    }


}
