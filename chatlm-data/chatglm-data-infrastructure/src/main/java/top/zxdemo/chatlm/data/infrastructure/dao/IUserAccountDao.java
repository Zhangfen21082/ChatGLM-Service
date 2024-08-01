package top.zxdemo.chatlm.data.infrastructure.dao;

import top.zxdemo.chatlm.data.infrastructure.po.UserAccountPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author ZhangXing zxdemo.top
 * @description 用户账户DAO
 */
@Mapper
public interface IUserAccountDao {

    int subAccountQuota(String openid);

    UserAccountPO queryUserAccount(String openid);

    int addAccountQuota(UserAccountPO userAccountPOReq);

    void insert(UserAccountPO userAccountPOReq);

}
