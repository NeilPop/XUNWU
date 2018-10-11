package com.imooc.repository;

import com.imooc.entity.SupportAddress;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface SupportAddressRepository extends CrudRepository<SupportAddress,Long> {
    /**
     * 获取所有对应行政级别的信息
     */
    List<SupportAddress> findAllByLevel(String level);

    /**
     * 根据城市来获取所有行政区
     */
    SupportAddress findDistinctFirstByLevelAndBelongTo(String level,String belongTo);

    /**
     * 根据城市和地区来获取信息
     * @param enName
     * @param belongTo
     * @return
     */
    SupportAddress findDistinctFirstByEnNameAndBelongTo(String enName,String belongTo);


}
