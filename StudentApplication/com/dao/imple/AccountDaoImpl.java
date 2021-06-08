package com.dao.imple;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import com.dao.AccountDao;
import com.entity.Account;
 
// Transactional for Hibernate
@Transactional
public class AccountDaoImpl implements AccountDao {
    
    @Autowired
    private SessionFactory sessionFactory;

	public Account findAccount(String userName) 
	{
		// TODO Auto-generated method stub
		Session session = sessionFactory.getCurrentSession();
        Criteria crit = session.createCriteria(Account.class);
        crit.add(Restrictions.eq("userName", userName));
        return (Account) crit.uniqueResult();
	}
 

 
}