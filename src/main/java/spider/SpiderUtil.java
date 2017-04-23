package spider;

import com.asprise.ocr.Ocr;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import model.Case;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import checkcodebreak.CheckCodeBreak;
import util.Jdbc_Util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created by zhaokangpan on 2017/4/1.
 */
public class SpiderUtil {

    static JsonParser jsonparer = new JsonParser();// 初始化解析json格式的对象
    //static Jdbc_Util db = new Jdbc_Util("127.0.0.1");//mysql控制对象

    static Ocr ocr = new Ocr(); // create a new OCR engine

    public static void main(String[] args) throws IOException{


       /* Connection conn = Jsoup.connect("http://wenshu.court.gov.cn/User/ValidateCode");
        conn.method(Connection.Method.GET);
        conn.followRedirects(false);
        Connection.Response response = conn.execute();
        System.out.println(response.cookies());*/

        /*String cookie = "FSSBBIl1UgzbN7N80S=9kiWsO1MTc6AssGXVFXdcNIM.TbVOFVw8IJDVWIbH.2aeonOz8MjwF5VElszGteo; ASP.NET_SessionId=wxrevqnxjwum3lyzvieesz2u; Hm_lvt_3f1a54c5a86d62407544d433f6418ef5=1491754896; Hm_lpvt_3f1a54c5a86d62407544d433f6418ef5=1491784974; _gscu_2116842793=91754896rgtomm86; _gscbrs_2116842793=1; FSSBBIl1UgzbN7N80T=1LXyxMH3C49UjApcgzTQG_99aQDljW1XcGAxW1.RajPGA6dtai092SshJAU7kBSKSltrn7LzsvuGpt6gJzTOyHCntQsFJHIKNQ91kx24p_dtwKovLCmgAY6jBR_xiA.zjPi1eBQhj.7yrCsKPgVLHVzMjKB2nmQY_7baDo.LrcUAy.n6eS.Qc4VkWlnqh.61LewF25.humJ6RwJ3dsbcuT56O0_TJLwKP92NRuxack1qFD2OlJBVwDGEMRGFAKX0Fgi8ykcr75.vREN645_QNNEkPOPW.wNmXnPcMMVbr5Wvmy3GD1WEvjojGss2LgZwnza";

        List<String> url = new ArrayList<String>();
        url.add("http://wenshu.court.gov.cn/User/ValidateCode");
        download(url);
        String checkCode = recogPic("ValidateCode.jpg");
        Map<String, String> checkCodeMap = new HashMap<String, String>();
        checkCodeMap.put("ValidateCode", checkCode);
        Document docMain = Jsoup.connect("http://wenshu.court.gov.cn/Content/CheckVisitCode").cookie("Cookie",cookie).data(checkCodeMap)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .ignoreContentType(true)
                .timeout(50000)
                .post();
        System.out.println(docMain.text());*/
    }


    public static int judgePageNumber(String urlMain, Map<String, String> paramMap){
        paramMap.put("Index", String.valueOf(1));
        int number = 0;
        try{
            String htmlContent = connectMain(urlMain, paramMap);
            htmlContent = htmlContent.substring(1, htmlContent.length() - 1).replaceAll("\\\\","");
            if(htmlContent.contains("Count")) {
                JsonArray ja = jsonparer.parse(htmlContent).getAsJsonArray();
                JsonObject jo = ja.get(0).getAsJsonObject();
                number = Integer.parseInt(jo.get("Count").getAsString());
                System.out.println("总条数: " + number);
                if(number != 0){//not empty
                    number = number/20 + 1;
                }
                Thread.sleep(10000);
            }else if(htmlContent.contains("[]")){
                number = 0;
            }else{//picture check
                System.out.println(htmlContent);
                CheckCodeBreak.checkBreak();
                Thread.sleep(10000);
            }
        }catch(Exception e){//read time out
            try{
                Thread.sleep(30000);
                judgePageNumber(urlMain, paramMap);
            }catch(Exception ee){
                ee.printStackTrace();
            }
        }
        return number;
    }

    public static int mainProcess(String urlMain, Map<String, String> paramMap, int page, String keyword, String case_wenshu_type, Jdbc_Util db){
        paramMap.put("Index", String.valueOf(page));
        try{
            String htmlContent = connectMain(urlMain, paramMap);
            if(htmlContent.contains("文书ID")) {//content is not empty e.g:[{\"Count\":\"124\"}("文书ID")]
                System.out.println(htmlContent.substring(1, htmlContent.length() - 1).replaceAll("\\\\",""));
                Thread.sleep(30000);
                parsePage(htmlContent, page, keyword, case_wenshu_type, db);
            }else if(!htmlContent.contains("文书ID")){//content is empty e.g:[{\"Count\":\"124\"}]
                return 222;
            }else{//picture check
                System.out.println(htmlContent);
                CheckCodeBreak.checkBreak();
                Thread.sleep(30000);
                return page;
            }
        }catch(Exception e){//read time out
            try{
                Thread.sleep(30000);
                mainProcess(urlMain, paramMap, page, keyword, case_wenshu_type, db);
            }catch(Exception ee){
                ee.printStackTrace();
            }
        }
        return 0;
    }

    //link to first level
    public static String connectMain(String urlMain, Map<String, String> paramMap) throws IOException{
        Document docMain = Jsoup.connect(urlMain).data(paramMap)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .ignoreContentType(true)
                .timeout(50000)
                .post();
        return docMain.text();
    }

    //parse the index page
    public static void parsePage(String htmlContent, int page, String keyword, String case_wenshu_type, Jdbc_Util db) throws InterruptedException{
        JsonArray caseList = jsonparer.parse(htmlContent.substring(1, htmlContent.length() - 1).replaceAll("\\\\", "")).getAsJsonArray();
        for (int index = 1; index < caseList.size(); index++) {
            Case ca = new Case();
            JsonObject tmp = caseList.get(index).getAsJsonObject();
            if (tmp.has("文书ID")) ca.setCase_id(tmp.get("文书ID").getAsString());
            if (tmp.has("案件类型")) ca.setCase_type(tmp.get("案件类型").getAsString());
            if (tmp.has("裁判日期")) ca.setCase_verdict_date(tmp.get("裁判日期").getAsString());
            if (tmp.has("案件名称")) ca.setCase_name(tmp.get("案件名称").getAsString());
            if (tmp.has("审判程序")) ca.setCase_procedure(tmp.get("审判程序").getAsString());
            if (tmp.has("案号")) ca.setCase_no(tmp.get("案号").getAsString());
            if (tmp.has("法院名称")) ca.setCase_court_name(tmp.get("法院名称").getAsString());
            ca.setCase_keyword(keyword);
            ca.setCase_wenshu_type(case_wenshu_type);
            //Thread.sleep(30000);
            //ca = singleProcess(ca);
            insertIndex(ca, db);
            System.out.println("PageNo : " + page + "\tIndex : " + index + "\tCase_id : " + ca.getCase_id());
        }
    }

    //single page process
    public static int singleProcess(Case ca, Jdbc_Util db) {
        try{
            String url = "http://wenshu.court.gov.cn/CreateContentJS/CreateContentJS.aspx?DocID=" + ca.getCase_id();
            String htmlSingle = connectSingle(url);
            if(htmlSingle.contains("jsonHtmlData")){
                ca = parseSingle(ca, htmlSingle);
                insertContent(ca, db);
                System.out.println("id:" + ca.getId() + "    case_id:" + ca.getCase_id() + "    pub_date:" + ca.getCase_pub_date());
                Thread.sleep(30000);
            }else if(htmlSingle.contains("remind")){//picture check
                System.out.println(htmlSingle);
                CheckCodeBreak.checkBreak();
                Thread.sleep(30000);
                return ca.getId();
            }
        }catch(Exception e){//read time out
            try{
                Thread.sleep(30000);
                singleProcess(ca, db);
            }catch(InterruptedException ee){
                ee.printStackTrace();
            }
        }
        return 0;
    }

    //parse single page
    public static Case parseSingle(Case ca, String htmlSingle) throws IOException{
        String result;
        result = htmlSingle.split("jsonHtmlData = \"")[1].split("\"; var jsonData =")[0];
        result = result.replaceAll("\\\\","");
        JsonObject tmp = jsonparer.parse(result).getAsJsonObject();
        if(tmp.has("PubDate")) ca.setCase_pub_date(tmp.get("PubDate").getAsString());
        if(tmp.has("Html")) ca.setCase_content(tmp.get("Html").getAsString());
        return ca;
    }

    //connect to single case page
    public static String connectSingle(String url) throws IOException{
        Document doc = Jsoup.connect(url)
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_12_3) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/57.0.2987.133 Safari/537.36")
                .ignoreContentType(true)
                .timeout(50000)
                .post();
        return doc.text();
    }

    //insert data into database
    public static void insertIndex(Case ca, Jdbc_Util db){
        String sql = "insert into `wenshu` (`case_id`, `case_name`, `case_pub_date`, `case_content`, `case_type`, `case_no`, `case_court_name`, `case_procedure`, `case_verdict_date`, `case_keyword`, `case_wenshu_type`) value ('" + ca.getCase_id() + "', '" + ca.getCase_name() + "', '" + ca.getCase_pub_date() + "', '" + ca.getCase_content() + "', '" + ca.getCase_type() + "', '" + ca.getCase_no() + "', '" + ca.getCase_court_name() + "', '" + ca.getCase_procedure() + "', '" + ca.getCase_verdict_date() + "', '" + ca.getCase_keyword() + "', '" + ca.getCase_wenshu_type() + "')";
        db.add(sql);
    }

    public static void insertContent(Case ca, Jdbc_Util db){
        String sql = "insert into `case_content` (`id`, `case_id`, `case_pub_date`, `case_content`) value ('" + ca.getId() + "', '" + ca.getCase_id() + "', '" + ca.getCase_pub_date() + "', '" + ca.getCase_content() + "')";
        db.add(sql);
    }

    public static String recogPic(String name){
        Ocr.setUp(); // one time setup
        ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
        String s = ocr.recognize(new File[] {new File(name)},
                Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT); // PLAINTEXT | XML | PDF | RTF
        System.out.println("Result: " + s);
        ocr.stopEngine();
        return s;
    }

    public static void download(List<String> listImgSrc) {
        try {
            for (String url : listImgSrc) {
                String imageName = url.substring(url.lastIndexOf("/") + 1, url.length()) + ".jpg";
                URL uri = new URL(url);
                InputStream in = uri.openStream();
                FileOutputStream fo = new FileOutputStream(new File(imageName));
                byte[] buf = new byte[1024];
                int length = 0;
                System.out.println("开始下载:" + url);
                while ((length = in.read(buf, 0, buf.length)) != -1) {
                    fo.write(buf, 0, length);
                }
                in.close();
                fo.close();
                System.out.println(imageName + "下载完成");
            }
        } catch (Exception e) {
            System.out.println("下载失败");
        }
    }
}