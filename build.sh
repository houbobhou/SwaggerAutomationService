mvn -B package

echo ; echo Preparing .ssh
mkdir -p ~/.ssh

echo ; echo Copying the keys to the required place and setting permissions
cp ./id_rsa* ~/.ssh
chmod 0700 ~/.ssh/id_rsa*

$IC_COMMAND ps
echo $MY_CONTAINER_NAME is the passed container name
CONTAINER_ID=$($IC_COMMAND ps | grep $MY_CONTAINER_NAME | cut -d ' ' -f1)
echo $CONTAINER_ID is the container ID
echo $IC_COMMAND is the command to call for ice
NODE_IP=$($IC_COMMAND inspect --format '{{ .NetworkSettings.PublicIpAddress }}' $CONTAINER_ID)
echo $NODE_IP is the IP of the node container

echo ; echo Attempting to SCP
scp -o StrictHostKeyChecking=no target/TomcatWebAppForChefNode.war vravish@$NODE_IP:~/apache-tomcat-8.0.36/webapps
