package demo;

import javax.sql.DataSource;

import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Junit加载Spring容器作单元测试
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath*:webconfig/service-all.xml"})
public class BaseJunitForSpring {

	@BeforeClass
	public static void beforeClass() throws Exception {
		ClassPathXmlApplicationContext app = new ClassPathXmlApplicationContext("classpath*:spring-junit-jndi.xml");

		DataSource ds = (DataSource) app.getBean("appJunitDataSource");
		DataSource reserve_ds = (DataSource) app.getBean("reserveJunitDataSource");
		DataSource log_ds = (DataSource) app.getBean("logJunitDataSource");

		/**
		 * 绑定jndi
		 */
		SimpleNamingContextBuilder builder = new SimpleNamingContextBuilder();
		builder.bind("java:comp/env/jdbc/appdb", ds);// tomcat
		builder.bind("java:comp/env/jdbc/reservedb", reserve_ds);// tomcat
		builder.bind("java:comp/env/jdbc/logdb", log_ds);// tomcat

		// builder.bind("jdbc/appdb", ds);//jetty
		// builder.bind("jdbc/reservedb", reserve_ds);//jetty
		// builder.bind("jdbc/logdb", log_ds);//jetty

		builder.activate();

		app.close();
	}

}
