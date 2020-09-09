import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

/**
 * 将多个Excel文件进行合并
 * @author lhldyf
 * @date 2020-04-10 22:33
 */
public class MergeExcel {
    static final String FILE_TYPE = ".xlsx";
    static String sourceDir = "E:\\var\\test";2

    public static void main(String[] args) {
        FileFilter fileFilter = pathname -> pathname.getName().endsWith(FILE_TYPE);
        List<File> files = FileUtil.loopFiles(sourceDir, fileFilter);
    }
}
