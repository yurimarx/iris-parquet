package parquet.resultset;

import java.io.File;
import java.io.FileOutputStream;
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

public class IrisParquetService {

	public Boolean sqlToParquet(String parquetSchemaName, String parquetNamespace, String jdbcUrl, String irisUsername, String irisPassword, String sql, String parquetFilePath, String hadoopPath) {
		
		Boolean result = true;
		
		Connection connection = null;
		
		try {
			Class.forName("com.intersystems.jdbc.IRISDriver");
	        connection = DriverManager.getConnection(jdbcUrl, irisUsername, irisPassword);
	        
	        System.setProperty("hadoop.home.dir", hadoopPath);

	        Statement statement = connection.createStatement();
	        ResultSet resultSet = statement.executeQuery(sql);
	        
	        List<TransformerListener> listeners = new ArrayList<>();
	        
	        ResultSetTransformer transformer = new ResultSetParquetTransformer();

	        InputStream inputStream = transformer.transform(resultSet, parquetSchemaName, parquetNamespace, listeners);

	        File testOutput = new File(parquetFilePath);

	        IOUtils.copy(inputStream, new FileOutputStream(testOutput));

	        resultSet.close();
	        statement.close();
	        connection.close();

		} catch (Exception e) {
			e.printStackTrace();
			result = false;
		} finally {
			if (connection != null) {
				try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return result;
	}
}
