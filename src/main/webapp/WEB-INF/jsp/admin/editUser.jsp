<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Редактирование пользователя: ${user.username}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Административная панель</h1>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user?action=profile">Главная</a></li>
            <li><a href="${pageContext.request.contextPath}/admin?action=viewUsers">Пользователи</a></li>
        </ul>
    </nav>
</header>

<main>
    <div class="centered-content">
        <h2>Редактирование пользователя: ${user.username}</h2>

        <form action="${pageContext.request.contextPath}/admin" method="post" class="form-container">
            <input type="hidden" name="action" value="updateUser">
            <input type="hidden" name="userId" value="${user.id}">

            <div class="form-group">
                <label>Логин:</label>
                <input type="text" name="username" value="${user.username}" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Email:</label>
                <input type="email" name="email" value="${user.email}" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Роль:</label>
                <select name="role" class="form-control">
                    <option value="USER" ${user.role == 'USER' ? 'selected' : ''}>Пользователь</option>
                    <option value="ADMIN" ${user.role == 'ADMIN' ? 'selected' : ''}>Администратор</option>
                </select>
            </div>

            <div class="form-group">
                <label>Новый пароль:</label>
                <input type="password" name="newPassword" class="form-control" placeholder="Оставьте пустым для сохранения текущего">
            </div>

            <div class="button-group">
                <button type="submit" class="btn-primary">Сохранить изменения</button>
                <a href="${pageContext.request.contextPath}/admin?action=viewUsers" class="btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
</main>

<footer>
    <p>&copy; 2025 Coworking Booking. Все права защищены</p>
</footer>
</body>
</html>