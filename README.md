# Description
PoC XML File Digital Sign and Validator Tool

## Sign a xml file
A sample to sign a xml file, execute:

```
./sign-xml-file.sh -f /Users/miguel/temp/employeesalary.xml -d /Users/miguel/temp/employeesalary_signed.xml
 ```
 
 **where**: 
 * -f: the xml file to be signed
 * -d: destination xml signed file 
 
## Validate sign xml file
A sample to validate the sign xml file, execute:

```
./validate-sign-xml.sh -f /Users/miguel/temp/employeesalary_signed.xml
```
 **where**: 
 * -f: the xml signed file to be validated

## Validate digest xml file
A sample to validate the digest xml file, execute:

```
./validate-digest-xml.sh -f /Users/miguel/temp/employeesalary_signed.xml
```
 **where**: 
 * -f: the xml signed file to be validated