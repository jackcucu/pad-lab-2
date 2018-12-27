# pad-lab-2


The implementation of proxy is presented here. Working language is **Java**. 
This project has 3 components:
* proxy
* client
* node

See [protocol-specs](https://github.com/jackcucu/pad-lab-2/blob/master/docs/protocol-specs.md) for more info about client-broker communication.

### Build

In order to build you should have installed java 8 and maven.

In terminal type `mvn clean package` that will produce in `target/` folder 3 jars :
* proxy (`target/proxy-jar-with-dependencies.jar`)
* node (`target/node-jar-with-dependencies.jar`)
* client (`target/client-jar-with-dependencies.jar`)

 ### Run

You can run the following jars:

**Proxy**

Proxy get config data [from](https://github.com/jackcucu/pad-lab-2/blob/master/config.json) here.

Verify if **BIND_PORT**(`see constants below`) is not already in use then
`cd` in `target` folder and type `java -jar proxy-jar-with-dependencies.jar`

**Node**

Program is running if port that user indicates as arg0 contains in config file [from](https://github.com/jackcucu/pad-lab-2/blob/master/config.json) here, run jar with arguments:
arg0 : 
- portfa
Verify if **port** is not already in use then

`cd` in `target` folder and type `java -jar node-jar-with-dependencies.jar`

**Client**

Program is running if proxy is running on (`localhost:BIND_PORT(see constants below)`), run jar with arguments:
arg0 : 
- xml(for response content-type in xml format)
- json(for response content-type in json format)

`cd` in `target` folder and type `java -jar client-jar-with-dependencies.jar`

**Client** (`client-module/`);

**Constants**

These constants are used in project:
- BIND_PORT = 8787 *(Number of tcp port)*
- HOST = "127.0.0.1" *(host ip for clients)*





