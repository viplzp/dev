package demo;
import org.junit.Test;

import io.flysium.framework.app.AppConfig;

/**
 * 应用配置测试
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 1.0
 */
public class AppTest extends BaseJunitForSpring {

	@Test
	public void test() {
		System.out.println(AppConfig.getInst().getApplicationName());
	}
}
