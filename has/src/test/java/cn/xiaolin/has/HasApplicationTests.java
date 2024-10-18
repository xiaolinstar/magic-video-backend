package cn.xiaolin.has;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("dev")
class HasApplicationTests {


	@Test
	public void test() {
		System.out.println("Hello Hls");
	}

}
