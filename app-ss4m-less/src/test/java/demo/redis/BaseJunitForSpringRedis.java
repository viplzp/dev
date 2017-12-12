package demo.redis;

import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import demo.BaseJunitForSpring;

/**
 * Junit加载Spring容器作单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:webconfig/service-all.xml", "classpath*:spring-junit-redis.xml"})
public class BaseJunitForSpringRedis extends BaseJunitForSpring {

}
