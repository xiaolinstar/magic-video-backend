package cn.xiaolin.core.service.impl;

import cn.xiaolin.core.domain.dto.CategoryReqDto;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import cn.xiaolin.core.domain.entity.Category;
import cn.xiaolin.core.service.CategoryService;
import cn.xiaolin.core.domain.mapper.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Objects;
import java.util.Optional;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 针对表【category(视频分类)】的数据库操作Service实现
 * @create 2023-07-30 17:10:49
 */
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category>
    implements CategoryService{

    @Override
    public Optional<Category> findItemById(Long id) {
        return Optional.ofNullable(getById(id));
    }

    @Override
    public Optional<Category> deleteAndReturnById(Long id) {
        Optional<Category> result = findItemById(id);
        removeById(id);
        return result;
    }

    @Override
    public Optional<Category> updateAndReturn(CategoryReqDto dto) {
        if (Objects.isNull(dto.getId())) {
            throw new IllegalArgumentException("Id cannot be null");
        }

        LambdaUpdateWrapper<Category> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(Objects.nonNull(dto.getName()), Category::getName, dto.getName())
                .eq(Category::getId, dto.getId());
        boolean updated = update(updateWrapper);
        return updated ? findItemById(dto.getId()) : Optional.empty();
    }

    @Override
    public Optional<Category> saveAndReturn(CategoryReqDto dto) {
        Category category = Category.builder()
                .id(dto.getId())
                .name(dto.getName())
                .build();
        boolean saved = false;
        try {
            saved = this.save(category);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return saved ? Optional.of(category) : Optional.empty();
    }
}




