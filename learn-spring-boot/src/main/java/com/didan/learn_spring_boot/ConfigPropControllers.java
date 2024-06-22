package com.didan.learn_spring_boot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConfigPropControllers {
	@Autowired
	ConfigProp configure; // Inject Bean ConfigProp vào Controller

	@RequestMapping("/configure")
	public ConfigProp getConfig() {
		return configure; // Trả về Bean ConfigProp (Ban đầu đã được gán giá trị từ file
							// application.properties)
	}

}
