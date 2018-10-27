[![Build Status](https://travis-ci.org/gzheyts/giant-cli-integration-demo.svg?branch=master)](https://travis-ci.org/gzheyts/giant-cli-integration-demo)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)
# giant-cli-integration-demo
## Overview
Spring Boot application wich integrates with Giant-cli tool [v1.2.2.1]  and every 5 minutes reloads page and shows  actual values for last block number in blockchain and its complexity

Available API:
1. giant-cli getblockcount - last block number
2. giant-cli getblockhash :height - block hash by number
3. giant-cli getblock :hash - get block info by hash

## Preparations for running Giant client and daemon

### Install  dependencies (if not found)

* boost-libs [v1.58]

```bash
$ mkdir -p ~/tmp/boost
$ wget http://sourceforge.net/projects/boost/files/boost/1.58.0/boost_1_58_0.tar.gz -P ~/tmp/boost
$ cd ~/tmp/boost
$ tar -xf boost_1_58_0.tar.gz; cd boost_1_58_0
$ ./bootstrap.sh --with-python-version=2.7 
$ sudo ./b2 install 
$ rm -rf ~/tmp 
$ # update shared libraries configuration
$ sudo echo "/usr/local/lib" > /etc/ld.so.conf.d/giant.conf # boost-libs
$ sudo ldconfig
```
* miniupnpc 1.0 

```bash
$ sudo ln -s /usr/lib/libminiupnpc.so.17 /usr/lib/libminiupnpc.so.10
```
Other dependecies may be installed from official repo or downgraded 

### Setup credentials
```bash 
$ mkdir ~/.giant -p
$ echo -e "rpcuser=user\nrpcpassword=password" > ~/.giant/giant.conf
```

## Run Giant client and daemon
```bash
$ wget https://github.com/GiantPay/GiantCore/releases/download/1.2.2.1/giant-1.2.2.1-linux64.zip -P /tmp
$ cd /tmp; unzip giant-1.2.2.1-linux64.zip -d giant-1.2.2.1-linux64; cd giant-1.2.2.1-linux64
$ # test all dependencies provided
$ ./giantd -testnet & 2>/dev/null # daemon
$ ./giant-cli                     # client
$ # test all works good - retrieve last block number and complexity in blockchain
$ ./giant-cli -testnet getblock  \
        $(./giant-cli -testnet  getblockhash \
        $(./giant-cli -testnet  getblockcount)) \
        |  jq '. | {number: .height, complexity: .difficulty}'
```
```json
{
  "number": 0,
  "complexity": 0.00024414
}  
```

## Run integration demo
```bash
$ git -C /tmp clone https://github.com/gzheyts/giant-cli-integration-demo
$ mvn -f /tmp/giant-cli-integration-demo/pom.xml clean test spring-boot:run 
```
Then navigate to [localhost:8080](http://localhost:8080)

## Copyright and license

The code is released under the *MIT license*

[v1.58]:https://www.boost.org/users/history/version_1_58_0.html
[v1.2.2.1]:https://github.com/GiantPay/GiantCore/releases/tag/1.2.2.1
