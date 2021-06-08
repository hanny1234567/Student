package com.dao.imple;


import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.dao.OrderDao;
import com.dao.ProductDao;
import com.entity.Order;
import com.entity.OrderDetail;
import com.entity.Product;
import com.model.CartInfo;
import com.model.CartLineInfo;
import com.model.CustomerInfo;
import com.model.OrderDetailInfo;
import com.model.OrderInfo;
import com.model.PaginationResult;
//Transactional for Hibernate
@Transactional
public class OrderDaoImpl implements OrderDao {

   @Autowired
   private SessionFactory sessionFactory;
   
   @Autowired
   private ProductDao productDAO;
   private int getMaxOrderNum() 
   {
       String sql = "Select max(o.orderNum) from " + Order.class.getName() + " o ";
       Session session = sessionFactory.getCurrentSession();
       Query query = session.createQuery(sql);
       Integer value = (Integer) query.uniqueResult();
       if (value == null) {
           return 0;
       }
       return value;
   }

	public void saveOrder(CartInfo cartInfo)
	{
		// TODO Auto-generated method stub
		  Session session = sessionFactory.getCurrentSession();
		  
	        int orderNum = this.getMaxOrderNum() + 1;
	        Order order = new Order();
	 
	        order.setId(UUID.randomUUID().toString());
	        order.setOrderNum(orderNum);
	        order.setOrderDate(new Date());
	        order.setAmount(cartInfo.getAmountTotal());
	 
	        CustomerInfo customerInfo = cartInfo.getCustomerInfo();
	        order.setCustomerName(customerInfo.getName());
	        order.setCustomerEmail(customerInfo.getEmail());
	        order.setCustomerPhone(customerInfo.getPhone());
	        order.setCustomerAddress(customerInfo.getAddress());
	 
	        session.persist(order);
	 
	        List<CartLineInfo> lines = cartInfo.getCartLines();
	 
	        for (CartLineInfo line : lines) {
	            OrderDetail detail = new OrderDetail();
	            detail.setId(UUID.randomUUID().toString());
	            detail.setOrder(order);
	            detail.setAmount(line.getAmount());
	            detail.setPrice(line.getProductInfo().getPrice());
	            detail.setQuanity(line.getQuantity());
	 
	            String code = line.getProductInfo().getCode();
	            Product product = this.productDAO.findProduct(code);
	            detail.setProduct(product);
	 
	            session.persist(detail);
	        }
	        cartInfo.setOrderNum(orderNum);
		
	}

public PaginationResult<OrderInfo> listOrderInfo(int page, int maxResult, int maxNavigationPage) 
{
	  String sql = "Select new " + OrderInfo.class.getName()//
              + "(ord.id, ord.orderDate, ord.orderNum, ord.amount, "
              + " ord.customerName, ord.customerAddress, ord.customerEmail, ord.customerPhone) " + " from "
              + Order.class.getName() + " ord "//
              + " order by ord.orderNum desc";
      Session session = this.sessionFactory.getCurrentSession();

      Query query = session.createQuery(sql);

      return new PaginationResult<OrderInfo>(query, page, maxResult, maxNavigationPage);
 }
  public Order findOrder(String orderId) 
  {
      Session session = sessionFactory.getCurrentSession();
      Criteria crit = session.createCriteria(Order.class);
      crit.add(Restrictions.eq("id", orderId));
      return (Order) crit.uniqueResult();
  }

public OrderInfo getOrderInfo(String orderId) 
{
	  Order order = this.findOrder(orderId);
      if (order == null) 
      {
          return null;
      }
      return new OrderInfo(order.getId(), order.getOrderDate(), //
      order.getOrderNum(), order.getAmount(), order.getCustomerName(), //
     order.getCustomerAddress(), order.getCustomerEmail(), order.getCustomerPhone());
}
public List listOrderDetailInfos(String orderId)
{
	  String sql = "Select new " + OrderDetailInfo.class.getName() //
              + "(d.id, d.product.code, d.product.name , d.quanity,d.price,d.amount) "//
              + " from " + OrderDetail.class.getName() + " d "//
              + " where d.order.id = :orderId ";

      Session session = this.sessionFactory.getCurrentSession();

      Query query = session.createQuery(sql);
      query.setParameter("orderId", orderId);

      return query.list();
  }
}

   
  