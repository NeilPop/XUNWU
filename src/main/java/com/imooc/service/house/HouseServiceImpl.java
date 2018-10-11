package com.imooc.service.house;

import com.imooc.base.HouseStatus;
import com.imooc.base.LoginUserUtil;
import com.imooc.entity.*;
import com.imooc.repository.*;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.dto.HouseDetailDTO;
import com.imooc.web.dto.HousePictureDTO;
import com.imooc.web.form.DataTableSearch;
import com.imooc.web.form.HouseForm;
import com.imooc.web.form.PhotoForm;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service

public class HouseServiceImpl implements IHouseService {

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private HouseRepository houseRepository;

    @Autowired
    private HousePictureRepository housePictureRepository;

    @Autowired
    private HouseTagRepository houseTagRepository;

    @Autowired
    private HouseDetailRepository houseDetailRepository;

    @Autowired
    private SubwayStationRepository subwayStationRepository;

    @Autowired
    private SubwayRepository subwayRepository;


    @Autowired
    private IQiNiuService qiNiuService;
    @Value("${qiniu.cdn.prefix}")
    private String cdnPrefix;

    @Override
    @Transactional
    public ServiceResult<HouseDTO> save(HouseForm houseForm) {
        HouseDetail detail = new HouseDetail();
        ServiceResult<HouseDTO> subwayValidationResult = wrapperDetailInfo(detail,houseForm);
        if (subwayValidationResult!=null){
            return subwayValidationResult;
        }
        House house = new House();
        modelMapper.map(houseForm,house);
        Date now = new Date();
        house.setCreateTime(now);
        house.setLastUpdateTime(now);
        house.setAdminId(LoginUserUtil.getLoginUserId());
        house = houseRepository.save(house);

        detail.setHouseId(house.getId());
        detail = houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm,house.getId());
        Iterable<HousePicture> housePictures = housePictureRepository.save(pictures);

        HouseDTO houseDTO = modelMapper.map(house,HouseDTO.class);
        HouseDetailDTO houseDetailDTO = modelMapper.map(detail,HouseDetailDTO.class);

        houseDTO.setHouseDetail(houseDetailDTO);
        List<HousePictureDTO> housePicturesDTOS = new ArrayList<>();
        housePictures.forEach(housePicture -> housePicturesDTOS.add(modelMapper.map(housePicture, HousePictureDTO.class)));
        houseDTO.setPictures(housePicturesDTOS);
        houseDTO.setCover(this.cdnPrefix+"/"+houseDTO.getCover());

        List<String> tags = houseForm.getTags();
        if (tags!=null||!tags.isEmpty()){
            List<HouseTag> houseTags = new ArrayList<>();
            for (String tag:tags){
                houseTags.add(new HouseTag(house.getId(),tag));
            }
            houseTagRepository.save(houseTags);
            houseDTO.setTags(tags);
        }
        return new ServiceResult<HouseDTO>(true,null,houseDTO);

    }

    @Override
    public ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody) {
        List<HouseDTO> houseDTOS = new ArrayList<>();
        Sort sort = new Sort(Sort.Direction.fromString(searchBody.getDirection()),searchBody.getOrderBy());
        int page = searchBody.getStart()/searchBody.getLength();
        Pageable pageable = new PageRequest(page,searchBody.getLength(),sort);
        Specification<House> specification = (root,query,cb)->{
            Predicate predicate = cb.equal(root.get("adminId"),LoginUserUtil.getLoginUserId());
            predicate = cb.and(predicate,cb.notEqual(root.get("status"), HouseStatus.DELETED.getValue()));
            if (searchBody.getCity() != null) {
                predicate = cb.and(predicate,cb.equal(root.get("cityEnName"),searchBody.getCity()));

            }
            if (searchBody.getStatus() != null) {
                predicate = cb.and(predicate,cb.equal(root.get("status"),searchBody.getStatus()));
            }
            if (searchBody.getCreateTimeMax() != null) {
                predicate = cb.and(predicate,cb.lessThanOrEqualTo(root.get("createTime"),searchBody.getCreateTimeMax()));
            }
            if (searchBody.getCreateTimeMin() != null) {
                predicate = cb.and(predicate,cb.greaterThanOrEqualTo(root.get("createTime"),searchBody.getCreateTimeMin()));
            }
            if (searchBody.getTitle() != null) {
                predicate = cb.and(predicate,cb.like(root.get("title"),"%"+searchBody.getTitle()+"%"));
            }
            return predicate;
        };

        Page<House> houses = houseRepository.findAll(specification,pageable);
        houses.forEach(house->{
            HouseDTO houseDTO = modelMapper.map(house,HouseDTO.class);
            houseDTO.setCover(this.cdnPrefix+"/"+house.getCover());
            houseDTOS.add(houseDTO);
        });
        return new ServiceMultiResult<>(houses.getTotalElements(),houseDTOS);
    }

    @Override
    @Transactional
    public ServiceResult addTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag,houseId);
        if (houseTag != null) {
            return new ServiceResult(false,"该标签已存在！");
        }
        houseTagRepository.save(new HouseTag(houseId,tag));
        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult removeTag(Long houseId, String tag) {
        House house = houseRepository.findOne(houseId);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseTag houseTag = houseTagRepository.findByNameAndHouseId(tag,houseId);
        if (houseTag == null) {
            return new ServiceResult(false,"该标签不存在，不能移除！");
        }
        houseTagRepository.delete(houseTag.getId());
        return ServiceResult.ofSuccess();
    }

    @Override
    public ServiceResult<HouseDTO> findCompleteOne(Long id) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }
        HouseDetail houseDetail= houseDetailRepository.findByHouseId(id);
        HouseDetailDTO houseDetailDTO = modelMapper.map(houseDetail,HouseDetailDTO.class);
        List<HousePicture> pictures = housePictureRepository.findAllByHouseId(id);
        List<HousePictureDTO> housePictureDTOS = new ArrayList<>();
        for (HousePicture picture:pictures){
            HousePictureDTO housePictureDTO = modelMapper.map(picture,HousePictureDTO.class);
            housePictureDTOS.add(housePictureDTO);
        }
        List<String> tags = new ArrayList<>();
        for (HouseTag houseTag : houseTagRepository.findAllByHouseId(id)) {
            tags.add(houseTag.getName());
        }
        HouseDTO houseDTO = modelMapper.map(house,HouseDTO.class);
        houseDTO.setHouseDetail(houseDetailDTO);
        houseDTO.setTags(tags);
        houseDTO.setPictures(housePictureDTOS);
        houseDTO.setCover(this.cdnPrefix+"/"+houseDTO.getCover());

        return ServiceResult.of(houseDTO);
    }

    @Override
    @Transactional
    public ServiceResult update(HouseForm houseForm) {
        House house = this.houseRepository.findOne(houseForm.getId());
        if (house == null) {
            return ServiceResult.notFound();
        }

        HouseDetail detail = this.houseDetailRepository.findByHouseId(house.getId());
        if (detail == null) {
            return ServiceResult.notFound();
        }

        ServiceResult wrapperResult = wrapperDetailInfo(detail, houseForm);
        if (wrapperResult != null) {
            return wrapperResult;
        }

        houseDetailRepository.save(detail);

        List<HousePicture> pictures = generatePictures(houseForm, houseForm.getId());
        housePictureRepository.save(pictures);

        if (houseForm.getCover() == null) {
            houseForm.setCover(house.getCover());
        }

        modelMapper.map(houseForm, house);
        house.setLastUpdateTime(new Date());
        houseRepository.save(house);

//        if (house.getStatus() == HouseStatus.PASSES.getValue()) {
//            searchService.index(house.getId());
//        }

        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult removePhoto(Long id) {
        HousePicture picture = housePictureRepository.findOne(id);
        if (picture == null) {
            return ServiceResult.notFound();
        }

        try {
            Response response = this.qiNiuService.delete(picture.getPath());
            if (response.isOK()) {
                housePictureRepository.delete(id);
                return ServiceResult.ofSuccess();
            } else {
                return new ServiceResult(false, response.error);
            }
        } catch (QiniuException e) {
            e.printStackTrace();
            return new ServiceResult(false, e.getMessage());
        }
    }

    @Override
    @Transactional
    public ServiceResult updateCover(Long coverId, Long targetId) {
        HousePicture cover = housePictureRepository.findOne(coverId);
        if (cover == null) {
            return ServiceResult.notFound();
        }

        houseRepository.updateCover(targetId, cover.getPath());
        return ServiceResult.ofSuccess();
    }

    @Override
    @Transactional
    public ServiceResult updateStatus(Long id, int operation) {
        House house = houseRepository.findOne(id);
        if (house == null) {
            return ServiceResult.notFound();
        }
        if (operation == house.getStatus()){
            return new ServiceResult(false,"状态没有发生变化");
        }
        if (house.getStatus() == HouseStatus.RENTED.getValue()){
            return new ServiceResult(false,"房屋已经出租，不允许修改状态");
        }
        if (house.getStatus() == HouseStatus.DELETED.getValue()){
            return new ServiceResult(false,"房屋已经删除，不允许修改状态");
        }

        houseRepository.updateStatus(id,operation);
        return ServiceResult.ofSuccess();
    }

    private ServiceResult<HouseDTO> wrapperDetailInfo(HouseDetail houseDetail, HouseForm houseForm){
        Subway subway = subwayRepository.findOne(houseForm.getSubwayLineId());
        if (subway == null){
            return new ServiceResult<>(false,"Not valid subway line");
        }
        SubwayStation subwayStation = subwayStationRepository.findOne(houseForm.getSubwayStationId());
        if (subwayStation == null) {
            return new ServiceResult<>(false, "Not valid Subway station");
        }
        houseDetail.setSubwayLineId(subway.getId());
        houseDetail.setSubwayLineName(subway.getName());
        houseDetail.setSubwayStationId(subwayStation.getSubwayId());
        houseDetail.setSubwayStationName(subwayStation.getName());
        houseDetail.setDescription(houseForm.getDescription());
        houseDetail.setLayoutDesc(houseForm.getLayoutDesc());
        houseDetail.setRentWay(houseForm.getRentWay());
        houseDetail.setRoundService(houseForm.getRoundService());
        houseDetail.setTraffic(houseForm.getTraffic());
        houseDetail.setAddress(houseForm.getDetailAddress());
        return null;
    }

    private List<HousePicture> generatePictures(HouseForm houseForm,Long houseId){
        List<HousePicture> pictures = new ArrayList<>();
        if (houseForm.getPhotos() == null || houseForm.getPhotos().isEmpty()){
            return pictures;
        }
        for (PhotoForm photoForm : houseForm.getPhotos()){
            HousePicture picture = new HousePicture();
            picture.setHouseId(houseId);
            picture.setHeight(photoForm.getHeight());
            picture.setWidth(photoForm.getWidth());
            picture.setPath(photoForm.getPath());
            picture.setCdnPrefix(this.cdnPrefix);
            //picture.setLocation();
            pictures.add(picture);
        }
        return pictures;
    }
}
