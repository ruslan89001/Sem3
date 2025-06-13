<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Мои бронирования</title>
    <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
    <h1>Мои бронирования</h1>
</header>
<main>
    <table>
        <thead>
        <tr>
            <th>ID</th>
            <th>Пространство</th>
            <th>Время начала</th>
            <th>Время окончания</th>
            <th>Статус</th>
            <th>Действия</th>
        </tr>
        </thead>
        <tbody>
        <c:forEach var="booking" items="${bookings}">
            <tr>
                <td>${booking.id}</td>
                <td>${booking.spaceId}</td>
                <td>${booking.startTime}</td>
                <td>${booking.endTime}</td>
                <td>${booking.status}</td>
                <td>
                    <form action="${pageContext.request.contextPath}/booking" method="post" style="display:inline;">
                        <input type="hidden" name="action" value="deleteBooking">
                        <input type="hidden" name="bookingId" value="${booking.id}">
                        <button type="submit">Отменить</button>
                    </form>
                </td>
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
