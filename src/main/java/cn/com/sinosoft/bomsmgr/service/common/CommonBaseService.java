/**
 *
 *
 * @author <a href="mailto:nytclizy@gmail.com">李志勇</a>
 * @date 2014-11-12
 */
package cn.com.sinosoft.bomsmgr.service.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import cn.com.sinosoft.bomsmgr.entity.ge.TAuthMenufun;
import cn.com.sinosoft.bomsmgr.entity.ge.TBaseDics;
import cn.com.sinosoft.bomsmgr.model.base.MFTreeVO;
import cn.com.sinosoft.bomsmgr.service.system.user.SystemUserService;
import cn.com.sinosoft.tbf.common.util.StringUtil;
import cn.com.sinosoft.tbf.dao.BaseDao;

/**
 * 通用服务
 *
 * @author <a href="mainto:nytclizy@gmail.com">lizhiyong</a>
 * @since 2017年5月2日
 */
@Service
public class CommonBaseService {

	public static final String NAMESPACE_BASE_DEFAULT = "cn.com.sinosoft.common.base.";

	@Resource
	BaseDao dao;
	@Resource
	SystemUserService systemUserService;

	/**
	 * 获取码表集合
	 *
	 * @param types
	 * @return
	 */
	@Cacheable(value = "dics", key = "'getInitCodes' + #type")
	public List<List<TBaseDics>> getInitCodes(String[] types) {
		if (types == null || types.length == 0) {
			return new ArrayList<List<TBaseDics>>();
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("codeTypes", types);
			List<TBaseDics> items = dao.queryList(NAMESPACE_BASE_DEFAULT + "code-list", params);
			return handleInitCodesResult(items);
		}
	}

	/**
	 * 处理码表结果集
	 *
	 * @param items
	 * @return
	 */
	private List<List<TBaseDics>> handleInitCodesResult(List<TBaseDics> items) {
		Map<String, Object> temp = new HashMap<String, Object>();
		Map<String, List<TBaseDics>> retTemp = new HashMap<String, List<TBaseDics>>();
		List<List<TBaseDics>> ret = new ArrayList<List<TBaseDics>>();
		for (TBaseDics item : items) {
			String codeType = item.getCodeType();
			if (temp.get(codeType) == null) {
				temp.put(codeType, true);
				retTemp.put(codeType, new ArrayList<TBaseDics>());
			}
			List<TBaseDics> list = retTemp.get(codeType);
			list.add(item);
		}
		for (String key : retTemp.keySet()) {
			ret.add(retTemp.get(key));
		}
		retTemp = null;
		temp = null;
		return ret;
	}

	/**
	 * 获取某类型码表
	 *
	 * @param type
	 * @return
	 * @author <a href="mailto:nytclizy@gmail.com">李志勇</a>
	 */
	@Cacheable(value = "dics", key = "'getTypeCode' + #type")
	public List<TBaseDics> getTypeCode(String type) {
		if (StringUtil.isEmpty(type)) {
			return new ArrayList<TBaseDics>();
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("codeTypes", new String[]{type});
			List<TBaseDics> items = dao.queryList(NAMESPACE_BASE_DEFAULT + "code-list", params);
			return items;
		}
	}

	/**
	 * 获取某种类型码表-不缓存
	 *
	 * @param type
	 * @return
	 */
	public List<TBaseDics> getTypeCodeNotCache(String type) {
		if (StringUtil.isEmpty(type)) {
			return new ArrayList<TBaseDics>();
		} else {
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("codeTypes", new String[]{type});
			List<TBaseDics> items = dao.queryList(NAMESPACE_BASE_DEFAULT + "code-list", params);
			return items;
		}
	}

	/**
	 * 获取用户所有具有权限的菜单功能点
	 *
	 * @param userId
	 *            用户id
	 * @param type
	 *            类型：1-菜单、2-功能点
	 * @return
	 * @author <a href="mailto:nytclizy@gmail.com">李志勇</a>
	 */
	public MFTreeVO getUserMF(String userId, String type) {
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("userId", userId);
		params.put("type", type);
		List<TAuthMenufun> items = dao.queryList(NAMESPACE_BASE_DEFAULT + "menu-fun-list", params);
		// 将菜单和功能点组装成一棵树
		return renderMenusAndFuns(items);
	}

	// 将菜单以及功能点融合成一棵树
	public MFTreeVO renderMenusAndFuns(List<TAuthMenufun> mfs) {
		List<TAuthMenufun> listMFVO = new ArrayList<TAuthMenufun>();
		for (TAuthMenufun mf : mfs) {
			listMFVO.add(mf);
		}
		// 获取0级菜单
		List<TAuthMenufun> firstlevelMenus = getFirstlevelMenus(mfs);
		for (TAuthMenufun mf : firstlevelMenus) {// 循环所有初级菜单
			return handleOneLevelMF(mf, mfs);
		}
		return null;
	}

	/**
	 *
	 * 获取初级菜单-父菜单为空或者level为0
	 *
	 */
	private List<TAuthMenufun> getFirstlevelMenus(List<TAuthMenufun> allMenus) {
		List<TAuthMenufun> ret = new ArrayList<TAuthMenufun>();
		for (TAuthMenufun menu : allMenus) {
			if ("0".equals(menu.getMfLevel()) || menu.getPmfId() == null) {
				ret.add(menu);
			}
		}
		return ret;
	}

	/**
	 * 递归处理某一个菜单
	 */
	private MFTreeVO handleOneLevelMF(TAuthMenufun mf, List<TAuthMenufun> mfs) {
		MFTreeVO mfTreeVO = new MFTreeVO();
		mfTreeVO.setMfVO(mf);
		for (TAuthMenufun item : mfs) {
			if (mf.getId().equals(item.getPmfId())) {
				MFTreeVO newMfTree = handleOneLevelMF(item, mfs);
				mfTreeVO.addChild(newMfTree);
			}
		}
		return mfTreeVO;
	}

}
