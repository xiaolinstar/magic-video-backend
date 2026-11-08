package cn.xiaolin.has.service.impl;

import cn.xiaolin.utils.exception.GlobalException;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class MediaKitPlaybackTest {

    @Test
    void publicObjectUrl_isEndpointBucketKeyWithoutQuery() {
        String key = "hls/0123456789abcdef0123456789abcdef/0123456789abcdef0123456789abcdef.m3u8";
        String url = MediaKitServiceImpl.publicObjectUrl("http://localhost:9010/", "transcoded", "/" + key);

        assertEquals("http://localhost:9010/transcoded/" + key, url);
        assertFalse(url.contains("?"));
        assertFalse(url.contains("X-Amz-"));
        assertTrue(url.length() <= 2048);
    }

    @Test
    void ffmpegCommand_startsWithConfiguredBinary() {
        List<String> command = MediaKitServiceImpl.ffmpegCommand(
                "/usr/bin/ffmpeg",
                List.of("-i", "http://localhost:9010/raw/videos/a.mp4", "-f", "hls", "out.m3u8"));

        assertEquals("/usr/bin/ffmpeg", command.get(0));
        assertEquals("-i", command.get(1));
        assertFalse(command.get(0).startsWith("-"));
    }

    @Test
    void ffmpegCommand_rejectsMissingBinary() {
        assertThrows(GlobalException.class,
                () -> MediaKitServiceImpl.ffmpegCommand("  ", List.of("-version")));
    }
}
