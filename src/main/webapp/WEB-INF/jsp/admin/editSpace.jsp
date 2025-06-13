<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Редактирование: ${space.name}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Административная панель</h1>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user?action=profile">Главная</a></li>
            <li><a href="${pageContext.request.contextPath}/admin?action=viewSpaces">Пространства</a></li>
        </ul>
    </nav>
</header>

<main>
    <div class="centered-content">
        <h2>Редактирование: ${space.name}</h2>

        <form action="${pageContext.request.contextPath}/admin/spaces" method="post" enctype="multipart/form-data" class="form-container">
            <input type="hidden" name="action" value="updateSpace">
            <input type="hidden" name="spaceId" value="${space.id}">

            <div class="form-group">
                <label>Текущее изображение:</label>
                <c:if test="${not empty space.image}">
                    <img src="${pageContext.request.contextPath}/${space.image}" alt="${space.name}" class="space-image">
                </c:if>
            </div>

            <div class="form-group">
                <label>Новое изображение:</label>
                <input type="file" name="image" accept="image/jpeg, image/png" class="form-control">
            </div>

            <div class="form-group">
                <label>Название:</label>
                <input type="text" name="name" value="${space.name}" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Описание:</label>
                <textarea name="description" class="form-control" rows="4" required>${space.description}</textarea>
            </div>

            <div class="form-group">
                <label>Цена:</label>
                <input type="number" step="0.01" name="price" value="${space.price}" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Расположение:</label>
                <input type="text" name="location" value="${space.location}" class="form-control" required>
            </div>

            <div class="form-group">
                <label>Доступность:</label>
                <select name="availability" class="form-control">
                    <option value="true" ${space.availability ? 'selected' : ''}>Доступно</option>
                    <option value="false" ${!space.availability ? 'selected' : ''}>Недоступно</option>
                </select>
            </div>

            <div class="button-group">
                <button type="submit" class="btn-primary">Сохранить изменения</button>
                <a href="${pageContext.request.contextPath}/admin?action=viewSpaces" class="btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
</main>

<footer>
    <p>&copy; 2025 Coworking Booking. Все права защищены</p>
</footer>
</body>
</html>