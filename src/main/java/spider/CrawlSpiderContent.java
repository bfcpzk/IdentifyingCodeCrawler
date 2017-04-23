package spider;

import model.Case;
import util.Jdbc_Util;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by zhaokangpan on 2017/4/16.
 */
public class CrawlSpiderContent {

    static Jdbc_Util db = new Jdbc_Util("127.0.0.1");//mysql控制对象

    public static void main(String[] args){
        int startId = Integer.parseInt(args[0]);//start Id
        int period = Integer.parseInt(args[1]);//period for every iter
        for(int iter = 0 ; iter < 10; iter++){//iter times
            List<Case> cList = getIdList(startId, period);
            for(int i = 0 ; i < cList.size() ; i++){//each case list
                int flag = SpiderUtil.singleProcess(cList.get(i), db);
                if(flag > 0) SpiderUtil.singleProcess(cList.get(i), db);
            }
        }
    }

    public static List<Case> getIdList(int startId, int period){
        List<Case> cList = new ArrayList<Case>();
        String sql = "select id, case_id from `wenshu` where id>='" + startId + "' and id<=" + period;
        ResultSet rs = db.select(sql);
        try {
            while(rs.next()){
                int id = rs.getInt("id");
                String case_id = rs.getString("case_id");
                Case ca = new Case();
                ca.setCase_id(case_id);
                ca.setId(id);
                cList.add(ca);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cList;
    }
}
