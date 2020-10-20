package com.ufo;

public class Main {

    public static void main(String[] args) {

        String[] questions = {
                "今年上半年有什么电影",
                "从2010年3月份到二零一九年十二月份有哪些动漫",
                "最近两个礼拜有什么电视剧",
        };

        for (String question : questions) {

            ZHSpokenDateFormat.AnalyzeResult analyzeResult = ZHSpokenDateFormat.analyzeStr(question);

            System.out.println("原句:" + question);
            System.out.println("是否有日期格式匹配:" + analyzeResult.isMatchDate());
            System.out.println("开始时间:" + analyzeResult.getBeginDate());
            System.out.println("结束时间:" + analyzeResult.getEndDate());
            System.out.println("去除日期字符串后剩余的字符串部分:" + analyzeResult.getRemainingPart());

            // 当然你也可以通过以下方式获取匹配文字
            String matchStr = question.replaceAll(analyzeResult.getRemainingPart(), "");
            System.out.println("匹配文字:" + matchStr);

            System.out.println("\n");
        }

    }


}
