Class dc.irisparquet.SamplePerson Extends (%Persistent, %Populate)
{

Property Name As %String(MAXLEN = "");

Property SSN As %String(MAXLEN = "");

Property Company As %String(MAXLEN = "");

Property Phone As %String(MAXLEN = "");

ClassMethod Populate10() As %Status
{
    Set sc = $$$OK
    Do ..Populate(10)
    Return sc
}

Storage Default
{
<Data name="SamplePersonDefaultData">
<Value name="1">
<Value>%%CLASSNAME</Value>
</Value>
<Value name="2">
<Value>Name</Value>
</Value>
<Value name="3">
<Value>SSN</Value>
</Value>
<Value name="4">
<Value>Company</Value>
</Value>
<Value name="5">
<Value>Phone</Value>
</Value>
</Data>
<DataLocation>^dc.sample.SamplePersonD</DataLocation>
<DefaultData>SamplePersonDefaultData</DefaultData>
<IdLocation>^dc.sample.SamplePersonD</IdLocation>
<IndexLocation>^dc.sample.SamplePersonI</IndexLocation>
<StreamLocation>^dc.sample.SamplePersonS</StreamLocation>
<Type>%Storage.Persistent</Type>
}

}
