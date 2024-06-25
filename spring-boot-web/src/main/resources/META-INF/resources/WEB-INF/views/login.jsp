<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
	<head>		
		<link href="webjars/bootstrap/5.3.3/css/bootstrap.min.css" rel="stylesheet" />
		<title>Login page</title>
	</head>
	<body>
		<div class="container">		
			<h1>Welcome to login page </h1>
			<pre>${authError}</pre>
			<form method="POST">
				Name: <input type="text" placeholder="username" name="username"/>
				Password: <input tpye="password" placeholder="password" name="password"/>
				<input type="submit" />
			</form>
		</div>
		
		<script src="webjars/bootstrap/5.3.3/js/bootstrap.min.js"></script>
		<script src= "webjars/jquery/3.7.1/jquery.min.js"></script>
	</body>
</html>