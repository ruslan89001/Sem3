<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Редактирование бронирования #${booking.id}</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Административная панель</h1>
    <nav>
        <ul>
            <li><a href="${pageContext.request.contextPath}/user?action=profile">Главная</a></li>
            <li><a href="${pageContext.request.contextPath}/admin?action=viewBookings">Бронирования</a></li>
        </ul>
    </nav>
</header>

<main>
    <div class="centered-content">
        <h2>Редактирование бронирования #${booking.id}</h2>

        <form action="${pageContext.request.contextPath}/admin" method="post" class="form-container">
            <input type="hidden" name="action" value="updateBooking">
            <input type="hidden" name="bookingId" value="${booking.id}">

            <div class="form-group">
                <label>Пользователь:</label>
                <select name="userId" class="form-control" required>
                    <c:forEach items="${allUsers}" var="user">
                        <option value="${user.id}" ${user.id == booking.userId ? 'selected' : ''}>
                                ${user.username} (ID: ${user.id})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Пространство:</label>
                <select name="spaceId" class="form-control" required>
                    <c:forEach items="${allSpaces}" var="space">
                        <option value="${space.id}" ${space.id == booking.spaceId ? 'selected' : ''}>
                                ${space.name} (ID: ${space.id})
                        </option>
                    </c:forEach>
                </select>
            </div>

            <div class="form-group">
                <label>Начало:</label>
                <input type="datetime-local" name="startTime"
                       value="<fmt:formatDate value="${booking.startTime}" pattern="yyyy-MM-dd'T'HH:mm"/>"
                       class="form-control" required>
            </div>

            <div class="form-group">
                <label>Окончание:</label>
                <input type="datetime-local" name="endTime"
                       value="<fmt:formatDate value="${booking.endTime}" pattern="yyyy-MM-dd'T'HH:mm"/>"
                       class="form-control" required>
            </div>

            <div class="form-group">
                <label>Статус:</label>
                <select name="status" class="form-control" required>
                    <option value="pending" ${booking.status == 'pending' ? 'selected' : ''}>В ожидании</option>
                    <option value="confirmed" ${booking.status == 'confirmed' ? 'selected' : ''}>Подтверждено</option>
                    <option value="cancelled" ${booking.status == 'cancelled' ? 'selected' : ''}>Отменено</option>
                </select>
            </div>

            <div class="button-group">
                <button type="submit" class="btn-primary">Сохранить изменения</button>
                <a href="${pageContext.request.contextPath}/admin?action=viewBookings" class="btn-secondary">Отмена</a>
            </div>
        </form>
    </div>
</main>

<footer>
    <p>&copy; 2025 Coworking Booking. Все права защищены</p>
</footer>
</body>
</html>