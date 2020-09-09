import org.apache.poi.hwpf.HWPFDocument;
import org.apache.poi.hwpf.usermodel.Range;

import java.io.*;

/**
 * @author lhldyf
 * @date 2019-10-08 23:22
 */
public class CopyWord {
    public static void main(String[] args) throws IOException {
        InputStream in = null;
        in = new FileInputStream("C:\\Users\\lhldy\\Desktop\\环境监测\\test.doc");

        HWPFDocument document = null;
        document = new HWPFDocument(in);
        // 读取文本内容
        Range bodyRange = document.getRange();
        System.out.println(bodyRange.toString());
        System.out.println(bodyRange.text());
        // 替换内容
        bodyRange.replaceText("${danwei}", "单位");
        bodyRange.replaceText("${dizhi}", "地址");

        //导出到文件
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            document.write((OutputStream) byteArrayOutputStream);
            OutputStream outputStream = new FileOutputStream("C:\\Users\\lhldy\\Desktop\\环境监测\\test1.doc");
            outputStream.write(byteArrayOutputStream.toByteArray());
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
