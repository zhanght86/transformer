package cn.com.sinosoft.bomsmgr.model.biz;

import java.util.Date;

import cn.com.sinosoft.bomsmgr.entity.ge.TBizDevice;

/**
 * 设备信息
 *
 * @author <a href="mainto:nytclizy@gmail.com">lizhiyong</a>
 * @since 2017年9月18日
 */
public class DeviceInfo extends TBizDevice {

	/** serialVersionUID */
	private static final long serialVersionUID = 1L;

	/**
	 * 创建人描述
	 */
	private String createUserDesc;

	/**
	 * 图片上传人描述
	 */
	private String uploadUserDesc;

	/**
	 * 图片上传时间
	 */
	private Date uploadTime;

	/**
	 * 图片存储路径
	 */
	private String path;

	public String getCreateUserDesc() {
		return createUserDesc;
	}

	public void setCreateUserDesc(String createUserDesc) {
		this.createUserDesc = createUserDesc;
	}

	public String getUploadUserDesc() {
		return uploadUserDesc;
	}

	public void setUploadUserDesc(String uploadUserDesc) {
		this.uploadUserDesc = uploadUserDesc;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public Date getUploadTime() {
		return uploadTime;
	}

	public void setUploadTime(Date uploadTime) {
		this.uploadTime = uploadTime;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

}
