<%@ page contentType="text/html; charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="ru">
<head>
  <meta charset="UTF-8">
  <title>Доступные пространства</title>
  <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/styles.css">
</head>
<body>
<header>
  <h1>Доступные пространства</h1>
</header>

<main style="max-width: 1200px; margin: 20px auto; padding: 25px;">
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
        <td style="word-break: break-word;">${space.name}</td>
        <td style="word-break: break-word;">${space.description}</td>
        <td>${space.price} руб./час</td>
        <td style="word-break: break-word;">${space.location}</td>
        <td>
          <c:if test="${not empty space.image}">
            <img src="${pageContext.request.contextPath}/${space.image}"
                 alt="${space.name}"
                 style="max-width: 200px; height: auto; display: block; margin: 0 auto;">
          </c:if>
        </td>
        <td>${space.availability ? "Доступно" : "Недоступно"}</td>
        <td>
          <c:if test="${space.availability}">
            <form action="${pageContext.request.contextPath}/bookings" method="post"
                  style="display: grid; gap: 8px;">
              <input type="hidden" name="spaceId" value="${space.id}">

              <div>
                <label>Начало:
                  <input type="datetime-local"
                         name="startTime"
                         style="width: 100%; padding: 6px;"
                         required>
                </label>
              </div>

              <div>
                <label>Окончание:
                  <input type="datetime-local"
                         name="endTime"
                         style="width: 100%; padding: 6px;"
                         required>
                </label>
              </div>

              <button type="submit"
                      style="background: #333; color: white;
                                               padding: 8px; border-radius: 4px;">
                Забронировать
              </button>
            </form>
          </c:if>
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