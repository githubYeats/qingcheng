package com.qingcheng.util;

import java.util.UUID;

/**
 * Author: Feiyue
 * Date: 2019/8/4 0:24
 * Desc:
 */
public class Demo {
    public static void main(String[] args) {
        IdWorker idWorker = new IdWorker();
        long id = 0L;
        for (int i = 0; i < 10000; i++) {
            id = idWorker.nextId();
            System.out.println(id);
        }

        // UUID
        System.out.println("UUID: " + UUID.randomUUID().toString());
    }
}
