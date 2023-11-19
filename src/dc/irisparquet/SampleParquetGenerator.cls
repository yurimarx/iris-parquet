Class dc.irisparquet.SampleParquetGenerator
{

ClassMethod CreateSampleParquet() As %Status
{

    Set result = ##class(dc.irisparquet.IrisParquet).SQLToParquet(
        "personSchema",
        "persons",
        "jdbc:IRIS://localhost:1972/IRISAPP", 
        "SELECT * FROM dc_irisparquet.SamplePerson", 
        "/tmp/sample.parquet"
    )
    
    write result  
    return result
}

}
