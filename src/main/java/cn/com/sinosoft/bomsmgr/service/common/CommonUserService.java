package cn.com.sinosoft.bomsmgr.service.common;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;

import cn.com.sinosoft.bomsmgr.entity.ge.TAuthUser;
import cn.com.sinosoft.bomsmgr.model.common.LoginUserInfo;
import cn.com.sinosoft.tbf.common.security.exception.SecurityExceptionUserNotValid;
import cn.com.sinosoft.tbf.common.util.StringUtil;
import cn.com.sinosoft.tbf.common.util.security.JwtHelper;
import cn.com.sinosoft.tbf.common.util.security.MD5Util;
import cn.com.sinosoft.tbf.common.util.security.TokenCache;
import cn.com.sinosoft.tbf.dao.BaseDao;
import io.jsonwebtoken.Claims;

/**
 * 公共、通用服务
 *
 * @author <a href="mainto:nytclizy@gmail.com">lizhiyong</a>
 * @since 2017年4月21日
 */
@Service
public class CommonUserService {

	public static final String NAMESPACE_BASE_DEFAULT = "cn.com.sinosoft.common.user.";

	@Resource
	BaseDao baseDao;
	@Resource
	JwtHelper jwtHelper;
	@Resource
	TokenCache tokenCache;

	/**
	 * session中存储用户信息
	 */
	public static final String SESSION_NAME_USERINFO = "loginUserInfo";

	/**
	 * 用户状态-锁定
	 */
	public static final String USER_STATE_LOCKED = "02";

	/**
	 * 用户状态-未锁定
	 */
	public static final String USER_STATE_UNLOCKED = "01";

	/**
	 * 用户默认密码
	 */
	public static final String USER_PWD_DEFAULT = "00000000";

	@Autowired
	HttpServletRequest request;

	/**
	 * 获取请求用户信息-从session中获取
	 *
	 * @return 用户信息
	 */
	public LoginUserInfo getRequestUser() {
		// 先从session中取
		LoginUserInfo userInfo = (LoginUserInfo) request.getSession().getAttribute(SESSION_NAME_USERINFO);
		// 从jwttoken中取
		if (userInfo == null) {
			Claims claims = jwtHelper.parseJWT();
			if (claims == null)
				return null;
			Object userInfoToken = claims.get(SESSION_NAME_USERINFO);
			if (userInfoToken != null) {
				// 从缓存中取出完全的信息
				userInfo = tokenCache.get(jwtHelper.getJWTToken());
			}
		}
		return userInfo;
	}

	/**
	 * 获取请求用户id
	 *
	 * @return 用户id
	 */
	public String getRequestUserId() {
		LoginUserInfo userInfo = getRequestUser();
		if (userInfo == null)
			return null;
		return userInfo.getId();
	}

	/**
	 * 生成token
	 *
	 * @param userInfo
	 * @return
	 */
	public String generateUserToken(LoginUserInfo userInfo) {
		Map<String, Object> userMap = new HashMap<String, Object>();
		userMap.put(SESSION_NAME_USERINFO, userInfo);
		return jwtHelper.createJWT(userMap);
	}

	/**
	 * 验证用户名和密码
	 *
	 * @param userName
	 *            用户名
	 * @param passWord
	 *            密码
	 * @return 是否正确
	 */
	public boolean validUser(String userName, String passWord) throws AuthenticationServiceException {
		if (userName == null || passWord == null) {
			throw new SecurityExceptionUserNotValid("用户名、密码不能为空");
		}
		TAuthUser user = geTAuthUser(userName, passWord);
		if (user == null) {
			throw new SecurityExceptionUserNotValid("用户名或密码错误");
		}
		return true;
	}

	/**
	 * 获取用户列表
	 *
	 * @param params
	 * @return
	 */
	public List<TAuthUser> geTAuthUserList(Map<String, Object> params) {
		List<TAuthUser> users = baseDao.queryList(NAMESPACE_BASE_DEFAULT + "user-list", params);
		return users;
	}

	/**
	 * 获取用户-根据用户名和用户密码
	 *
	 * @param userName
	 *            用户名
	 * @param passWord
	 *            密码
	 * @return 用户基础信息
	 */
	public TAuthUser geTAuthUser(String userName, String passWord) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		params.put("passWord", MD5Util.digestMD5(passWord));
		List<TAuthUser> users = geTAuthUserList(params);
		return users.size() > 0 ? users.get(0) : null;
	}

	/**
	 * 获取用户-根据用户名
	 *
	 * @param userName
	 *            用户名
	 * @return 用户
	 */
	public TAuthUser geTAuthUserByUserName(String userName) {
		if (StringUtil.isEmpty(userName)) {
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userName", userName);
		List<TAuthUser> users = geTAuthUserList(params);
		return users.size() > 0 ? users.get(0) : null;
	}

	/**
	 * 获取用户-根据用户ID
	 *
	 * @param userName
	 *            用户名
	 * @return 用户
	 */
	public TAuthUser geTAuthUserByUserId(String userId) {
		if (userId == null) {
			return null;
		}
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		List<TAuthUser> users = geTAuthUserList(params);
		return users.size() > 0 ? users.get(0) : null;
	}

}
