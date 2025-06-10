package com.example.demo.entity.generator;

import org.hibernate.HibernateException;
import org.hibernate.engine.spi.SharedSessionContractImplementor;
import org.hibernate.id.IdentifierGenerator;

import java.util.UUID;

public class BookRefGenerator implements IdentifierGenerator {

	@Override
	public Object generate(SharedSessionContractImplementor session, Object object) throws HibernateException {
		String randomPart = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 5);

		return "G" + randomPart.toUpperCase();
	}
}