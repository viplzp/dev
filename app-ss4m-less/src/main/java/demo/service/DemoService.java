package demo.service;

import org.springframework.stereotype.Service;

import demo.common.service.IDemoService;
import demo.common.vo.DemoVO;

/**
 * 一个简单的Service样例
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月2日
 */
@Service
public class DemoService implements IDemoService {

	@Override
	public String sayHi(DemoVO vo) {
		return (vo == null) ? "Hi,who are you?" : ("Hi," + vo.getName());
	}
}
