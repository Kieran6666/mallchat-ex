import com.kieran.mallchat.common.MallchatCustomApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = MallchatCustomApplication.class)
@RunWith(SpringRunner.class)
public class RedisTest {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 序列化方式是 jackson， 但会有坑 会把long类型识别为int
     */
    @Resource
    private RedisTemplate redisTemplate;

    @Test
    public void test() {
        stringRedisTemplate.opsForValue().set("pcc", "im pca");
        Object kieran = stringRedisTemplate.opsForValue().get("pcc");
        System.err.println((String) kieran);
    }
}
