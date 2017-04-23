package phantomjsdemo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by zhaokangpan on 2017/4/13.
 */
public class PhantomjsTest {

    public static String base_path = "/Users/zhaokangpan/Desktop/phantomjs/";

    public static String getAjaxCotnent(String url) throws IOException {
        Runtime rt = Runtime.getRuntime();

        Process p = rt.exec(base_path + "bin/phantomjs " + base_path + "bin/codes.js "+url);//这里我的codes.js是保存在c盘下面的phantomjs目录
        InputStream is = p.getInputStream();
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        StringBuffer sbf = new StringBuffer();
        String tmp = "";
        while((tmp = br.readLine())!=null){
            sbf.append(tmp);
        }
        System.out.println(sbf.toString());
        return sbf.toString();
    }

    public static void main(String[] args) throws IOException {
        getAjaxCotnent("http://wenshu.court.gov.cn/List/List?sorttype=1&conditions=searchWord+2+AJLX++%E6%A1%88%E4%BB%B6%E7%B1%BB%E5%9E%8B:%E6%B0%91%E4%BA%8B%E6%A1%88%E4%BB%B6");
    }
}
