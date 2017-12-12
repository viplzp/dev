package demo.common.vo;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;

import io.flysium.framework.Consts;

/**
 * 用户信息
 * 
 * @author SvenAugustus(蔡政滦) e-mail: SvenAugustus@outlook.com
 * @version 2017年3月5日
 */
public class User implements java.io.Serializable {

	private static final long serialVersionUID = 3454949194164468966L;

	private String user_id;
	private String user_name;
	private String password;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Consts.DatePattern.DATE_PATTERN_DEFAULT, timezone = Consts.TimeZone.TIME_ZONE_CHINA)
	private Date eff_date;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = Consts.DatePattern.DATE_PATTERN_DEFAULT, timezone = Consts.TimeZone.TIME_ZONE_CHINA)
	private Date exp_date;
	private String status_cd;
	private Date create_date;
	private Date status_date;

	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_name() {
		return user_name;
	}
	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public Date getEff_date() {
		return eff_date;
	}
	public void setEff_date(Date eff_date) {
		this.eff_date = eff_date;
	}
	public Date getExp_date() {
		return exp_date;
	}
	public void setExp_date(Date exp_date) {
		this.exp_date = exp_date;
	}
	public String getStatus_cd() {
		return status_cd;
	}
	public void setStatus_cd(String status_cd) {
		this.status_cd = status_cd;
	}
	public Date getCreate_date() {
		return create_date;
	}
	public void setCreate_date(Date created_date) {
		this.create_date = created_date;
	}
	public Date getStatus_date() {
		return status_date;
	}
	public void setStatus_date(Date status_date) {
		this.status_date = status_date;
	}

}
