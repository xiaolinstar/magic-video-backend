package cn.xiaolin.multimedia.service.impl;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VideoPlaybackUrlTest {

    @Test
    void publicObjectUrl_isEndpointBucketKeyWithoutQuery() {
        String url = VideoServiceImpl.publicObjectUrl(
                "http://localhost:9010/", "raw", "/videos/0123456789abcdef0123456789abcdef.mp4");

        assertEquals("http://localhost:9010/raw/videos/0123456789abcdef0123456789abcdef.mp4", url);
        assertFalse(url.contains("?"));
        assertTrue(url.length() <= 2048);
        assertTrue(url.startsWith("http://localhost:9010/raw/videos/"));
    }
}
