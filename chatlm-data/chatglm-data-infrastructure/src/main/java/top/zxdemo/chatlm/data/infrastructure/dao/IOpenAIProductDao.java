package top.zxdemo.chatlm.data.infrastructure.dao;

import top.zxdemo.chatlm.data.infrastructure.po.OpenAIProductPO;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * @author ZhangXing zxdemo.top
 * @description 商品Dao
 */
@Mapper
public interface IOpenAIProductDao {

    OpenAIProductPO queryProductByProductId(Integer productId);

    List<OpenAIProductPO> queryProductList();

}
