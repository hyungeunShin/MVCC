package com.example.mvvc.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mvvc.domain.Stock;
import com.example.mvvc.repository.StockRepository;

@Service
public class OptimisticLockStockService {
	private final StockRepository stockRepository;
	
	public OptimisticLockStockService(StockRepository stockRepository) {
		this.stockRepository = stockRepository;
	}
	
	@Transactional
	public void decrease(Long id, Long quantity) {
		Stock stock = stockRepository.findByIdWithOptimisticLock(id);
		
		stock.decrease(quantity);
		
		stockRepository.save(stock);
	}
}
