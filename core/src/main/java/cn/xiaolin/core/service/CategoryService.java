package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.CategoryReqDto;
import cn.xiaolin.core.domain.entity.Category;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【category(视频分类)】的数据库操作Service
 * @create 2023-07-30 17:10:49
 */
public interface CategoryService extends IService<Category> {

    Optional<Category> findItemById(Long id);
    Optional<Category> deleteAndReturnById(Long id);

    Optional<Category> updateAndReturn(CategoryReqDto dto);

    Optional<Category> saveAndReturn(CategoryReqDto dto);
}
