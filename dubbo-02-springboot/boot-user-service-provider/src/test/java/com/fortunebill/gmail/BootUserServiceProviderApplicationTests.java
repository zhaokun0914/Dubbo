package com.fortunebill.gmail;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.StringUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.TimeUnit;

@RunWith(SpringRunner.class)
@SpringBootTest
@Slf4j
public class BootUserServiceProviderApplicationTests {

    @Autowired
    private RedisTemplate<String,String> redisTemplate;

    private int i = 1;

    @Test
    public void testIp() throws InterruptedException {
        while (true) {
            int time = RandomUtil.randomInt(1, 2);
            log.info("==> {}秒后请求", time);
            TimeUnit.SECONDS.sleep(time);
            String result = sendPost("https://alifree.net/?fromuid=14393", null);
            log.info("<== 收到请求:[{}]", result);
            log.info("<== 请求完成");
        }
    }

    public String sendPost(String url, String param) {
        PrintWriter out = null;
        BufferedReader in = null;
        StringBuilder result = new StringBuilder();
        try {
            URL realUrl = new URL(url);
            URLConnection conn = realUrl.openConnection();

            // 随机生成ip
            String ip = randIP();
            String ipStr = redisTemplate.opsForValue().get(ip);
            if (StringUtils.isEmpty(ipStr)) {
                redisTemplate.opsForValue().set(ip, ip);
            } else {
                return "<== ip地址已经使用过";
            }
            conn.setRequestProperty("X-Forwarded-For", ip);
            conn.setRequestProperty("HTTP_X_FORWARDED_FOR", ip);
            conn.setRequestProperty("HTTP_CLIENT_IP", ip);
            conn.setRequestProperty("REMOTE_ADDR", ip);
            conn.setRequestProperty("Host", "");
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setRequestProperty("Content-Length", "17");
            conn.setRequestProperty("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
            conn.setRequestProperty("Origin", "ORIGIN");
            String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/" + getVersionStr() + " Safari/537.36";
            conn.setRequestProperty("User-Agent", userAgent);
            conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            conn.setRequestProperty("Referer", "REFERER");
            conn.setRequestProperty("Accept-Encoding", "gzip, deflate, br");
            conn.setRequestProperty("Accept-Language", "zh,en-US;q=0.9,en;q=0.8,zh-CN;q=0.7");

            conn.setDoOutput(true);
            conn.setDoInput(true);
            out = new PrintWriter(conn.getOutputStream());
            out.print(param);
            out.flush();
            in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                result.append(line);
            }
        } catch (Exception e) {
            log.info("发送 POST 请求出现异常！" + e);
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return result.toString();
    }

    private String getVersionStr() {
        int version = RandomUtil.randomInt(42, 96);
        int version_01 = RandomUtil.randomInt(0, 5);
        int version_02 = RandomUtil.randomInt(3000, 5000);
        int version_03 = RandomUtil.randomInt(0, 200);
        return version + version_01 + version_02 + version_03 + "";
    }

    public String randIP() {
//        String result = HttpUtil.get("http://localhost:5010/get/");
//        String proxy = JSONObject.parseObject(result).getString("proxy");
        List<String> strings = FileUtil.readLines("classpath:ip.txt", StandardCharsets.UTF_8);
        String proxy = strings.get(i);
        log.info("==> 获取第{}行IP，{}", i, proxy);
        i++;
        return proxy;
    }
}
