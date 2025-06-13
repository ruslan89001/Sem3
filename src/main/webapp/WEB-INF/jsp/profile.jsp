<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Профиль</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Профиль</h1>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user?action=logout">Выход</a></li>
            <c:if test="${user.role == 'admin'}">
                <li><a href="${pageContext.request.contextPath}/admin?action=viewUsers">Администрирование пользователей</a></li>
                <li><a href="${pageContext.request.contextPath}/admin?action=viewSpaces">Администрирование пространств</a></li>
                <li><a href="${pageContext.request.contextPath}/admin?action=viewBookings">Администрирование бронирований</a></li>
            </c:if>
            <c:if test="${user.role != 'admin'}">
                <li><a href="${pageContext.request.contextPath}/spaces">Пространства</a></li>
                <li><a href="${pageContext.request.contextPath}/bookings">Мои бронирования</a></li>
                <li><a href="${pageContext.request.contextPath}/reviews">Отзывы</a></li>
            </c:if>
        </ul>
    </nav>
</header>
<main class="centered-content">
    <h2>Добро пожаловать, ${user.username}</h2>
    <form action="${pageContext.request.contextPath}/user?action=editProfile" method="post">
        <div class="form-group">
            <label>Имя пользователя:</label>
            <input type="text" name="username" value="${user.username}" class="form-control">
        </div>

        <div class="form-group">
            <label>Электронная почта:</label>
            <input type="email" name="email" value="${user.email}" class="form-control">
        </div>

        <div class="form-group">
            <label>Новый пароль (оставьте пустым, если не меняется):</label>
            <input type="password" name="newPassword" class="form-control">
        </div>

        <div class="button-group">
            <button type="submit" class="btn-primary">Сохранить изменения</button>
        </div>
    </form>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>
