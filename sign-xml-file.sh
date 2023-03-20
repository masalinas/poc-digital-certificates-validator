#!/bin/bash

helpFunction()
{
   echo ""
   echo "Usage: $0 -f /Users/miguel/temp/employeesalary.xml -d /Users/miguel/temp/xml-signed.xml"
   echo -e "\t-f XML File to be sign"
   echo -e "\t-d Destination XML signed file"
   exit 1 # Exit script after printing help
}

while getopts "f:d:" opt
do
   case "$opt" in
      f ) file="$OPTARG" ;;
      d ) destination="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Begin script in case all parameters are correct
echo "$file"
echo "$destination"

# Print helpFunction in case parameters are empty
if [ -z "$file" ] || [ -z "$destination" ]
then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

mvn spring-boot:run -Dspring-boot.run.arguments="--action=sign --file=$file --destination=$destination"