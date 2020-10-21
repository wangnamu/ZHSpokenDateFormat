# ZHSpokenDateFormat

## 介绍

一个Java工具类，实现中文语句提取口语化的中文日期并转换为YYYY-MM-DD格式


## 描述


基于正则表达式关键字提取，自年、季度、月、周、日向下提取拼接成日期字符串，同时支持“从……到……”形式的范围日期查找。

日期|口语格式|同义词
--|--:|--:
年|去、前、今、本、明、后、前、近、最近|YYYY、YY
季度|这、本、上、下、前、近、最近、第|-
月|这、本、上、下、上、前、近、最近、半年|MM、M
周|这、本、上、下、前、近、最近|周、礼拜、星期|-
日|现在、昨、今、前、明、后、前、近、最近、半个月、月上旬、月中旬、月下旬|DD、D、天、日、号、星期X、周X、礼拜X


## 使用

### 代码
```java
ZHSpokenDateFormat.AnalyzeResult analyzeResult = ZHSpokenDateFormat.analyzeStr("今年上半年有什么电影");
```

### AnalyzeResult 类属性
```java
// 是否有日期格式匹配
private boolean matchDate;
// 开始时间
private String beginDate;
// 结束时间
private String endDate;
// 去除日期字符串后剩余的字符串部分
private String remainingPart;
```

## 示例

### 代码
```java

 public static void main(String[] args) {

        String[] questions = {
                "今年上半年有什么电影",
                "从2010年3月份到二零一九年十二月份有哪些动漫",
                "最近两个礼拜有什么电视剧",
                "七月上旬开了几场演唱会",
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

```

### 结果

```
原句:今年上半年有什么电影
是否有日期格式匹配:true
开始时间:2020-01-01
结束时间:2020-06-30
去除日期字符串后剩余的字符串部分:有什么电影
匹配文字:今年上半年


原句:从2010年3月份到二零一九年十二月份有哪些动漫
是否有日期格式匹配:true
开始时间:2010-03-01
结束时间:2019-12-31
去除日期字符串后剩余的字符串部分:有哪些动漫
匹配文字:从2010年3月份到二零一九年十二月份


原句:最近两个礼拜有什么电视剧
是否有日期格式匹配:true
开始时间:2020-10-5
结束时间:2020-10-11
去除日期字符串后剩余的字符串部分:有什么电视剧
匹配文字:最近两个礼拜


原句:七月上旬开了几场演唱会
是否有日期格式匹配:true
开始时间:2020-07-01
结束时间:2020-07-10
去除日期字符串后剩余的字符串部分:开了几场演唱会
匹配文字:七月上旬
```

## License

ZHSpokenDateFormat is a MIT licensed




