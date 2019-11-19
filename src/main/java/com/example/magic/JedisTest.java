package com.example.magic;

import redis.clients.jedis.Jedis;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Description: jedis使用
 */
public class JedisTest {

    public static void main(String[] args) {

        // 创建一个redis连接(也可以创建池)
        Jedis jedis=new Jedis("127.0.0.1", 6379);

        // 写入一个字符串;
        jedis.set("key1", "string-value1");
        String value1 = jedis.get("key1");
//        设置key的时间 单位秒
        jedis.expire("key1", 60);
        // 打印string-value1
        System.out.println(value1);
        // key不存在则返回 null
        System.out.println(jedis.get("key1"));

        // 写入一个hash
        HashMap<String, String> hashMap = new HashMap<String, String>();
        hashMap.put("name", "zhang-");
        hashMap.put("age", "36");
        jedis.hmset("user", hashMap);
        jedis.expire("user", 20);
        //获取按map中的key来获取数据,得到一个list
        List<String> hmget = jedis.hmget("user", "name", "age");
        System.out.println(hmget);
        //判断hashMap中是否存在某个字段
        Boolean isExist = jedis.hexists("user", "name");
        System.out.println(isExist);
        //删除某个字段
//        jedis.hdel("user", "name");
        //获取整个hashMap
        Map<String, String> map = jedis.hgetAll("user");
        System.out.println(map);

        //写入一个list(列表)
        // 在头部写入数据,列表数据是 [name2, name1]
        jedis.lpush("user1", "name1", "name2");
        // 在尾部写入数据,列表数据是[name1, name2]
        jedis.rpush("user1", "name1", "name2");
        // 按索引来获取数据
        jedis.lindex("key", 1);
        // 获取列表的长度
        long length = jedis.llen("user2");
        System.out.println(length);

        //无序set操作
        // 创建一个set
        jedis.sadd("set1", "value1");
        jedis.sadd("set1", "value2");
        jedis.sadd("set1", "value3");

        // 获取整个set
        Set<String> set1 = jedis.smembers("set1");

        // 移出某个value
        jedis.srem("set1", "value2");

        // 判断是否存在该value
        boolean sismember = jedis.sismember("set1", "value2");

        //有序set操作
        // 有序set
        jedis.zadd("set2", 1, "value1");
        jedis.zadd("set2", 10, "value10");
        jedis.zadd("set2", 11, "value11");
        jedis.zadd("set2", 9, "value9");
        jedis.zadd("set2", 5, "value5");

        // 获取set的长度
        Long set21 = jedis.zcard("set2");
        System.out.println(set21);

        // 获取set的片段
        Set<String> set2 = jedis.zrange("set2", 0, 10);
        System.out.println(set2);
    }
}
