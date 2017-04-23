package recogdemo;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;

/**
 * Created by zhaokangpan on 2017/4/14.
 */
public class SnapShot {
    public static void main(String[] args) throws IOException, URISyntaxException, AWTException{
        Desktop.getDesktop().browse(new URL("http://wenshu.court.gov.cn/Html_Pages/VisitRemind.html").toURI());
        Robot robot = new Robot();
        robot.delay(10000);
        Dimension d = new Dimension(Toolkit.getDefaultToolkit().getScreenSize());
        int width = (int) d.getWidth();
        int height = (int) d.getHeight();
        //最大化浏览器
        robot.keyRelease(KeyEvent.VK_F11);
        robot.delay(2000);
        Image image = robot.createScreenCapture(new Rectangle(651, 234, 715,
                263));
        BufferedImage bi = new BufferedImage(120, 100,
                BufferedImage.TYPE_INT_RGB);
        Graphics g = bi.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        //保存图片
        ImageIO.write(bi, "jpg", new File("baidu.jpg"));
    }
}
