package com.example.mvvc.facade;

import org.springframework.stereotype.Component;

import com.example.mvvc.repository.RedisLockRepository;
import com.example.mvvc.service.StockService;

@Component
public class LettuceLockStockFacade {
	private final RedisLockRepository redisLockRepository;
	
	private final StockService stockService;

	public LettuceLockStockFacade(RedisLockRepository redisLockRepository, StockService stockService) {
		this.redisLockRepository = redisLockRepository;
		this.stockService = stockService;
	}
	
	public void decrease(Long id, Long quantity) throws InterruptedException {
		while(!redisLockRepository.lock(id)) {
			Thread.sleep(100);
		}
		
		try {
			stockService.decrease(id, quantity);
		} finally {
			redisLockRepository.unlock(id);
		}
	}
}
