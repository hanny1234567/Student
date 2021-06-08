package com.dao;

import com.entity.Product;
import com.model.PaginationResult;
import com.model.ProductInfo;

public interface ProductDao {

   
   
   public Product findProduct(String code);
   
   public ProductInfo findProductInfo(String code) ;
 
   
   public PaginationResult<ProductInfo> queryProducts(int page,
                      int maxResult, int maxNavigationPage  );
   
   public PaginationResult<ProductInfo> queryProducts(int page, int maxResult,
                      int maxNavigationPage, String likeName);

   public void save(ProductInfo productInfo);
   
}