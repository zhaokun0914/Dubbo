package com.fortunebill.gmail.service;

import com.fortunebill.gmail.bean.UserAddress;

import java.util.List;

/**
 * 用户服务
 * @author kevin
 * @date 2021年12月07日 21:18
 */
public interface UserService {
	
	/**
	 * 按照用户id返回所有的收货地址
	 * @param userId
	 * @return
	 */
	public List<UserAddress> getUserAddressList(String userId);

}
