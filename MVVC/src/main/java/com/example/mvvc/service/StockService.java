package com.example.mvvc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.example.mvvc.domain.Stock;
import com.example.mvvc.repository.StockRepository;

@Service
public class StockService {
	private final StockRepository stockRepository;
	
	public StockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}
	
	/*
	@Transactional
	public void decrease(Long id, Long quantity) {
		//Stock 조회
		//재고 감소
		//갱신된 값 저장
		Stock stock = stockRepository.findById(id).orElseThrow();
		stock.decrease(quantity);
		
		stockRepository.saveAndFlush(stock);
	}
	*/
	
	/*
	@Transactional
	public synchronized void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id).orElseThrow();
		stock.decrease(quantity);
		
		stockRepository.saveAndFlush(stock);
	}
	*/
	
	@Transactional(propagation = Propagation.REQUIRES_NEW)
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findById(id).orElseThrow();
		stock.decrease(quantity);
		
		stockRepository.saveAndFlush(stock);
	}
}
