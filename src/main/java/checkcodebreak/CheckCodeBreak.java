package checkcodebreak;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import spider.SpiderUtil;

import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * Created by zhaokangpan on 2017/4/13.
 */
public class CheckCodeBreak {

    public void testSelenium() throws IOException{
        //System.getProperties().setProperty("phantomjs.binary.path", "/Users/zhaokangpan/Desktop/phantomjs/bin/phantomjs");
        //webDriver.navigate().to(url);
        //HtmlUnitDriver webDriver = new HtmlUnitDriver(true);
        //webDriver.get("");
        //WebElement webElement = webDriver.findElement(By.className("suggest_body"));
        //WebElement webElement = webDriver.findElement(By.xpath("/html"));
        //System.out.println(webElement.getAttribute("outerHTML"));
    }

    public static WebDriver startChromeDriver(){
        System.getProperties().setProperty("webdriver.chrome.bin", "/Users/zhaokangpan/Application/Google Chrome.app");
        System.getProperties().setProperty("webdriver.chrome.driver", "/Users/zhaokangpan/IDEA/PicRecognition/chromedriver 2");
        WebDriver webDriver = new ChromeDriver();
        return webDriver;
    }

    // 裁剪图片
    private static void cutImage() throws IOException {
        String type = "png";
        // 裁剪的位置
        int x = 1045;
        int y = 280;
        int width = 108;
        int height = 58;

        // 文件地址
        File file = new File("screenshot.png");
        InputStream input = new FileInputStream(file);
        ImageInputStream imageStream = null;
        try {
            // 图片类型 默认 jpg
            String imageType = (null == type || "".equals(type)) ? "png" : type;
            // 将图片转化为 imageReader
            Iterator<ImageReader> readers = ImageIO.getImageReadersByFormatName(imageType);
            ImageReader reader = readers.next();
            // 读入图片
            imageStream = ImageIO.createImageInputStream(input);
            reader.setInput(imageStream, true);
            // 参数
            ImageReadParam param = reader.getDefaultReadParam();
            // 图片裁剪范围
            Rectangle rect = new Rectangle(x, y, width, height);
            param.setSourceRegion(rect);
            // 裁剪出图片
            BufferedImage bi = reader.read(0, param);
            // 输出达到文件夹
            ImageIO.write(bi, imageType, new File("after.png"));
        } catch (Exception e) {

        } finally {
            // 关闭stream
            imageStream.close();
        }
    }

    public static String preciseRecognition(String url, WebDriver webDriver) throws IOException, InterruptedException{//ensure precisely recognition
        String result = "";
        while(true){
            webDriver.navigate().to(url);
            File srcFile = ((TakesScreenshot)webDriver).getScreenshotAs(OutputType.FILE);
            //利用FileUtils工具类的copyFile()方法保存getScreenshotAs()返回的文件对象。
            FileUtils.copyFile(srcFile, new File("screenshot.png"));
            cutImage();
            result = SpiderUtil.recogPic("after.png");
            if(!result.contains("*")) break;
            Thread.sleep(10000);
        }
        return result;
    }

    public static void imitateSubmit(WebDriver webDriver, String result){
        //找到文本框
        WebElement element = webDriver.findElement(By.id("txtValidateCode"));
        //搜索关键字
        element.sendKeys(result);
        //提交表单 webDriver会自动从表单中查找提交按钮并提交
        WebElement submit = webDriver.findElement(By.id("btnLogin"));
        submit.click();
        //检查页面title
        System.out.println("页面Title："+webDriver.getTitle());
    }

    public static void checkBreak() throws IOException, InterruptedException {
        String url = "http://wenshu.court.gov.cn/Html_Pages/VisitRemind.html";

        WebDriver webDriver = startChromeDriver();

        String result = preciseRecognition(url, webDriver);

        imitateSubmit(webDriver, result);

        webDriver.quit();
    }


    public static void main(String[] args) throws IOException{
        CheckCodeBreak selenium = new CheckCodeBreak();
        selenium.testSelenium();
    }
}
