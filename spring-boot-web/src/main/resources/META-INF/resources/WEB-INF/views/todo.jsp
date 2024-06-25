<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>   <!-- Thêm taglib để sử dụng form validation -->
<html>
	<head>		
		<link href="webjars/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet" />
		<title>Add Todo page</title>
	</head>
	<body>
		<div class="container">		
			<h1>Add Todo</h1>
			<form:form method="POST" modelAttribute="todo"> <!-- Thêm modelAttribute để binding dữ liệu từ form vào object todo -->
				Description: <form:input type="text" placeholder="description" path="description" required="required"/> <!-- Thêm required để validate dữ liệu -->
				<form:errors path="description" cssClass="text-warning"/> <!-- Thêm form:errors để hiển thị thông báo lỗi nếu có -->
				<form:input type="hidden" path="id"/> <!-- Thêm hidden để không hiển thị trường id -->
				<form:input type="hidden" path="done"/> <!-- Thêm hidden để không hiển thị trường done -->
				<input type="submit" />
			</form:form>
		</div>
		
		<script src="webjars/bootstrap/5.3.3/js/bootstrap.min.js"/>
		<script src= "webjars/jquery/3.7.1/jquery.min.js"/>
	</body>
</html>