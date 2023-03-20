# Description
PoC XML File Digital Sign and Validator Tool

## Sign a xml file
A sample to sign a xml file, execute:

```
./sign-xml-file.sh -f /Users/miguel/temp/employeesalary.xml -d /Users/miguel/temp/xml-signed.xml
 ```
 
 **where**: 
 * -f: the xml file to be signed
 * -d: destination xml signed file 
 
## Validate a xml file
A sample to validate a xml file, execute:

```
./validate-xml-file.sh -f /Users/miguel/temp/xml-signed.xml
```
 **where**: 
 * -f: the xml signed file to be validated
