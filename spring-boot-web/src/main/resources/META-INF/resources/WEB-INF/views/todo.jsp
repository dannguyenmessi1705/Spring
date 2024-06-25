<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>		
		<link href="webjars/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet" />
		<title>Add Todo page</title>
	</head>
	<body>
		<div class="container">		
			<h1>Add Todo</h1>
			<form method="POST">
				Description: <input type="text" placeholder="description" name="description" required/>
				<input type="submit" />
			</form>
		</div>
		
		<script src="webjars/bootstrap/5.3.3/js/bootstrap.min.js"/>
		<script src= "webjars/jquery/3.7.1/jquery.min.js"/>
	</body>
</html>