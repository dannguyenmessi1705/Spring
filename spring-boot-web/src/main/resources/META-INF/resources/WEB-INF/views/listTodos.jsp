<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>		
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
	</body>
</html>