package parquet.resultset.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.generic.GenericRecord;
import org.apache.avro.generic.GenericRecordBuilder;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.LocalFileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.parquet.avro.AvroParquetWriter;
import org.apache.parquet.hadoop.ParquetWriter;
import org.apache.parquet.hadoop.metadata.CompressionCodecName;

import parquet.resultset.ResultSetSchemaGenerator;
import parquet.resultset.ResultSetTransformer;
import parquet.resultset.SchemaResults;
import parquet.resultset.SchemaSqlMapping;
import parquet.resultset.TransformerListener;

public class ResultSetParquetTransformer implements ResultSetTransformer {

    public InputStream transform(ResultSet resultSet, String schemaName, String namespace,
                                 List<TransformerListener> listeners) throws IOException, SQLException {

        SchemaResults schemaResults = new ResultSetSchemaGenerator().generateSchema(resultSet,
                schemaName, namespace);

        listeners.forEach(transformerListener -> transformerListener.onSchemaParsed(schemaResults));

        java.nio.file.Path tempFile = Files.createTempFile(null, null);
        Path outputPath = new Path(tempFile.toUri());

        final LocalFileSystem localFileSystem = FileSystem.getLocal(new Configuration());

        File file = localFileSystem.pathToFile(outputPath);
        file.delete();

        
        ParquetWriter parquetWriter = AvroParquetWriter.builder(outputPath)
                .withSchema(schemaResults.getParsedSchema())
                .withCompressionCodec(CompressionCodecName.SNAPPY)
                .build();
        
        List<GenericRecord> records = new ArrayList<>();

        while (resultSet.next()) {

            GenericRecordBuilder builder = new GenericRecordBuilder(schemaResults.getParsedSchema());

            for (SchemaSqlMapping mapping : schemaResults.getMappings()) {

                builder.set(
                        schemaResults.getParsedSchema().getField(mapping.getSchemaName()),
                        ResultSetTransformer.extractResult(mapping, resultSet));
            }

            GenericRecord record = builder.build();

            records.add(record);

            listeners.forEach(transformerListener -> transformerListener.onRecordParsed(record));
        }

        for (GenericRecord record : records) {
            parquetWriter.write(record);
        }

        parquetWriter.close();

        File outputFile = localFileSystem.pathToFile(outputPath);

        return new FileInputStream(outputFile);

    }


}
