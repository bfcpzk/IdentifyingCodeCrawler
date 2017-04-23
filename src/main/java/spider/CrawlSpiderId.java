package spider;

import model.Keyword;
import util.Jdbc_Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by zhaokangpan on 2017/4/16.
 */
public class CrawlSpiderId {

    //sdf
    static SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

    //main_url
    static String urlMain = "http://wenshu.court.gov.cn/List/ListContent";

    //mysql
    static Jdbc_Util db = new Jdbc_Util("127.0.0.1");//mysql控制对象

    //param map
    static Map<String, String> paramMap = new HashMap<String, String>();

    //wenshu type
    static String[] wType = new String[]{"判决书","裁定书","调解书","决定书","通知书","批复","答复","函","令","其他"};

    public static void main(String[] args){

        String type = args[0];

        boolean breakCheck = Boolean.parseBoolean(args[1]);

        List<Keyword> kList = getKeywordByType(type);

        List<String> timeList = getDateListByStartEnd(args[2], args[3]);

        initialParam();

        if(!breakCheck){//don't check break
            process(kList, timeList);
        }else{//check break
            continueProcess(kList, timeList, type);
        }

        //paramMap.put("Param","案件类型:民事案件,关键词:合同,裁判日期:2017-04-04 TO 2017-04-04");
    }

    public static void continueProcess( List<Keyword> kList, List<String> timeList, String type){
        List<Keyword> firstkList = new ArrayList<Keyword>();
        List<String> firstDayTimeList = new ArrayList<String>();
        String firstDay = timeList.get(0);
        timeList.remove(0);//remove the incomplete day info
        firstDayTimeList.add(firstDay);
        ResultSet rs;
        //get all keyword
        String sqlCheckKeyword = "select k_name from `keyword` where k_type='" + type + "' and k_name not in (select DISTINCT(case_keyword) from `wenshu` where case_verdict_date='" + firstDay + "' and case_type='" + type + "')";
        rs = db.select(sqlCheckKeyword);
        try {
            while(rs.next()){
                String name = rs.getString("k_name");
                Keyword keyword = new Keyword();
                keyword.setK_name(name);
                firstkList.add(keyword);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        //break day
        process(firstkList, firstDayTimeList);

        //continue
        process(kList, timeList);
    }

    public static void process(List<Keyword> kList, List<String> timeList){
        for(int i = 0 ; i < timeList.size() ; i++){//time iteration
            for(int j = 0 ; j < kList.size() ; j++){//keyword iteration
                String paramCombine = "案件类型:民事案件,关键词:" + kList.get(j).getK_name() + ",裁判日期:" + timeList.get(i) + " TO " + timeList.get(i);
                System.out.println("裁判日期:" + timeList.get(i) + "    关键词:" + kList.get(j).getK_name());
                paramMap.put("Param", paramCombine);
                int pageNum = SpiderUtil.judgePageNumber(urlMain, paramMap);
                if(pageNum > 0){
                    for(int k = 0 ; k < wType.length ; k++){
                        String tmpCombine = paramCombine + ",文书类型:" + wType[k];
                        paramMap.put("Param", tmpCombine);
                        System.out.println("裁判日期:" + timeList.get(i) + "    关键词:" + kList.get(j).getK_name() + "    文书类型:" + wType[k]);
                        for(int page = 1 ; page < pageNum ; page++) {
                            int flag = SpiderUtil.mainProcess(urlMain, paramMap, page, kList.get(j).getK_name(), wType[k], db);
                            if(flag > 0 && flag != 222) SpiderUtil.mainProcess(urlMain, paramMap, page, kList.get(j).getK_name(), wType[k], db);
                            if(flag == 222){
                                System.out.println("无数据！");
                                break;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void initialParam(){
        paramMap.put("Page","20");
        paramMap.put("Order","法院层级");
        paramMap.put("Direction","asc");
    }

    public static List<Keyword> getKeywordByType(String type){
        List<Keyword> klist = new ArrayList<Keyword>();
        String sql = "select * from `keyword` where k_type='" + type +  "'";
        ResultSet rs = db.select(sql);
        try {
            while(rs.next()){
                Keyword key = new Keyword();
                String name = rs.getString("k_name");
                key.setK_name(name);
                klist.add(key);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return klist;
    }

    public static List<String> getDateListByStartEnd(String startTime, String endTime){

        List<String> timeList = new ArrayList<String>();

        String[] st = startTime.split("-");
        String[] et = endTime.split("-");

        Calendar start = Calendar.getInstance();
        start.set(Integer.parseInt(st[0]), Integer.parseInt(st[1]) - 1, Integer.parseInt(st[2]));
        Long stt = start.getTimeInMillis();
        //System.out.println(new Date(stt));

        Calendar end = Calendar.getInstance();
        end.set(Integer.parseInt(et[0]), Integer.parseInt(et[1]) - 1, Integer.parseInt(et[2]));
        Long ett = end.getTimeInMillis();
        //System.out.println(new Date(ett));

        Long oneDay = 1000 * 60 * 60 * 24l;

        Long time = stt;
        while (time <= ett) {
            Date d = new Date(time);
            timeList.add(sdf.format(d));
            time += oneDay;
        }
        return timeList;
    }
}