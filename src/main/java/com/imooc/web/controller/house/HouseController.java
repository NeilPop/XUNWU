package com.imooc.web.controller.house;

import com.imooc.base.ApiResponse;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.house.IAddressService;
import com.imooc.web.dto.SubwayDTO;
import com.imooc.web.dto.SubwayStationDTO;
import com.imooc.web.dto.SupportAddressDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class HouseController {
    @Autowired
    private IAddressService addressService;

    @GetMapping("address/support/cities")
    @ResponseBody
    public ApiResponse getSupportCities(){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllCities();
        if (result.getResultSize() == 0) {
            return ApiResponse.ofMessage(ApiResponse.Status.NOT_FOUND.getCode(),ApiResponse.Status.NOT_FOUND.getStandardMessage());
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("address/support/regions")
    @ResponseBody
    public ApiResponse getSupportRegions(@RequestParam(value="city_name",required = true)String city){
        ServiceMultiResult<SupportAddressDTO> result = addressService.findAllRegionsByCity(city);
        if (result.getResult() == null || result.getResultSize() == 0) {
            return ApiResponse.ofMessage(ApiResponse.Status.NOT_FOUND.getCode(),ApiResponse.Status.NOT_FOUND.getStandardMessage());
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("address/support/subway/line")
    @ResponseBody
    public ApiResponse getSupportSubways(@RequestParam(value="city_name",required = true)String city){
        ServiceMultiResult<SubwayDTO> result = addressService.findAllSubwaysByCityEnName(city);
        if(result.getResult() == null || result.getResultSize() == 0){
            return ApiResponse.ofMessage(ApiResponse.Status.NOT_FOUND.getCode(),ApiResponse.Status.NOT_FOUND.getStandardMessage());
        }
        return ApiResponse.ofSuccess(result.getResult());
    }

    @GetMapping("address/support/subway/station")
    @ResponseBody
    public ApiResponse getSupportStations(@RequestParam(value = "subway_id")Long subwayId){
        ServiceMultiResult<SubwayStationDTO> result = addressService.findAllStationsBySubwayId(subwayId);
        if(result.getResult() == null || result.getResultSize() == 0){
            return ApiResponse.ofMessage(ApiResponse.Status.NOT_FOUND.getCode(),ApiResponse.Status.NOT_FOUND.getStandardMessage());
        }
        return ApiResponse.ofSuccess(result.getResult());
    }


}
