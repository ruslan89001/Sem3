<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <title>Административная панель пространств</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Административная панель пространств</h1>
</header>

<!-- Основная зона с увеличенной шириной -->
<main style="max-width: 1200px; margin: 20px auto; padding: 25px;">
    <h2>Список пространств</h2>

    <table style="width: 100%; table-layout: fixed;">

        <colgroup>
            <col style="width: 5%">   <!-- ID -->
            <col style="width: 15%">   <!-- Название -->
            <col style="width: 25%">   <!-- Описание -->
            <col style="width: 10%">   <!-- Цена -->
            <col style="width: 15%">   <!-- Расположение -->
            <col style="width: 15%">   <!-- Изображение -->
            <col style="width: 10%">   <!-- Доступность -->
            <col style="width: 15%">   <!-- Действия -->
        </colgroup>

        <thead>
        <tr>
            <th>ID</th>
            <th>Название</th>
            <th>Описание</th>
            <th>Цена</th>
            <th>Расположение</th>
            <th>Изображение</th>
            <th>Доступность</th>
            <th>Действия</th>
        </tr>
        </thead>

        <tbody>
        <c:forEach var="space" items="${spaces}">
            <tr>
                <td style="word-break: break-word;">${space.id}</td>
                <td style="word-break: break-word;">${space.name}</td>
                <td style="word-break: break-word;">${space.description}</td>
                <td>${space.price} руб./час</td>
                <td style="word-break: break-word;">${space.location}</td>
                <td>
                    <c:if test="${not empty space.image}">
                        <img src="${pageContext.request.contextPath}/${space.image}"
                             alt="${space.name}"
                             style="max-width: 150px; height: auto; display: block; margin: 0 auto;">
                    </c:if>
                </td>
                <td>${space.availability ? "Доступно" : "Недоступно"}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/admin/spaces" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="deleteSpace">
                        <input type="hidden" name="spaceId" value="${space.id}">
                        <button type="submit">Удалить</button>
                    </form>
                    <a href="${pageContext.request.contextPath}/admin?action=editSpace&spaceId=${space.id}">Редактировать</a>
                </td>
            </tr>
        </c:forEach>
        </tbody>
    </table>

    <h2>Добавить новое пространство</h2>

    <form action="${pageContext.request.contextPath}/admin" method="post" enctype="multipart/form-data"
          style="display: grid; gap: 15px; max-width: 600px;">
        <input type="hidden" name="action" value="addSpace">

        <div style="display: grid; gap: 8px;">
            <label>Фото пространства:</label>
            <input type="file" name="image" accept="image/jpeg, image/png" style="padding: 6px;">
        </div>

        <div style="display: grid; gap: 8px;">
            <label>Название:</label>
            <input type="text" name="name" required
                   style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
        </div>

        <div style="display: grid; gap: 8px;">
            <label>Описание:</label>
            <textarea name="description" required
                      style="padding: 8px; border: 1px solid #ddd; border-radius: 4px; height: 100px;"></textarea>
        </div>

        <div style="display: grid; gap: 8px;">
            <label>Цена:</label>
            <input type="number" name="price" step="0.01" required
                   style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
        </div>

        <div style="display: grid; gap: 8px;">
            <label>Расположение:</label>
            <input type="text" name="location" required
                   style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
        </div>

        <div style="display: grid; gap: 8px;">
            <label>Доступность:</label>
            <select name="availability" required
                    style="padding: 8px; border: 1px solid #ddd; border-radius: 4px;">
                <option value="true">Доступно</option>
                <option value="false">Недоступно</option>
            </select>
        </div>

        <div style="display: flex; gap: 12px; margin-top: 20px;">
            <button type="submit"
                    style="background: #28a745; color: white;
                           padding: 10px 20px; border: none; border-radius: 4px;">
                Добавить
            </button>
            <a href="${pageContext.request.contextPath}/user?action=profile"
               style="background: #6c757d; color: white;
                      padding: 10px 20px; text-decoration: none; border-radius: 4px;">
                Отмена
            </a>
        </div>
    </form>
</main>

<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>