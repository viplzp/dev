package demo.common.vo;

import java.util.Date;

/**
 * 系统操作日志
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月6日
 */
public class SysOperLog implements java.io.Serializable {

	private static final long serialVersionUID = 1851620244580224573L;

	private String log_id;
	private String user_id;
	private String action_type;
	private String action_desc;
	private String result_code;
	private String result_msg;
	private String client_ip;
	private Date log_date;

	public String getLog_id() {
		return log_id;
	}
	public void setLog_id(String log_id) {
		this.log_id = log_id;
	}
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getAction_type() {
		return action_type;
	}
	public void setAction_type(String action_type) {
		this.action_type = action_type;
	}
	public String getAction_desc() {
		return action_desc;
	}
	public void setAction_desc(String action_desc) {
		this.action_desc = action_desc;
	}
	public String getResult_code() {
		return result_code;
	}
	public void setResult_code(String result_code) {
		this.result_code = result_code;
	}
	public String getResult_msg() {
		return result_msg;
	}
	public void setResult_msg(String result_msg) {
		this.result_msg = result_msg;
	}
	public String getClient_ip() {
		return client_ip;
	}
	public void setClient_ip(String client_ip) {
		this.client_ip = client_ip;
	}
	public Date getLog_date() {
		return log_date;
	}
	public void setLog_date(Date log_date) {
		this.log_date = log_date;
	}

}
