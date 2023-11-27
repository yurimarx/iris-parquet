package parquet.resultset;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;

import parquet.resultset.impl.ResultSetParquetTransformer;

public class TestWrite {
	
	protected static final String SCHEMA_NAME = "SchemaName";
    protected static final String NAMESPACE = "org.NAMESPACE";
    protected static final String TEMP_FILE_NAME = "c:\\temp\\teste.parquet";
	
	public static void main(String[] args) throws ClassNotFoundException, SQLException, IOException {
		String url = "jdbc:IRIS://127.0.0.1:51773/USER";

        Class.forName("com.intersystems.jdbc.IRISDriver");
        Connection connection = DriverManager.getConnection(url,"_SYSTEM","SYS");
        // Replace _SYSTEM and SYS with a username and password on your system

        String query = "SELECT \r\n"
        		+ "ID, Company, DOB, Name, Phone, Title\r\n"
        		+ "FROM dc_Sample.Person";
        
        System.setProperty("hadoop.home.dir", "C:\\Apps\\hadoop-3.3.6");

        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(query);
        
        List<TransformerListener> listeners = new ArrayList<>();
        
        ResultSetTransformer transformer = new ResultSetParquetTransformer();

        InputStream inputStream = transformer.transform(resultSet, SCHEMA_NAME, NAMESPACE, listeners);

        File testOutput = new File(TEMP_FILE_NAME);

        IOUtils.copy(inputStream, new FileOutputStream(testOutput));
        
        
        
        resultSet.close();
        statement.close();
        connection.close();
	}
	
	
}
