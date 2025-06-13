<!-- login.jsp -->
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Вход</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <script>
        function validateLoginForm() {
            var username = document.getElementById("username").value;
            var password = document.getElementById("password").value;

            if (username === "" || password === "") {
                alert("Все поля обязательны для заполнения!");
                return false;
            }
            return true;
        }
    </script>
</head>
<body>
<header>
    <h1>Вход</h1>
</header>
<main>
    <form action="${pageContext.request.contextPath}/user" method="post" onsubmit="return validateLoginForm()">
        <input type="hidden" name="action" value="login">
        <div>
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <button type="submit">Войти</button>
        </div>
    </form>
    <p>Нет аккаунта? <a href="${pageContext.request.contextPath}/user?action=register">Зарегистрируйтесь</a></p>
    <c:if test="${not empty param.error}">
        <p style="color: red;">Неверное имя пользователя или пароль. Попробуйте снова.</p>
    </c:if>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>
