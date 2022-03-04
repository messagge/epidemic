package cn.tyust.integration.service;

import com.github.pagehelper.PageInfo;
import cn.tyust.integration.pojo.Product;

public interface ProductService {

    PageInfo<Product> queryAllProduct(int page,int pageSize);

    void addProduct(Product product);

    Product queryProductById(int id);

    int updateProduct(Product product);

}
