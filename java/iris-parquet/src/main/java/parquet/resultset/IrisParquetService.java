package parquet.resultset;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericData;
import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetReader;
import org.apache.parquet.hadoop.ParquetReader;

import parquet.resultset.impl.ResultSetParquetTransformer;

public class IrisParquetService {

	public Boolean sqlToParquet(String parquetSchemaName, String parquetNamespace, String jdbcUrl, String irisUsername,
			String irisPassword, String sql, String parquetFilePath, String hadoopPath) {

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

	public Boolean parquetToJSON(String parquetFilePath, String jsonFilePath) {

		Boolean result = false;
		BufferedWriter jsonWriter = null;
		ParquetReader<GenericData.Record> reader = null;
		Path path = new Path(parquetFilePath);

		try {
			File file = new File(jsonFilePath);
	        file.createNewFile();
			
	        jsonWriter = new BufferedWriter(new FileWriter(jsonFilePath));

			jsonWriter.write("[");
			jsonWriter.newLine();
			reader = AvroParquetReader.<GenericData.Record>builder(path).withConf(new Configuration()).build();
			
			GenericData.Record record;
			
			if((record = reader.read()) != null) {
				jsonWriter.write(record.toString());
			}
			
			while ((record = reader.read()) != null) {
				jsonWriter.write(",");				
				jsonWriter.newLine();
				jsonWriter.write(record.toString());
			}
			
			jsonWriter.newLine();
			jsonWriter.write("]");
			
			result = true;
		
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (reader != null) {
				try {
					reader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if (jsonWriter != null) {
				try {
					jsonWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return result;
	}
}
