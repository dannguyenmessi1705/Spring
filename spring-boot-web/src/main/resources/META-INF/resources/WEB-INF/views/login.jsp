<%@ include file="common/header.jspf" %>
		<div class="container">		
			<h1>Welcome to login page </h1>
			<pre>${authError}</pre>
			<form method="POST">
				Name: <input type="text" placeholder="username" name="username"/>
				Password: <input tpye="password" placeholder="password" name="password"/>
				<input type="submit" />
			</form>
		</div>
		
<%@ include file="common/footer.jspf" %>