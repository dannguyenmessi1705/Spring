package com.didan.spring_boot_restapi.jacksonfilter;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ser.FilterProvider;
import com.fasterxml.jackson.databind.ser.impl.SimpleBeanPropertyFilter;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

@RestController
public class FilterController {
	
	
	// STATIC FILTER
	@GetMapping(path = "filter-static")
	public ResponseEntity<?> filterStatic() {
		List<FilterStatic> list = Arrays.asList(new FilterStatic("Didan", 20, "123456"), new FilterStatic("Duy", 21, "123456"));
		return new ResponseEntity<>(list, HttpStatus.OK);
	}
	
	
	// DYNAMIC FILTER
	@GetMapping(path = "filter-dynamic")
	public ResponseEntity<MappingJacksonValue> filterDynamic() {
		List<FilterDynamic> list = Arrays.asList(new FilterDynamic("Didan", 20, "123456"), new FilterDynamic("Duy", 21, "123456"));
		
		// Thêm dữ liệu vào MappingJacksonValue
		MappingJacksonValue mapping = new MappingJacksonValue(list);
		
		// Tạo filter lọc ra những trường cần thiết
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userName", "age"); // Lọc ra userName và age
		
		// Tạo filters cho mappingJackson
		FilterProvider filters = new SimpleFilterProvider().addFilter("FilterDynamic", filter); // FilterDynamic là tên filter đặt trong class FilterDynamic, filter là filter đã tạo
		
		// Set filters cho mappingJackson
		mapping.setFilters(filters); // Set filters cho mappingJackson
		
		return new ResponseEntity<>(mapping, HttpStatus.OK);
	} // Khi truy cập vào /filter-dynamic sẽ trả về dữ liệu như sau: [{"userName":"Didan","age":20},{"userName":"Duy","age":21}]
	
	// NO DYNAMIC FILTER
	@GetMapping(path = "filter-dynamic-2")
	public ResponseEntity<MappingJacksonValue> filterNoDynamic() {
		List<FilterDynamic> list = Arrays.asList(new FilterDynamic("Abc", 20, "123456"), new FilterDynamic("Def", 21, "123456"));
		
		MappingJacksonValue mapping = new MappingJacksonValue(list);
		
		// Tạo filter lọc ra những trường cần thiết
		SimpleBeanPropertyFilter filter = SimpleBeanPropertyFilter.filterOutAllExcept("userName"); // chỉ lọc ra userName
		
		// Tạo filters cho mappingJackson
		FilterProvider filters = new SimpleFilterProvider().addFilter("FilterDynamic", filter); // FilterDynamic là tên filter đặt trong class FilterDynamic, filter là filter đã tạo
		
		// Set filters cho mappingJackson
		mapping.setFilters(filters); // Set filters cho mappingJackson
		
        return new ResponseEntity<>(mapping, HttpStatus.OK);
	} // Khi truy cập vào /filter-no-dynamic sẽ trả về dữ liệu như sau: [{"userName":"Didan"},{"userName":"Duy"}]
}
