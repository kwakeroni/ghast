package example.generate;

import org.apache.commons.collections15.Predicate;
import org.apache.commons.collections15.Transformer;
import org.apache.commons.collections15.iterators.TransformIterator;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.commons.collections15.iterators.FilterIterator;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

/**
 * @author Maarten Van Puymbroeck
 */
public class MappingExcel implements Iterable<MappingSpec>, Closeable {

    Workbook workbook;
    Sheet sheet;
    MappingSheet sheetSpec;

    public MappingExcel(File file, MappingSheet sheetSpec) throws IOException, InvalidFormatException {
        this.sheetSpec = sheetSpec;
        Workbook wb =  WorkbookFactory.create(file);
        this.sheet = wb.getSheet(sheetSpec.sheetName);
    }


    @Override
    public Iterator<MappingSpec> iterator() {
        return new TransformIterator<Row, MappingSpec>(new FilterIterator<Row>(sheet.rowIterator(), rowFilter(sheetSpec)), mappingTransformer(sheetSpec));
    }

    @Override
    public void close() throws IOException {
        this.sheet = null;
        this.workbook = null;
    }

    public static class MappingSheet {
        public final String sheetName;
        public final String sourcePkg;
        public final String targetPkg;
        public final int sourceColumn;
        public final int targetColumn;
        public final int startRow;
        public final int sourceSkip;
        public final int targetSkip;


        public MappingSheet(String name, String sourcePkg, String targetPkg, int sourceColumn, int targetColumn, int startRow, int sourceSkip, int targetSkip){
            this.sheetName = name;
            this.sourcePkg = sourcePkg;
            this.targetPkg = targetPkg;
            this.sourceColumn = sourceColumn;
            this.targetColumn = targetColumn;
            this.startRow = startRow;
            this.sourceSkip = sourceSkip;
            this.targetSkip = targetSkip;
        }
    }

    private static Predicate<Row> rowFilter(final MappingSheet sheetSpec){
        return new Predicate<Row>(){
            @Override
            public boolean evaluate(Row row) {
                return row.getRowNum() >= sheetSpec.startRow
                        && (! isEmpty(row.getCell(sheetSpec.sourceColumn)))
                        && (! isEmpty(row.getCell(sheetSpec.targetColumn)));
            }

            private boolean isEmpty(Cell cell){
                if (cell == null){
                    return true;
                }
                String value = cell.getStringCellValue();
                return value == null || value.length() == 0 || value.trim().length() == 0;
            }
        };
    }

    private static Transformer<Row, MappingSpec> mappingTransformer(final MappingSheet sheetSpec){
        return new Transformer<Row, MappingSpec>(){
            @Override
            public MappingSpec transform(Row row) {
                return new MappingSpec(row.getCell(sheetSpec.sourceColumn).getStringCellValue(), sheetSpec.sourcePkg, sheetSpec.sourceSkip,
                                       row.getCell(sheetSpec.targetColumn).getStringCellValue(), sheetSpec.targetPkg, sheetSpec.targetSkip);
            }
        };
    }
}
