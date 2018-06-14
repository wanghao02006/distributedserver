package com.leiyu.distribute.common.nio2;

import java.io.IOException;
import java.nio.file.*;

/**
 * @Project: distributedserver
 * @Package Name: com.leiyu.distribute.common.nio2
 * @Description: TODO
 * @Author: wanghao30
 * @Creation Date: 2018-06-11
 */
public class WatchServiceDemo {

    public void watchDir(Path path) throws IOException,InterruptedException{
        try {
            WatchService watchService = FileSystems.getDefault().newWatchService();
            path.register(watchService, StandardWatchEventKinds.ENTRY_CREATE,StandardWatchEventKinds.ENTRY_MODIFY,StandardWatchEventKinds.ENTRY_DELETE);
            while (true){
                final WatchKey key = watchService.take();
                for(WatchEvent<?> watchEvent : key.pollEvents()){
                    final WatchEvent.Kind<?> kind = watchEvent.kind();
                    if(kind == StandardWatchEventKinds.OVERFLOW){
                        continue;
                    }

                    final WatchEvent<Path> watchEventPath = (WatchEvent<Path>) watchEvent;

                    final Path fileName = watchEventPath.context();
                    System.out.println("kind:" + kind + " : " + fileName);
                }

                boolean valid = key.reset();
                if(!valid){
                    break;
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        final Path path = Paths.get("D://");
        WatchServiceDemo watchServiceDemo = new WatchServiceDemo();
        try {
            watchServiceDemo.watchDir(path);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
