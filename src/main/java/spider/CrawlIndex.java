package spider;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import util.Jdbc_Util;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaokangpan on 2017/4/14.
 */
public class CrawlIndex {

    static JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
    static Jdbc_Util db = new Jdbc_Util("127.0.0.1");//mysql控制对象

    public static void main(String[] args) throws IOException {

        //param map
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("Param","案件类型:执行案件");

        Document keywordlist = Jsoup.connect("http://wenshu.court.gov.cn/List/TreeContent").data(paramMap)
                                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                                .ignoreContentType(true)
                                .timeout(50000)
                                .post();
        String result = keywordlist.text().substring(1, keywordlist.text().length() - 1).replaceAll("\\\\","");
        System.out.println(result);

        JsonArray jaa = jsonparer.parse(result).getAsJsonArray();
        JsonObject jo = jaa.get(0).getAsJsonObject();
        JsonArray ja = jo.get("Child").getAsJsonArray();
        System.out.println(ja.size());
        for(int i = 0 ; i < ja.size() ; i++){
            JsonObject tmp = ja.get(i).getAsJsonObject();
            String keyword = tmp.get("Key").getAsString();
            String value = tmp.get("Value").getAsString();
            String sql = "insert into `keyword` (`k_name`, `k_value`, `k_type`) values ('" + keyword + "', '" + value + "', '执行案件')";
            db.add(sql);
        }
    }
}
