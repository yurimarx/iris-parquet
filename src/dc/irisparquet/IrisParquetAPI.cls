Class dc.irisparquet.IrisParquetAPI Extends %CSP.REST
{

Parameter CHARSET = "utf-8";

Parameter CONTENTTYPE = "text/plain";

Parameter HandleCorsRequest = 1;

XData UrlMap [ XMLNamespace = "http://www.intersystems.com/urlmap" ]
{
<Routes>
<!-- Swagger specs -->
<Route Url="/_spec" Method="GET" Call="SwaggerSpec" />
<!-- Generate random persons  -->
<Route Url="/generate-persons" Method="GET" Call="GeneratePersons" Cors="true"/>
<!-- Provide a SQL query instruction on body and get the results on a parquet file  -->
<Route Url="/sql2parquet" Method="POST" Call="SqlToParquet" Cors="true"/>
<!-- Provide a Parquet file get the json file  -->
<Route Url="/parquet2json" Method="POST" Call="ParquetToJSON" Cors="true"/>

</Routes>
}

ClassMethod ParquetToJSON() As %Status
{
  try {
        Set source = %request.GetMimeData("file")
        Set destination=##class(%Stream.FileBinary).%New()
        Set destination.Filename="/tmp/"_source.FileName
        Set tSC=destination.CopyFrom(source) //reader open the file
        Set result=destination.%Save()

        Set result = ##class(dc.irisparquet.IrisParquet).ParquetToJSON(
        "/tmp/"_source.FileName,
        "/tmp/content.json"
        )

        Set %response.ContentType = "application/json"
        Set %response.NoCharSetConvert=1
        Set %response.Headers("Access-Control-Allow-Origin")="*"
        
        Set stream=##class(%Stream.FileBinary).%New()
        Set tSC=stream.LinkToFile("/tmp/content.json")
        Do stream.OutputToDevice() 
 
        Set tSC=$$$OK
   
    //returns error message to the user
    } catch e {
        Set tSC=e.AsStatus()
        Set pOutput = tSC
    }
 
    Quit tSC
}

ClassMethod SqlToParquet() As %Status
{
    Set sc = $$$OK

    Set Sql=$ZCONVERT(%request.Content.Read(),"I","UTF8")

    Set result = ##class(dc.irisparquet.IrisParquet).SQLToParquet(
        "irisschema",
        "irisapp", 
        "jdbc:IRIS://localhost:1972/IRISAPP", 
        Sql, 
        "/tmp/irissample.parquet"
    )
        
    Set %response.ContentType = "application/octet-stream"
    Do %response.SetHeader("Content-Disposition","attachment;filename=""irissample.parquet""")
    Set %response.NoCharSetConvert=1
    Set %response.Headers("Access-Control-Allow-Origin")="*"
    
    Set stream=##class(%Stream.FileBinary).%New()
    Set tSC=stream.LinkToFile("/tmp/irissample.parquet")
    Do stream.OutputToDevice() 
    
    Set tSC=$$$OK
    
    Return tSC
}

/// Generate fake persons
ClassMethod GeneratePersons() As %Status
{
  Set sc = $$$OK
  Do ##class(dc.irisparquet.SamplePerson).Populate10()
  Return sc
}

ClassMethod SwaggerSpec() As %Status
{
  Set tSC = ##class(%REST.API).GetWebRESTApplication($NAMESPACE, %request.Application, .swagger)
  Do swagger.info.%Remove("x-ISC_Namespace")
  Set swagger.basePath = "/parquet-api"
  Set swagger.info.title = "InterSystems IRIS SQL to Parquet"
  Set swagger.info.version = "0.1"
  Set tSC = ##class(%RoutineMgr).GetWebServerPort(.Port,.Server,.URLPrefix,.URL)
  Return ..%ProcessResult($$$OK, swagger)
}

ClassMethod %ProcessResult(pStatus As %Status = {$$$OK}, pResult As %DynamicObject = "") As %Status [ Internal ]
{
  #dim %response As %CSP.Response
  SET tSC = $$$OK
  IF $$$ISERR(pStatus) {
    SET %response.Status = 500
    SET tSC = ..StatusToJSON(pStatus, .tJSON)
    IF $isobject(tJSON) {
      SET pResult = tJSON
    } ELSE {
      SET pResult = { "errors": [ { "error": "Unknown error parsing status code" } ] }
    }
  } 
  ELSEIF pStatus=1 {
    IF '$isobject(pResult){
      SET pResult = {
      }
    }
  }
  ELSE {
    SET %response.Status = pStatus
    SET error = $PIECE(pStatus, " ", 2, *)
    SET pResult = {
      "error": (error)
    }
  }
  
  IF pResult.%Extends("%Library.DynamicAbstractObject") {
    WRITE pResult.%ToJSON()
  }
  ELSEIF pResult.%Extends("%JSON.Adaptor") {
    DO pResult.%JSONExport()
  }
  ELSEIF pResult.%Extends("%Stream.Object") {
    DO pResult.OutputToDevice()
  }
  
  QUIT tSC
}

ClassMethod ReportHttpStatusCode(pHttpStatus, pSC As %Status = {$$$OK}) As %Status
{
  Set %response.Status=pHttpStatus
  
  If $$$ISERR(pSC) Do ..outputStatus(pSC)
      
  Return $$$OK
}

}
