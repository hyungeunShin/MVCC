package com.example.mvvc.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.example.mvvc.domain.Stock;
import com.example.mvvc.repository.StockRepository;

@SpringBootTest
class StockServiceTest {
//	@Autowired
//	private StockService stockService;
	
	@Autowired
	private PessimisticLockStockService pessimisticLockStockService;
	
	@Autowired
	private StockRepository stockRepository;
	
	@BeforeEach
	public void before() {
		stockRepository.saveAndFlush(new Stock(1L, 100L));
	}
	
//	@Test
//	public void 재고감소() {
//		stockService.decrease(1L, 1L);
//		
//		Stock stock = stockRepository.findById(1L).orElseThrow();
//		assertEquals(99, stock.getQuantity());
//	}
	
	@Test
	public void 동시에_100개_요청() throws InterruptedException {
		int threadCount = 100;
		
		//ExecutorService : 병렬 작업 시 여러 개의 작업을 효율적으로 처리하기 위해 제공되는 JAVA 라이브러리
		//인자 개수만큼 고정된 쓰레드풀을 생성
		ExecutorService executorService = Executors.newFixedThreadPool(32);
		
		//쓰레드를 N개 실행했을 때 일정 개수의 쓰레드가 모두 끝날 때까지 기다려야지만 다음으로 진행할 수 있거나 
		//다른 쓰레드를 실행시킬 수 있는경우 사용
		CountDownLatch latch = new CountDownLatch(threadCount);
		
		for(int i = 0; i < threadCount; i++) {
			//submit(() -> { })은 멀티쓰레드로 처리할 작업을 예약
			executorService.submit(() -> {
				try {
					//stockService.decrease(1L, 1L);
					pessimisticLockStockService.decrease(1L, 1L);
				} finally {
					//각 쓰레드는 마지막에 자신이 실행 완료했음을 알려준다.
					latch.countDown();
				}
			});
		}
		
		//현재 메소드가 실행중인 메인쓰레드는 더 이상 진행하지 않고
		//CountDownLatch의 count값이 0이 될 때까지 기다린다.
		latch.await();
		
		Stock stock = stockRepository.findById(1L).orElseThrow();
		assertEquals(0, stock.getQuantity());
	}
	
	@AfterEach
	public void after() {
		stockRepository.deleteAll();
	}
}
