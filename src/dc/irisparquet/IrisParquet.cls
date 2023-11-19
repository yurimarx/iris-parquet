Class dc.irisparquet.IrisParquet Extends %RegisteredObject
{

ClassMethod SQLToParquet(ParquetSchemaName As %String, ParquetNamespace As %String, JdbcURL As %String, Sql As %String, ParquetFilePath As %String) As %Status
{
    set javaGate = $system.external.getJavaGateway()  
    do javaGate.addToPath($SYSTEM.Util.BinaryDirectory()_"iris-parquet.jar")
    set irisparquet = javaGate.new("irisparquet.IrisParquet") 
    
    Set cred = ##class(Ens.Config.Credentials).%OpenId("IrisParquetCreds")
    Set username = cred.Username 
    Set password = cred.Password 

    If password = "" {
        set password = "SYS"
    }
    
    set result = irisparquet.sqlToParquet(
        ParquetSchemaName, 
        ParquetNamespace, 
        JdbcURL, 
        username,
        password,
        Sql,
        ParquetFilePath)
    
    return result
}

}
