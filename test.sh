mkdir tests

curl -i -H "Content-Type: application/json" -X POST -d '{"swaggerURL":"http://169.44.118.61:8080/TomcatWebAppForChefNode/swagger.json"}' http://169.44.116.214:9080/SwaggerParser/api/swaggerTestService | sed 's/\r//' > curl.out
printf "\n" >> curl.out

print_real_lines() {
  set -f;
  seen=false;
  while read i; do
    if [ "$i" = "" ]; then 
      seen=true; 
    else if $seen; then echo $i; fi; 
    fi; 
  done;
}

print_real_lines < curl.out > tests/TEST-swagger.xml

echo TEST-swagger.xml begins here:
cat tests/TEST-swagger.xml
echo And TEST-swagger.xml ends here:

MY_EXIT_CODE=$(grep teststatus curl.out | cut -d':' -f2)
echo My exit code is $MY_EXIT_CODE

echo This is the last line.
exit $MY_EXIT_CODE