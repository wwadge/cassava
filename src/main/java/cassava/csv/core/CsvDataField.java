package cassava.csv.core;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public class CsvDataField {
    private String headerName;
    private String fieldValue;
    private int fieldPosition;

    public CsvDataField(String headerName, String fieldValue, int fieldPosition) {
        this.headerName = headerName;
        this.fieldValue = fieldValue;
        this.fieldPosition = fieldPosition;
    }

    public String getHeaderName() {
        return headerName;
    }

    public void setHeaderName(String headerName) {
        this.headerName = headerName;
    }

    public String getFieldValue() {
        return fieldValue;
    }

    public void setFieldValue(String fieldValue) {
        this.fieldValue = fieldValue;
    }

    public int getFieldPosition() {
        return fieldPosition;
    }

    public void setFieldPosition(int fieldPosition) {
        this.fieldPosition = fieldPosition;
    }
}
