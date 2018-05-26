package com.leiyu.distribute.core.zk;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;

import java.util.concurrent.TimeUnit;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.core.zk
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-05-26
 */
public class ZKClientDemo {

    public static void main(String[] args) throws InterruptedException {
        String zkServers = "127.0.0.1:2181,127.0.0.1:2182,127.0.0.1:2183";
        int connectionTimeout = 3000;
        ZkClient zkClient = new ZkClient(zkServers,connectionTimeout);
        String path = "/zk-data";

        if(zkClient.exists(path)){
            System.out.println("this path has data,the data is " + zkClient.readData(path));
            zkClient.delete(path);
        }

        zkClient.createPersistent(path,"test_data1");

        final String data = zkClient.<String>readData(path);
        System.out.println(data);

        zkClient.subscribeDataChanges(path, new IZkDataListener() {
            public void handleDataChange(String s, Object o) throws Exception {
                System.out.println("data has changed,datapath:" + s +",data:" + data);
            }

            public void handleDataDeleted(String s) throws Exception {
                System.out.println("data has deleted,datapath:" + s);
            }
        });

        TimeUnit.SECONDS.sleep(5l);
        zkClient.writeData(path, "test_data_2");

        TimeUnit.SECONDS.sleep(5l);
        zkClient.delete(path);

        TimeUnit.SECONDS.sleep(100000l);
    }
}
