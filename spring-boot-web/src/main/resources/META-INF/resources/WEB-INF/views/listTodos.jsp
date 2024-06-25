<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>		
		<link href="webjars/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet" />
		<title>Login page</title>
	</head>
	<body>
		<div>Welcome to ${username} Todo</div>
		<hr />
		<h1>Your list todos</h1>
		<table>
			<thead>
				<tr>
					<th>ID</th>
					<th>Description</th>
					<th>Date</th>
					<th>Is Done?</th>
				</tr>
			</thead>
			<tbody>
				<c:forEach items="${todos}" var="todo">
					<tr>
						<td>${todo.id}</td>
						<td>${todo.description}</td>
						<td>${todo.date}</td>
						<td>${todo.done}</td>
					</tr>
				</c:forEach>
			</tbody>
		</table>
		
		<script src="webjars/bootstrap/5.3.3/js/bootstrap.min.js"/>
		<script src= "webjars/jquery/3.7.1/jquery.min.js"/>
	</body>
</html>