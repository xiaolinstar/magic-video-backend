package cn.xiaolin.core.service;

import cn.xiaolin.core.domain.dto.ResourceReqDto;
import cn.xiaolin.core.domain.entity.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author xingxiaolin xing.xiaolin@foxmail.com
 * @Description 视频资源测试类
 * @create 2025/3/11
 */
@SpringBootTest
@ActiveProfiles("dev")
class ResourceServiceTest {


    @Autowired
    private ResourceService resourceService;


    @Test
    public void testPostResource() {
        // 数据库中长度不够 varchar(255)
        String name = "Otis & Ruby";
        String md5 = "Md5-Otis-Ruby";
        String avatar = "http://localhost:9010/magic-image/Otis%20%26%20Ruby.jpg?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20250311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250311T151433Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=fb8e3763cbe82d9e6788d4e513125424acb2b5fad880816018f7a6f43e0fdf0f";
        String mp4 = "http://localhost:9010/magic-video/Md5-Otis-Ruby.mp4?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=RzQywv1gIcgXeSSMMeNz%2F20250311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250311T144616Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=3cb3533daee434332cf65be33a985bfe746db231f58ea3dafb1a4127092fb210";
        String mpd = "http://localhost:9020/magic-dash/Md5-Otis-Ruby.mpd?X-Amz-Algorithm=AWS4-HMAC-SHA256&X-Amz-Credential=bnY5rNCWoZUXNSk7CcR0%2F20250311%2Fus-east-1%2Fs3%2Faws4_request&X-Amz-Date=20250311T150451Z&X-Amz-Expires=604800&X-Amz-SignedHeaders=host&X-Amz-Signature=5b010c1ba7a215abe7020beb34810e54c42002b37698eece6b9639442c8ab628";
        String title = "Netflix 剧集《性爱自修室》，Otis & Ruby";
        String description = "性爱自修室，非官配 CP：Otis & Ruby";

        ResourceReqDto resourceReqDto = ResourceReqDto.builder()
                .name(name)
                .mp4(mp4)
                .md5(md5)
                .avatar(avatar)
                .mpd(mpd)
                .title(title)
                .description(description)
                .build();

        Optional<Resource> resource = resourceService.saveAndReturn(resourceReqDto);
        resource.ifPresent(System.out::println);
        List<Resource> resources = resourceService.list();
        assertNotNull(resources);
        resources.forEach(System.out::println);
    }


}