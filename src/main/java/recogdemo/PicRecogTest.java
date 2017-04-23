package recogdemo;

import com.asprise.ocr.Ocr;

import java.io.File;

/**
 * Created by zhaokangpan on 2017/4/1.
 */
public class PicRecogTest {
    public static void main(String[] args){
        Ocr.setUp(); // one time setup
        Ocr ocr = new Ocr(); // create a new OCR engine
        ocr.startEngine("eng", Ocr.SPEED_FASTEST); // English
        String s = ocr.recognize(new File[] {new File("ocr-result-highlighted.pdf")},
                Ocr.RECOGNIZE_TYPE_ALL, Ocr.OUTPUT_FORMAT_PLAINTEXT); // PLAINTEXT | XML | PDF | RTF
        System.out.println("Result: " + s);
        ocr.stopEngine();
    }
}
