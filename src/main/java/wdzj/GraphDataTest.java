package wdzj;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaokangpan on 2017/4/27.
 */
public class GraphDataTest {

    public static void main(String[] args) throws IOException{
        String url = "http://shuju.wdzj.com/plat-info-target.html";
        Map<String, String> paramMap = new HashMap<String, String>();
        paramMap.put("wdzjPlatId","85");
        paramMap.put("type","1");
        paramMap.put("target1","7");
        paramMap.put("target2","8");
        Document doc = Jsoup.connect(url).data(paramMap)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .ignoreContentType(true)
                .timeout(50000)
                .post();
        System.out.println(doc.text());
    }
}
