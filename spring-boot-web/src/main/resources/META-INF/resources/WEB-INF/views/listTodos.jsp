<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>		
		<link href="webjars/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet" />
		<title>Login page</title>
	</head>
	<body>
		<div class="container">
			<h1>Your list todos</h1>
			<table class="table">
				<thead>
					<tr>
						<th>ID</th>
						<th>Description</th>
						<th>Date</th>
						<th>Is Done?</th>
						<th></th>
						<th></th>
					</tr>
				</thead>
				<tbody>
					<c:forEach items="${todos}" var="todo">
						<tr>
							<td>${todo.id}</td>
							<td>${todo.description}</td>
							<td>${todo.date}</td>
							<td>${todo.done}</td>
							<td><a href="delete-todo?id=${todo.id}" class="btn btn-warning">Delete</a></td> <!-- Thêm link để xóa todo -->
							<td><a href="update-todo?id=${todo.id}" class="btn btn-primary">Update</a></td> <!-- Thêm link để cap nhat todo -->
						</tr>
					</c:forEach>
				</tbody>
			</table>
			<a href="add-todo" class="btn btn-success">Add Todo</a> 
		</div>
		
		<script src="webjars/bootstrap/5.3.3/js/bootstrap.min.js"></script>
		<script src= "webjars/jquery/3.7.1/jquery.min.js"></script>
	</body>
</html>