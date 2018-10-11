package com.imooc.service.house;

import com.imooc.ApplicationTests;
import com.imooc.entity.Subway;
import com.imooc.entity.SubwayStation;
import com.imooc.service.ServiceMultiResult;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;


public class AddressServiceTests extends ApplicationTests {
    @Autowired
    private IAddressService addressService;

    @Test
    public void getSubways(){
        ServiceMultiResult<SubwayDTO> result = addressService.findAllSubwaysByCityEnName("bj");
        List<SubwayDTO> subways = result.getResult();
        for (SubwayDTO subway : subways) {
            System.out.println("id="+subway.getId()+",name="+subway.getName()+",city_en_name="+subway.getCityEnName());
        }
        Assert.assertTrue(result.getTotal()== 7 );

    }

    @Test
    public void getStations(){
        ServiceMultiResult<SubwayStationDTO> result = addressService.findAllStationsBySubwayId(1l);
        List<SubwayStationDTO> stations = result.getResult();
        for (SubwayStationDTO station : stations) {
            System.out.println("id="+station.getId()+",subway_id="+station.getSubwayId()+",name="+station.getName());
        }
        Assert.assertTrue(result.getResultSize()==16);
    }
}
