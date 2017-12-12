package io.flysium.framework.message;

/**
 * 回参结果
 */
public class ResponseResult extends Response {

	private static final long serialVersionUID = -1956054023523857352L;

	protected Object result;

	/**
	 * 构造器
	 */
	public ResponseResult() {
		super();
	}

	/**
	 * 构造器
	 * 
	 * @param res_code
	 * @param res_message
	 */
	public ResponseResult(String res_code, String res_message) {
		super(res_code, res_message);
	}

	/**
	 * 构造器
	 * 
	 * @param codeInfo
	 */
	public ResponseResult(CodeInfo codeInfo) {
		super(codeInfo);
	}

	/**
	 * 获取回参结果
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <T extends Object> T getResult() {
		return (T) result;
	}

	/**
	 * 设置回参结果
	 * 
	 * @param result
	 */
	public void setResult(Object result) {
		this.result = result;
	}
}