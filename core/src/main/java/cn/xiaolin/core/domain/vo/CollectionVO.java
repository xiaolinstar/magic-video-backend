package cn.xiaolin.core.domain.vo;

import cn.xiaolin.core.domain.dto.VideoItem;
import cn.xiaolin.core.domain.entity.Collection;
import lombok.*;

import java.util.List;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description
 * @create 2025/6/8
 */
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class CollectionVO extends Collection {
     private List<VideoItem> items;
     private Integer releaseYear;
     private List<Integer> relatedCollections;

     public CollectionVO(Collection collection) {
          this.setId(collection.getId());
          this.setType(collection.getType());
          this.setCoverImage(collection.getCoverImage());
          this.setTitle(collection.getTitle());
          this.setDescription(collection.getDescription());
     }
}
