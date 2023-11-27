package parquet.resultset;

import java.io.IOException;
import java.sql.SQLException;

public class TestRead {
	
	protected static final String TEMP_FILE_NAME = "c:\\temp\\teste.parquet";
	protected static final String JSON_TEMP_FILE_NAME = "c:\\temp\\teste.json";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		
		IrisParquetService service = new IrisParquetService();
		service.parquetToJSON(TEMP_FILE_NAME, JSON_TEMP_FILE_NAME);
	}
	
	
}
