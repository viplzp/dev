package demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import demo.common.vo.DemoVO;

/**
 * 一个简单的Controller样例（ViewResolver + ModelAndView + JSP + EL表达式)
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月2日
 */
@Controller // 一般mvc模式
@RequestMapping("/demo/classic")
public class ClassicDemoController {

	/**
	 * 测试方法：简单输出欢迎用语
	 * 
	 * @param vo
	 * @return
	 */
	@RequestMapping(value = "/hi", method = RequestMethod.POST)
	public ModelAndView sayHi(DemoVO vo) {
		ModelAndView view = new ModelAndView();
		view.setViewName("/demo/classic/hi"); // 跳转页面： 前缀+/demo/classic/hi +后缀
		view.addObject("message", vo.getName());
		return view;
	}

}
