<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Регистрация</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
    <script>
        function validateForm() {
            const username = document.getElementById("username").value;
            const password = document.getElementById("password").value;
            const email = document.getElementById("email").value;

            if (username === "" || password === "" || email === "") {
                alert("Все поля обязательны для заполнения!");
                return false;
            }
            return true;
        }

        function checkUsername() {
            const username = document.getElementById("username").value;
            if (username.length < 3) return;

            fetch("/coworkingbooking/check-username?username=" + username)
                    .then(response => response.json())
                .then(data => {
                    const statusDiv = document.getElementById("username-status");
                    if (data.exists) {
                        statusDiv.style.color = "red";
                        statusDiv.textContent = "Логин занят!";
                    } else {
                        statusDiv.style.color = "green";
                        statusDiv.textContent = "Логин доступен";
                    }
                });
        }
    </script>
</head>
<body>
<header>
    <h1>Регистрация</h1>
</header>
<main class="centered-content">
    <form action="${pageContext.request.contextPath}/user?action=register" method="post" onsubmit="return validateForm()" >
        <div>
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required onblur="checkUsername()">
            <div id="username-status"></div>
        </div>
        <c:if test="${param.error == 'userExists'}">
            <p style="color: red;">Пользователь с таким именем уже существует. Пожалуйста, выберите другое имя.</p>
        </c:if>

        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
        </div>

        <div>
            <label for="email">Электронная почта:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <c:if test="${param.error == 'emailExists'}">
            <p style="color: red;">Пользователь с таким email уже существует. Пожалуйста, используйте другой email.</p>
        </c:if>

        <div>
            <button type="submit">Зарегистрироваться</button>
        </div>
    </form>
    <p>Уже есть аккаунт? <a href="${pageContext.request.contextPath}/user?action=login">Войдите</a></p>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>