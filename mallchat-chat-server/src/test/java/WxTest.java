import com.kieran.mallchat.common.MallchatCustomApplication;
import me.chanjar.weixin.common.error.WxErrorException;
import me.chanjar.weixin.mp.api.WxMpService;
import me.chanjar.weixin.mp.bean.result.WxMpQrCodeTicket;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@SpringBootTest(classes = MallchatCustomApplication.class)
@RunWith(SpringRunner.class)
public class WxTest {

    /**
     * 测试类中想注入bean 就需要加入 @RunWith注解， 而@Runwith注解在 org.springframework.spring-test依赖中，需要引入
     */
    @Resource
    private WxMpService wxMpService;


    @Test
    public void test() throws WxErrorException {
        WxMpQrCodeTicket wxMpQrCodeTicket = wxMpService.getQrcodeService().qrCodeCreateTmpTicket(1, 10000);
        String url = wxMpQrCodeTicket.getUrl();
        System.err.println(url);

    }
}
