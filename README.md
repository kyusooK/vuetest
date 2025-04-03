# 

## Model
www.msaez.io/#/123912988/storming/vuetest

## frontend
How to install npm
npm install -g npm@9

# How to install Node.js
### 1. Visit the official Node.js website: https://nodejs.org/
### 2. Download the installer for your operating system.
### 3. Run the installer and follow the instructions to complete the installation.
### 4. To install a specific version of Node.js, you can use the following command:
###    nvm install 20.17.0
###    (Make sure you have nvm installed. If not, follow the instructions at https://github.com/nvm-sh/nvm)
### 5. Once installed, you can verify the installation by running the following commands in your terminal:
###    node -v
###    npm -v

node -v : 20.17.0
npm -v : 9.9.4


```
cd front
npm install
npm run dev
```


## Before Running Services
### Make sure there is a Kafka server running
```
cd infra
docker-compose up
```
- Check the Kafka messages:
```
cd infra
docker-compose exec -it kafka /bin/bash
cd /bin
./kafka-console-consumer --bootstrap-server localhost:9092 --topic
```

## Run the backend micro-services
See the README.md files inside the each microservices directory:

- order
- inventory
- customercenter


## Run API Gateway (Spring Gateway)
```
cd gateway
mvn spring-boot:run
```

## Test by API
- order
```
 http :8088/orders id="id"qty="qty"userId="userId"orderDate="orderDate"InventoryId := '{"id": 0}'Address := '{"street": street, "city": city, "state": state, "country": country, "zipcode": zipcode}'
```
- inventory
```
 http :8088/inventories id="id"productName="productName"qty="qty"Photo := '{"imgName": imgName, "imgFile": imgFile}'
```
- customercenter
```
```


## Run the frontend
```
cd frontend
npm i
npm run serve
```

## Test by UI
Open a browser to localhost:8088

## Required Utilities

- httpie (alternative for curl / POSTMAN) and network utils
```
sudo apt-get update
sudo apt-get install net-tools
sudo apt install iputils-ping
pip install httpie
```

- kubernetes utilities (kubectl)
```
curl -LO "https://dl.k8s.io/release/$(curl -L -s https://dl.k8s.io/release/stable.txt)/bin/linux/amd64/kubectl"
sudo install -o root -g root -m 0755 kubectl /usr/local/bin/kubectl
```

- aws cli (aws)
```
curl "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
unzip awscliv2.zip
sudo ./aws/install
```

- eksctl 
```
curl --silent --location "https://github.com/weaveworks/eksctl/releases/latest/download/eksctl_$(uname -s)_amd64.tar.gz" | tar xz -C /tmp
sudo mv /tmp/eksctl /usr/local/bin
```
