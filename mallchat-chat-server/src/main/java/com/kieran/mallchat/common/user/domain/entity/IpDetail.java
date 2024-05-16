package com.kieran.mallchat.common.user.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class IpDetail implements Serializable {

    private static final long serialVersionUID = 1L;

    private String area;

    private String country;

    private String isp_id;

    private String queryIp;

    private String city;
    private String ip;
    private String isp;
    private String county;
    private String region_id;
    private String area_id;
    private String county_id;
    private String region;
    private String country_id;
    private String city_id;
}
