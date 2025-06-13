<!-- index.jsp -->
<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Добро пожаловать в Coworking Booking</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Добро пожаловать в Coworking Booking</h1>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user?action=register">Регистрация</a></li>
            <li><a href="${pageContext.request.contextPath}/user?action=login">Вход</a></li>
        </ul>
    </nav>
</header>
<main>
    <p>Здесь вы можете забронировать место в коворкинге, оставить отзывы и управлять своими бронированиями.</p>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>
