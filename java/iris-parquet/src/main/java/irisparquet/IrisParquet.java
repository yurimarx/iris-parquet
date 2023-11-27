package irisparquet;

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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import parquet.resultset.IrisParquetService;
import parquet.resultset.ResultSetTransformer;
import parquet.resultset.TransformerListener;
import parquet.resultset.impl.ResultSetParquetTransformer;

public class IrisParquet {

	public Boolean sqlToParquet(
			String parquetSchemaName,
			String parquetNamespace,
			String jdbcUrl, 
			String irisUser, 
			String irisPassword, 
			String sql,
			String parquetFilePath) {
		
		Boolean result = true;
		
		Connection connection = null;
		
        try {
        	Logger.getRootLogger().setLevel(Level.OFF); 
        	Class.forName("com.intersystems.jdbc.IRISDriver");
            connection = DriverManager.getConnection(jdbcUrl, irisUser, irisPassword);
       
            String query = sql;
            
            //System.setProperty("hadoop.home.dir", "C:\\Apps\\hadoop-3.3.6");

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            
            List<TransformerListener> listeners = new ArrayList<>();
            
            ResultSetTransformer transformer = new ResultSetParquetTransformer();

            InputStream inputStream = transformer.transform(resultSet, parquetSchemaName, parquetNamespace, listeners);

            File testOutput = new File(parquetFilePath);
            
            FileOutputStream outStream = new FileOutputStream(testOutput);

            IOUtils.copy(inputStream, outStream);
            
            outStream.close();
            resultSet.close();
            statement.close();
            
        } catch(Exception e) {
        	result = false;
        	e.printStackTrace();
        } finally {
        	if(connection != null) {
        		try {
					connection.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
        	}
        }
		        
        return result;
	}
	
	public Boolean parquetToJSON(String parquetFilePath, String jsonFilePath) {
		Logger.getRootLogger().setLevel(Level.OFF); 
		return new IrisParquetService().parquetToJSON(parquetFilePath, jsonFilePath);
	}
}
