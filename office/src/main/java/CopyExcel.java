import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * @author lhldyf
 * @date 2019-10-08 23:22
 */
public class CopyExcel {
    public static void main(String[] args) throws IOException {
        FileInputStream excelFileInputStream = new FileInputStream("C:\\Users\\lhldy\\Desktop\\环境监测\\test.xlsx");
        Workbook workbook = new XSSFWorkbook(excelFileInputStream);
        excelFileInputStream.close();
        Sheet sheet = workbook.getSheetAt(0);
        //这样的构造方法可以直接拿到对应行和列下标
        CellAddress address = new CellAddress("K9");
        //得到行
        Row row = sheet.getRow(address.getRow());
        //得到列
        Cell cell = row.getCell(address.getColumn());
        //打印该数据到控制台
        System.out.println(cell.getStringCellValue());
        // 重新赋值
        cell.setCellValue(1);
        sheet.getRow(new CellAddress("K3").getRow()).getCell(new CellAddress("K3").getColumn()).setCellValue(2);
        sheet.getRow(new CellAddress("K4").getRow()).getCell(new CellAddress("K4").getColumn()).setCellValue(3);
        FileOutputStream excelFileOutPutStream = new FileOutputStream("C:\\Users\\lhldy\\Desktop\\环境监测\\test1.xlsx");
        workbook.setForceFormulaRecalculation(true);
        workbook.write(excelFileOutPutStream);
        excelFileOutPutStream.flush();
        excelFileOutPutStream.close();
        System.out.println("done");
    }
}
