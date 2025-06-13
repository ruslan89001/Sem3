<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Административная панель пользователей</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Административная панель пользователей</h1>
</header>
<main>
    <h2>Список пользователей</h2>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Имя пользователя</th>
            <th>Электронная почта</th>
            <th>Роль</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="user" items="${users}">
            <tr>
                <td>${user.id}</td>
                <td>${user.username}</td>
                <td>${user.email}</td>
                <td>${user.role}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="deleteUser">
                        <input type="hidden" name="userId" value="${user.id}">
                        <button type="submit">Удалить</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/admin?action=editUser&userId=${user.id}">Редактировать</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
    <h2>Добавить нового пользователя</h2>
    <form action="${pageContext.request.contextPath}/admin" method="post">
        <input type="hidden" name="action" value="addUser">
        <div>
            <label for="username">Имя пользователя:</label>
            <input type="text" id="username" name="username" required>
        </div>
        <div>
            <label for="email">Электронная почта:</label>
            <input type="email" id="email" name="email" required>
        </div>
        <div>
            <label for="password">Пароль:</label>
            <input type="password" id="password" name="password" required>
        </div>
        <div>
            <button type="submit">Добавить</button>
            <a href="${pageContext.request.contextPath}/user?action=profile" class="btn-secondary">Отмена</a>
        </div>
    </form>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>
