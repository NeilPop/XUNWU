package com.imooc.service.house;

import com.imooc.base.HouseStatus;
import com.imooc.service.ServiceMultiResult;
import com.imooc.service.ServiceResult;
import com.imooc.web.dto.HouseDTO;
import com.imooc.web.form.DataTableSearch;
import com.imooc.web.form.HouseForm;

/**
 * 房屋管理
 */
public interface IHouseService {
    ServiceResult<HouseDTO> save(HouseForm houseForm);

    ServiceMultiResult<HouseDTO> adminQuery(DataTableSearch searchBody);

    ServiceResult addTag(Long houseId,String tag);

    ServiceResult removeTag(Long houseId,String tag);
    /**
     * 查询完整房源信息
     * @param id
     * @return
     */
    ServiceResult<HouseDTO> findCompleteOne(Long id);

    ServiceResult update(HouseForm houseForm);

    ServiceResult removePhoto(Long id);

    ServiceResult updateCover(Long coverId, Long targetId);

    ServiceResult updateStatus(Long id, int status);
}
