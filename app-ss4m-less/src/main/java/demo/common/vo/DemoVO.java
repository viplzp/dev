package demo.common.vo;

/**
 * 一个简单的VO样例
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月2日
 */
public class DemoVO implements java.io.Serializable {

	private static final long serialVersionUID = 579798238682106908L;

	private String name;

	public DemoVO() {
		super();
	}

	public DemoVO(String name) {
		super();
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
