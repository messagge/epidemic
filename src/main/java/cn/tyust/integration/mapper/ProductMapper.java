package cn.tyust.integration.mapper;

import cn.tyust.integration.pojo.Product;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ProductMapper {
    //增加
    void addProduct(Product product);

    //删 根据id
    int deleteProduct(int id);

    //改
    int updateProduct(Product product);

    //查询所有商品
    List<Product> queryAllProduct();

    //根据名称查询商品
    List<Product> queryProductByName(String name);

    Product queryProductById(int id);

}
