package com.didan.test_mokito.business;

public class Business {
	private DataService dataService;
	
	public Business(DataService dataService) {
		this.dataService = dataService;
	}
	
	public int findMax() {
		int [] datas = dataService.getAllDatas();
		int max = Integer.MIN_VALUE;
		for (int value : datas) {
			max = max >= value ? max : value;
		}
		return max;
	}

}

interface DataService {
	int [] getAllDatas();
}