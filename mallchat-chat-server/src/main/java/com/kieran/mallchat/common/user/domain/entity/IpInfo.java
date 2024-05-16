package com.kieran.mallchat.common.user.domain.entity;

import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

/**
 * 这里的IPINFO需要保存到数据库中，所以需要序列化
 */
@Data
public class IpInfo implements Serializable {

    /**
     * 在不同版本下保证序列化反序列化的兼容性，如果不定义的话，会自动根据class的内容生成一个序列化ID
     */
    private static final long serialVersionUID = 1L;


    private String createIp;

    //注册时的ip详情
    private IpDetail createIpDetail;

    //最新登录的ip
    private String updateIp;

    //最新登录的ip详情
    private IpDetail updateIpDetail;


    public void refreshIp(String ip) {
        if (StringUtils.isEmpty(ip)) {
            return;
        }
        if (StringUtils.isEmpty(this.createIp)) {
            this.createIp = ip;
        }

        this.updateIp = ip;
    }

    /**
     * 需要刷新的ip，这里判断更新ip就够，初始化的时候ip也是相同的，只需要设置的时候多设置进去就行
     */
    public String needRefreshIp() {
        boolean notNeedRefresh = Optional.ofNullable(updateIpDetail)
                .map(IpDetail::getIp)
                .filter(ip -> Objects.equals(ip, updateIp))
                .isPresent();
        return notNeedRefresh ? null : updateIp;
    }

    public void refreshIpDetail(IpDetail ipDetail) {
        if (Objects.equals(createIp, ipDetail.getIp())) {
            createIpDetail = ipDetail;
        }
        if (Objects.equals(updateIp, ipDetail.getIp())) {
            updateIpDetail = ipDetail;
        }
    }


}
