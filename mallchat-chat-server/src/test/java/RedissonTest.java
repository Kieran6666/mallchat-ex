import com.kieran.mallchat.common.MallchatCustomApplication;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = MallchatCustomApplication.class)
@RunWith(SpringRunner.class)
public class RedissonTest {

    @Resource
    private RedissonClient redissonClient;

    @Test
    public void test() {
        RLock lock = redissonClient.getLock("1234");
        lock.lock();

        System.err.println();
        lock.unlock();
    }
}
