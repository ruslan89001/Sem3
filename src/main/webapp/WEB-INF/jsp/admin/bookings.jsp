<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <title>Административная панель бронирований</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
  <h1>Административная панель бронирований</h1>
</header>
<main>
  <h2>Список бронирований</h2>
  <table>
    <thead>
    <tr>
      <th>Пользователь</th>
      <th>Пространство</th>
      <th>Начало</th>
      <th>Окончание</th>
      <th>Статус</th>
      <th>Действия</th>
    </tr>
    </thead>
    <tbody>
    <c:choose>
      <c:when test="${empty enrichedBookings}">
        <tr>
          <td colspan="6">Нет доступных бронирований</td>
        </tr>
      </c:when>
      <c:otherwise>
        <c:forEach var="entry" items="${enrichedBookings}">
          <tr>
            <td>${entry.user.username}</td>
            <td>${entry.space.name}</td>
            <td><fmt:formatDate value="${entry.booking.startTime}" pattern="dd.MM.yyyy HH:mm"/></td>
            <td><fmt:formatDate value="${entry.booking.endTime}" pattern="dd.MM.yyyy HH:mm"/></td>
            <td>${entry.booking.status}</td>
            <td>
              <form action="${pageContext.request.contextPath}/admin" method="post" style="display:inline;">
                <input type="hidden" name="action" value="deleteBooking">
                <input type="hidden" name="bookingId" value="${entry.booking.id}">
                <button type="submit">Удалить</button>
              </form>
              <a href="${pageContext.request.contextPath}/admin?action=editBooking&bookingId=${entry.booking.id}">Редактировать</a>
            </td>
          </tr>
        </c:forEach>
      </c:otherwise>
    </c:choose>
    </tbody>
  </table>

  <h2>Добавить новое бронирование</h2>
  <form action="${pageContext.request.contextPath}/admin" method="post">
    <input type="hidden" name="action" value="addBooking">
    <div>
      <label for="user_id">Пользователь:</label>
      <select id="user_id" name="user_id" required>
        <c:forEach var="user" items="${allUsers}">
          <option value="${user.id}">${user.username} (ID: ${user.id})</option>
        </c:forEach>
      </select>
    </div>
    <div>
      <label for="space_id">Пространство:</label>
      <select id="space_id" name="space_id" required>
        <c:forEach var="space" items="${allSpaces}">
          <option value="${space.id}">${space.name} (ID: ${space.id})</option>
        </c:forEach>
      </select>
    </div>
    <div>
      <label for="start_time">Время начала:</label>
      <input type="datetime-local" id="start_time" name="start_time" required>
    </div>
    <div>
      <label for="end_time">Время окончания:</label>
      <input type="datetime-local" id="end_time" name="end_time" required>
    </div>
    <div>
      <label for="status">Статус:</label>
      <select id="status" name="status" required>
        <option value="pending">В ожидании</option>
        <option value="confirmed">Подтверждено</option>
        <option value="cancelled">Отменено</option>
      </select>
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