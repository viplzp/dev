package demo.servlet;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import demo.common.service.IDemoService;
import demo.common.vo.DemoVO;
import io.flysium.framework.util.SpringContextUtils;

/**
 * 一个简单的Servlet样例，仅做测试spring容器是否正常工作。
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月2日
 */
public class DemoServlet extends HttpServlet {

	private static final long serialVersionUID = 1991518552839121295L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doPost(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		IDemoService mybean = SpringContextUtils.getBean("demoService");;
		String result = mybean.sayHi(new DemoVO("SvenAugustus"));
		resp.getOutputStream().print(result);
	}

}
