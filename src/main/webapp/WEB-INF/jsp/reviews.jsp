<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Отзывы</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Отзывы</h1>
</header>
<main>
    <h2>Оставить отзыв</h2>
    <form action="${pageContext.request.contextPath}/reviews" method="post">
        <div>
            <label for="spaceId">Пространство:</label>
            <select id="spaceId" name="spaceId" required>
                <c:forEach var="space" items="${spaces}">
                    <option value="${space.id}">${space.name}</option>
                </c:forEach>
            </select>
        </div>
        <div>
            <label for="rating">Рейтинг:</label>
            <select id="rating" name="rating" required>
                <option value="5">5 - Отлично</option>
                <option value="4">4 - Хорошо</option>
                <option value="3">3 - Удовлетворительно</option>
                <option value="2">2 - Плохо</option>
                <option value="1">1 - Ужасно</option>
            </select>
        </div>
        <div>
            <label for="comment">Комментарий:</label>
            <textarea id="comment" name="comment" required></textarea>
        </div>
        <button type="submit">Отправить</button>
    </form>
    <h2>Отзывы пользователей</h2>
    <table>
        <thead>
        <tr>
            <th>Пространство</th>
            <th>Рейтинг</th>
            <th>Комментарий</th>
            <th>Дата</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="entry" items="${enrichedReviews}">
            <tr>
                <td>${entry.spaceName}</td>
                <td>${entry.review.rating}</td>
                <td>${entry.review.comment}</td>
                <td>${entry.review.createdAt}</td>
            </tr>
        </c:forEach>
        </tbody>
    </table>
</main>
<footer>
    <p>&copy; 2025 Coworking Booking.</p>
</footer>
</body>
</html>
