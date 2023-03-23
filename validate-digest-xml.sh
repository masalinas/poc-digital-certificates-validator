#!/bin/bash

helpFunction()
{
   echo ""
   echo "Usage: $0 -f /Users/miguel/temp/xml-signed.xml"
   echo -e "\t-f XML signed file to be validated"
   exit 1 # Exit script after printing help
}

while getopts "f:" opt
do
   case "$opt" in
      f ) file="$OPTARG" ;;
      ? ) helpFunction ;; # Print helpFunction in case parameter is non-existent
   esac
done

# Begin script in case all parameters are correct
echo "$file"

# Print helpFunction in case parameters are empty
if [ -z "$file" ]
then
   echo "Some or all of the parameters are empty";
   helpFunction
fi

# Execute the tool
mvn spring-boot:run -Dspring-boot.run.arguments="--action=validate --file=$file"
