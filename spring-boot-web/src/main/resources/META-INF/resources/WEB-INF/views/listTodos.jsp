<%@ include file="common/header.jspf" %>
		<%@ include file="common/navigation.jspf" %>
		
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
		
<%@ include file="common/footer.jspf" %>