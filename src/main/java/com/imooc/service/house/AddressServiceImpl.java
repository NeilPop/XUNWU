package com.imooc.service.house;

import com.imooc.entity.Subway;
import com.imooc.entity.SubwayStation;
import com.imooc.entity.SupportAddress;
import com.imooc.repository.SubwayRepository;
import com.imooc.repository.SubwayStationRepository;
import com.imooc.repository.SupportAddressRepository;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AddressServiceImpl implements IAddressService {
    @Autowired
    private SupportAddressRepository supportAddressRepository;

    @Autowired
    private SubwayRepository subwayRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllCities() {
        List<SupportAddress> cities = supportAddressRepository.findAllByLevel(SupportAddress.Level.CITY.getValue());
        List<SupportAddressDTO> addressDTOS = new ArrayList<>();
        for (SupportAddress city : cities) {
            SupportAddressDTO target = modelMapper.map(city,SupportAddressDTO.class);
            addressDTOS.add(target);
        }
        return new ServiceMultiResult<>(addressDTOS.size(),addressDTOS);
    }

    @Override
    public ServiceMultiResult<SupportAddressDTO> findAllRegionsByCity(String city) {
        List<SupportAddress> regions = supportAddressRepository.findAllByLevel(SupportAddress.Level.REGION.getValue());
        List<SupportAddressDTO> addressDTOS = new ArrayList<>();
        for (SupportAddress region : regions) {
            //if (region.getBelongTo().equals(city)) {
                SupportAddressDTO target = modelMapper.map(region, SupportAddressDTO.class);
                addressDTOS.add(target);
            //}
        }
        return new ServiceMultiResult<>(addressDTOS.size(),addressDTOS);

    }

    @Override
    public ServiceMultiResult<SubwayDTO> findAllSubwaysByCityEnName(String cityEnName) {
        List<Subway> subways = subwayRepository.findAllByCityEnName(cityEnName);
        List<SubwayDTO> subwayDTOS = new ArrayList<>();
        for(Subway subway : subways){
            SubwayDTO target = modelMapper.map(subway,SubwayDTO.class);
            subwayDTOS.add(target);
        }
        return new ServiceMultiResult<>(subwayDTOS.size(),subwayDTOS);
    }

    @Override
    public ServiceMultiResult<SubwayStationDTO> findAllStationsBySubwayId(Long subwayId) {
        List<SubwayStation> stations = subwayStationRepository.findAllBySubwayId(subwayId);
        List<SubwayStationDTO> stationDTOS = new ArrayList<>();
        for (SubwayStation station : stations) {
            SubwayStationDTO target = modelMapper.map(station,SubwayStationDTO.class);
            stationDTOS.add(target);
        }
        return new ServiceMultiResult<>(stationDTOS.size(),stationDTOS);
    }

    @Override
    public Map<SupportAddress.Level, SupportAddressDTO> findCityAndRegion(String cityEnName, String regionEnName) {
        SupportAddress city = supportAddressRepository.findDistinctFirstByEnNameAndBelongTo(cityEnName,cityEnName);
        SupportAddress region = supportAddressRepository.findDistinctFirstByEnNameAndBelongTo(regionEnName,cityEnName);
        Map<SupportAddress.Level,SupportAddressDTO> map = new HashMap<>();
        if (city!=null){
            map.put(SupportAddress.Level.CITY,modelMapper.map(city,SupportAddressDTO.class));
        }
        if (region != null){
            map.put(SupportAddress.Level.REGION,modelMapper.map(region,SupportAddressDTO.class));
        }

        return map;
    }

    @Override
    public ServiceResult<SubwayDTO> findSubway(Long subwayLineId) {
        Subway subway = subwayRepository.findOne(subwayLineId);
        if (subway == null){
            return ServiceResult.notFound();
        }
        SubwayDTO subwayDTO = modelMapper.map(subway,SubwayDTO.class);
        return ServiceResult.of(subwayDTO);
    }

    @Override
    public ServiceResult<SubwayStationDTO> findSubwayStation(Long subwayStationId) {
        SubwayStation subwayStation = subwayStationRepository.findOne(subwayStationId);
        if (subwayStation == null) {
            return ServiceResult.notFound();
        }
        SubwayStationDTO subwayStationDTO = modelMapper.map(subwayStation,SubwayStationDTO.class);
        return ServiceResult.of(subwayStationDTO);
    }
}
