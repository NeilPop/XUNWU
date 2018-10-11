package com.imooc.service.house;

import com.imooc.entity.SupportAddress;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;

import java.util.Map;


public interface IAddressService {
    ServiceMultiResult<SupportAddressDTO> findAllCities();

    ServiceMultiResult<SupportAddressDTO> findAllRegionsByCity(String city);

    ServiceMultiResult<SubwayDTO> findAllSubwaysByCityEnName(String cityEnName);

    ServiceMultiResult<SubwayStationDTO> findAllStationsBySubwayId(Long subwayId);

    Map<SupportAddress.Level,SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName);

    ServiceResult<SubwayDTO> findSubway(Long subwayLineId);

    ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId);
}
