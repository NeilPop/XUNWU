package com.imooc.web.controller.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Map;
import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.imooc.base.ApiDataTableResponse;
import com.imooc.base.HouseOperation;
import com.imooc.base.HouseStatus;
import com.imooc.entity.SupportAddress;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.service.house.IAddressService;
import com.imooc.service.house.IHouseService;
import com.imooc.service.house.IQiNiuService;
import com.imooc.web.dto.*;
import com.imooc.web.form.DataTableSearch;
import com.imooc.web.form.HouseForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.hibernate.validator.constraints.NotBlank;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import com.imooc.base.ApiResponse;
import javax.validation.Valid;

@Controller
public class AdminController {

	@Autowired
	private IQiNiuService qiNiuService;

	@Autowired
	private IAddressService addressService;

	@Autowired
	private IHouseService houseService;

	@Autowired
	private Gson gson;

	@GetMapping("/admin/center")
	public String adminCenterPage() {
		return "admin/center";
	}
	
	@GetMapping("/admin/welcome")
	public String weilcomPage() {
		return "admin/welcome";
	}

	@GetMapping("admin/house/list")
	public String houseList() { return "admin/house-list"; }

	@PostMapping("admin/houses")
	@ResponseBody
	public ApiDataTableResponse houses(@ModelAttribute DataTableSearch searchBody){
		ServiceMultiResult<HouseDTO> result = houseService.adminQuery(searchBody);
		ApiDataTableResponse response = new ApiDataTableResponse(ApiResponse.Status.SUCCESS);
		response.setData(result.getResult());
		response.setRecordsFiltered(result.getTotal());
		response.setRecordsTotal(result.getTotal());
		response.setDraw(searchBody.getDraw());
		return response;

	}

	@GetMapping("/admin/login")
	public String adminLogin() {
		return "admin/login";
	}
	@GetMapping("admin/add/house")
	public String addHousePage() {
		return "admin/house-add";
	}
	
	@PostMapping(value = "admin/upload/photo",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	@ResponseBody
	public ApiResponse uploadPhoto(@RequestParam("file") MultipartFile file) {
		if (file.isEmpty()) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
		}
		String fileName = file.getOriginalFilename();
		try {
			InputStream inputStream = file.getInputStream();
			Response response = qiNiuService.UploadFile(inputStream);
			if (response.isOK()) {
				QiNiuPutRet ret = gson.fromJson(response.bodyString(), QiNiuPutRet.class);
				return ApiResponse.ofMessage(ret);
			} else {
				return ApiResponse.ofMessage(response.statusCode, response.getInfo());
			}
		}catch (QiniuException e){
			Response response = e.response;
			try {
				return ApiResponse.ofMessage(response.statusCode,response.bodyString());
			} catch (QiniuException e1) {
				e1.printStackTrace();
				return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
			}
		}catch (IOException e) {
			return ApiResponse.ofStatus(ApiResponse.Status.INTERNAL_SERVER_ERROR);
		}

	}

	@PostMapping("admin/add/house")
	@ResponseBody
	public ApiResponse addHouse(@Valid @ModelAttribute("form-house-add")HouseForm houseForm, BindingResult bindingResult){
		if (bindingResult.hasErrors()){
			return new ApiResponse(HttpStatus.BAD_REQUEST.value(),bindingResult.getAllErrors().get(0).getDefaultMessage(),null);
		}
		if (houseForm.getPhotos() == null || houseForm.getCover() == null) {
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),"必须上传图片");
		}
		Map<SupportAddress.Level, SupportAddressDTO> map =  addressService.findCityAndRegion(houseForm.getCityEnName(),houseForm.getRegionEnName());

		if (map.keySet().size()!=2){
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
		}
		ServiceResult<HouseDTO> result = houseService.save(houseForm);
		if (result.isSuccess()){
			return ApiResponse.ofSuccess(result.getResult());
		}
		return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
	}

	@PostMapping("admin/house/tag")
	@ResponseBody
	public ApiResponse addTag(@RequestParam("house_id")Long id,@RequestParam("tag")String tag){
		if (id < 1|| Strings.isNullOrEmpty(tag)){
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		ServiceResult result = houseService.addTag(id,tag);
		if (result.isSuccess()) {
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		}
		return ApiResponse.ofMessage(ApiResponse.Status.BAD_REQUEST.getCode(),result.getMessage());

	}
	/**
	 * 编辑接口
	 */
	@PostMapping("admin/house/edit")
	@ResponseBody
	public ApiResponse saveHouse(@Valid @ModelAttribute("form-house-edit") HouseForm houseForm, BindingResult bindingResult) {
		if (bindingResult.hasErrors()) {
			return new ApiResponse(HttpStatus.BAD_REQUEST.value(), bindingResult.getAllErrors().get(0).getDefaultMessage(), null);
		}

		Map<SupportAddress.Level, SupportAddressDTO> addressMap = addressService.findCityAndRegion(houseForm.getCityEnName(), houseForm.getRegionEnName());

		if (addressMap.keySet().size() != 2) {
			return ApiResponse.ofSuccess(ApiResponse.Status.NOT_VALID_PARAM);
		}

		ServiceResult result = houseService.update(houseForm);
		if (result.isSuccess()) {
			return ApiResponse.ofSuccess(null);
		}

		ApiResponse response = ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		response.setMessage(result.getMessage());
		return response;
	}

	/**
	 * 移除图片接口
	 * @param id
	 * @return
	 */
	@DeleteMapping("admin/house/photo")
	@ResponseBody
	public ApiResponse removeHousePhoto(@RequestParam(value = "id") Long id) {
		ServiceResult result = this.houseService.removePhoto(id);

		if (result.isSuccess()) {
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		} else {
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
		}
	}

	/**
	 * 修改封面接口
	 * @param coverId
	 * @param targetId
	 * @return
	 */
	@PostMapping("admin/house/cover")
	@ResponseBody
	public ApiResponse updateCover(@RequestParam(value = "cover_id") Long coverId,
								   @RequestParam(value = "target_id") Long targetId) {
		ServiceResult result = this.houseService.updateCover(coverId, targetId);

		if (result.isSuccess()) {
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		} else {
			return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(), result.getMessage());
		}
	}

	@DeleteMapping("admin/house/tag")
	@ResponseBody
	public ApiResponse removeTag(@RequestParam("house_id")Long id,@RequestParam("tag")String tag){
		if (id < 1|| Strings.isNullOrEmpty(tag)){
			return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		ServiceResult result = houseService.removeTag(id,tag);
		if (result.isSuccess()) {
			return ApiResponse.ofStatus(ApiResponse.Status.SUCCESS);
		}
		return ApiResponse.ofMessage(ApiResponse.Status.BAD_REQUEST.getCode(),result.getMessage());
	}

	@GetMapping("admin/house/edit")
	public String houseEditPage(@RequestParam(value = "id")Long id, Model model){
		if (id == null || id<1){
			return "404";
		}
		ServiceResult<HouseDTO> serviceResult = houseService.findCompleteOne(id);
		if (!serviceResult.isSuccess()){
			return "404";
		}
		HouseDTO result = serviceResult.getResult();
		HouseDetailDTO houseDetailDTO = result.getHouseDetail();
		model.addAttribute("house",result);
		Map<SupportAddress.Level,SupportAddressDTO> addressMap = addressService
				.findCityAndRegion(result.getCityEnName(),result.getRegionEnName());
		model.addAttribute("city",addressMap.get(SupportAddress.Level.CITY));
		model.addAttribute("region",addressMap.get(SupportAddress.Level.REGION));
		ServiceResult<SubwayDTO> subwayServiceResult = addressService.findSubway(houseDetailDTO.getSubwayLineId());
		if (subwayServiceResult.isSuccess()){
			model.addAttribute("subway",subwayServiceResult.getResult());
		}
		ServiceResult<SubwayStationDTO> subwayStationServiceResult = addressService.findSubwayStation(houseDetailDTO.getSubwayStationId());
		if (subwayStationServiceResult.isSuccess()) {
			model.addAttribute("station",subwayStationServiceResult.getResult());
		}

		return "admin/house-edit";
	}

	/**
	 * 管理員操作接口（审核、下架、删除、出租）
	 */
	@PutMapping("admin/house/operate/{id}/{operation}")
	@ResponseBody
	public ApiResponse operate(@PathVariable(value = "id")Long id,@PathVariable(value = "operation")int operation){
		if (id <= 0) {
			return ApiResponse.ofStatus(ApiResponse.Status.NOT_VALID_PARAM);
		}
		ServiceResult result;
		switch (operation){
			case HouseOperation.PASS:
				result = this.houseService.updateStatus(id,HouseStatus.PASSES.getValue());
				break;
			case HouseOperation.DELETE:
				result = this.houseService.updateStatus(id,HouseStatus.DELETED.getValue());
				break;
			case HouseOperation.PULL_OUT:
				result = this.houseService.updateStatus(id,HouseStatus.NOT_AUDITED.getValue());
				break;
			case HouseOperation.RENT:
				result = this.houseService.updateStatus(id,HouseStatus.RENTED.getValue());
				break;
			default:
				return ApiResponse.ofStatus(ApiResponse.Status.BAD_REQUEST);
		}
		if (result.isSuccess()) {
			return ApiResponse.ofSuccess(null);
		}
		return ApiResponse.ofMessage(HttpStatus.BAD_REQUEST.value(),result.getMessage());
	}

}
