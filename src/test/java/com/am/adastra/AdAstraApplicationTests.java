package com.am.adastra;

import com.alibaba.fastjson.JSONObject;
import com.am.adastra.app.LivePool;
import com.am.adastra.entity.Video;
import com.am.adastra.mapper.UserHistoryMapper;
import com.am.adastra.mapper.UserMapper;
import com.am.adastra.service.UserHistoryService;
import com.am.adastra.service.VideoService;
import com.am.adastra.util.HttpClient;
import com.am.adastra.util.SMSUtil;
import com.am.adastra.util.EmailUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SessionCallback;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.*;
import java.util.regex.Pattern;

@Slf4j
@AutoConfigureMockMvc
@SpringBootTest(classes = AdAstraApplication.class)
class AdAstraApplicationTests {
    @Autowired
    private RedisTemplate redisTemplate;


    @Autowired
    private MockMvc mockMvc;

    @Resource
    LivePool livePool;

    @Autowired
    private EmailUtil emailUtil;

    @Autowired
    private SMSUtil smsUtil;

    @Autowired
    VideoService videoService;

    @Autowired
    UserHistoryService userHistoryService;

    @Autowired
    DataSource dataSource;

    @Autowired
    UserHistoryMapper userHistoryMapper;

    private static final String JSON_BIRD_V1 = "https://bird.ioliu.cn/v1";
    private static final String JSON_BIRD_V2 = "https://bird.ioliu.cn/v2";
    private static final String BL_URL = "https://api.live.bilibili.com/xlive/web-interface/v1/webMain/getMoreRecList?platform=web";

    @Test
    void http() {
//        String client = HttpClient.client(JSON_BIRD_V1 + "?url=" + BL_URL, HttpMethod.GET, null);
        String client = HttpClient.client("https://bird.ioliu.cn/v2?url=https://api.live.bilibili.com/xlive/web-interface/v1/webMain/getMoreRecList?platform=web", HttpMethod.GET, null);
        log.info(client + "");
        client = HttpClient.client(BL_URL, HttpMethod.GET, null);
        log.info(client + "");
    }

    @Test
    void history() {
        System.out.println(userHistoryService.getAll(11L));
    }

    @Test
    void dateHistory() {
//        long dis = 7 * 24 * 60 * 60 * 1000;
        long dis = 16 * 60 * 60 * 1000;
        long c = System.currentTimeMillis();
        Date date = new Date(c - dis);
        List<Video> limitByDate = userHistoryMapper.getLimitByDate(11L, date);
        for (Video video : limitByDate) {
            System.out.println(video.getAid());
        }
    }

    @Test
    void data() {
        System.out.println(dataSource);
    }

    @Test
    public void mail() {
        String to = "1743089727@qq.com";
        emailUtil.sendRegisterMail(to, null);
    }

    @Test
    public void reTest() {
        Pattern pattern = Pattern.compile("^1[3|4|5|8][0-9]\\d{8}$");
        String phone = "15911245016";
        System.out.println(pattern.matcher(phone).matches());
    }

    @Test
    public void sms() {
        System.out.println(smsUtil.sendSMS("123", null));
        System.out.println(smsUtil.sendSMS("abc", null));
        System.out.println(smsUtil.sendSMS("15911245016", null));
    }

    @Test
    public void reMailTest() {
        Pattern pattern = Pattern.compile("^\\s*\\w+(?:\\.?[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$");
        String mail = "174308972@qq.com";
        System.out.println(pattern.matcher(mail).matches());
    }

    @Autowired
    public UserMapper userMapper;

    @Test
    void getMD5() {
        String s = "123456admin";
        System.out.println(DigestUtils.md5Hex(s));
    }

    @Test
    void getJson() {
        String[] item = new String[]{};
    }


    @Test
    void testGetPid() {
        Integer pid = 155;
        Map entries = redisTemplate.opsForHash().entries("video:811566062");
        System.out.println(entries);
    }

    @Test
    void remove() {

    }


    @Test
    void getAllVideo() {
        Long st = System.currentTimeMillis();
        Set<String> keys = redisTemplate.keys("*");
        HashMap<Object, Object> map = new HashMap<>();
        for (String key : keys) {
            try {
                Object value = redisTemplate.opsForValue().get(key);
                System.out.println(value);
                map.put(key, value);
            } catch (Exception e) {
                System.out.println("error" + key);
            }
        }
        System.out.println(map.size());
        Long ed = System.currentTimeMillis();
        System.out.println((ed - st) / 1000);
    }

    @Test
    public void getAllVideo2() {
        Set keys = redisTemplate.keys("video:" + "*");
        Iterator<String> iterator = keys.iterator();
        List<Video> pipelinedResultList = redisTemplate.executePipelined(
                new SessionCallback<Object>() {
                    @Override
                    public <K, V> Object execute(RedisOperations<K, V> operations) throws DataAccessException {
                        HashOperations<K, Object, Object> hashOperations = operations.opsForHash();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            hashOperations.entries((K) key);
                        }
                        return null;
                    }
                });
        System.out.println(pipelinedResultList.get(0));
        System.out.println(pipelinedResultList.size());
//        return pipelinedResultList;
    }


    public static <T> T mapToObject(Map<String, Object> map, Class<T> clazz) {
        String jsonStr = JSONObject.toJSONString(map);
        return JSONObject.parseObject(jsonStr, clazz);
    }

}
