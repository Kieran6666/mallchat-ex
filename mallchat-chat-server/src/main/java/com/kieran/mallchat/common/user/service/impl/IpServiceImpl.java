package com.kieran.mallchat.common.user.service.impl;

import cn.hutool.core.lang.TypeReference;
import cn.hutool.core.thread.NamedThreadFactory;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONUtil;
import com.kieran.mallchat.common.common.config.thread.MyUncaughtExceptionHandler;
import com.kieran.mallchat.common.user.dao.UserDao;
import com.kieran.mallchat.common.user.domain.dto.IpResult;
import com.kieran.mallchat.common.user.domain.entity.IpDetail;
import com.kieran.mallchat.common.user.domain.entity.IpInfo;
import com.kieran.mallchat.common.user.domain.entity.User;
import com.kieran.mallchat.common.user.service.IpService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 实现了一个自定义的线程池，并没有交给Spring管理这个线程池，所以不会自动销毁，需要手动销毁
 */
@Slf4j
@Service
public class IpServiceImpl implements IpService, DisposableBean {

    private static final ExecutorService EXECUTOR = new ThreadPoolExecutor(1, 1,
            0L, TimeUnit.MILLISECONDS,
            new LinkedBlockingQueue<>(500),
            new NamedThreadFactory("refresh-ipDetail", null, false,
                    new MyUncaughtExceptionHandler()));

    @Resource
    private UserDao userDao;

    @Override
    public void refreshIpAsync(Long uid) {
        EXECUTOR.execute(() -> {
            User user = userDao.getById(uid);
            IpInfo ipInfo = user.getIpInfo();
            if (Objects.isNull(ipInfo)) {
                return;
            }
            String ip = ipInfo.needRefreshIp();
            if (StrUtil.isBlank(ip)) {
                return;
            }
            // 异步调用淘宝的IP解析接口
            IpDetail ipDetail = tryGetIpAsyncThreeTimes(user.getIpInfo().getUpdateIp());
            if (Objects.nonNull(ipDetail)) {
                ipInfo.refreshIpDetail(ipDetail);
                User update = new User();
                update.setId(uid);
                update.setIpInfo(ipInfo);
                userDao.updateById(update);
//                userCache.userInfoChange(uid);
            } else {
                log.error("get ip detail fail ip:{},uid:{}", ip, uid);
            }
        });


    }

    private static IpDetail tryGetIpAsyncThreeTimes(String ip) {
        String url = String.format("https://ip.taobao.com/outGetIpInfo?ip=%s&accessKey=alibaba-inc", ip);

        for (int i = 0; i < 3; i++) {
            String data = HttpUtil.get(url);
            IpResult<IpDetail> result = JSONUtil.toBean(data, new TypeReference<IpResult<IpDetail>>() {}, false);
            if (result.isSuccess() && Objects.nonNull(result.getData())) {
                return result.getData();
            } else {
               try {
                   Thread.sleep(2000);
               } catch (Exception e) {
                   e.printStackTrace();
               }
            }
        }

        return null;
    }

    @Override
    public void destroy() throws Exception {
        EXECUTOR.shutdown();
        if (EXECUTOR.awaitTermination(3000, TimeUnit.MILLISECONDS)) { // 等30秒，超时就结束
            if (log.isErrorEnabled()) {
                log.error("Timed out while waiting for executor [{}] to terminate", EXECUTOR);
            }

        }
    }

    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            final int fi = i;
            EXECUTOR.execute(() -> {
                IpDetail ipDetail = tryGetIpAsyncThreeTimes("163.43.245.73");
                System.out.println(String.format("第%d次成功", fi));
                System.err.println(ipDetail.toString());
            });
        }
    }
}
