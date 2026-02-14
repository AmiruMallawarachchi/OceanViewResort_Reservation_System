<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="com.oceanview.resort.dto.ReservationDTO" %>
<% String ctx = request.getContextPath(); %>
<%
  ReservationDTO r = (ReservationDTO) request.getAttribute("viewReservation");
  boolean isPrint = Boolean.TRUE.equals(request.getAttribute("isPrint"));
  if (r == null) {
    response.sendRedirect(ctx + "/reservations");
    return;
  }
  String resStatus = r.getStatus() == null ? "" : r.getStatus();
%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reservation <%= r.getReservationNo() %> | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
  <% if (isPrint) { %>
  <style media="print">
    .no-print { display: none !important; }
    body { padding: 16px; }
  </style>
  <% } %>
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header page-header--row">
      <div>
        <h1 class="page-header__title">Reservation details</h1>
        <p class="muted">Reservation #<%= r.getReservationNo() %></p>
      </div>
      <div class="table-actions no-print">
        <a class="btn btn--outline" href="<%= ctx %>/reservations">Back to list</a>
        <a class="btn btn--outline" href="<%= ctx %>/reservations?viewId=<%= r.getId() %>&print=1" target="_blank">Print</a>
        <a class="btn btn--outline" href="<%= ctx %>/reservations?editId=<%= r.getId() %>">Edit</a>
        <% if ("PENDING".equals(resStatus) || "CONFIRMED".equals(resStatus)) { %>
        <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
          <input type="hidden" name="action" value="checkIn" />
          <input type="hidden" name="id" value="<%= r.getId() %>" />
          <button class="btn btn--primary" type="submit">Check-in</button>
        </form>
        <% } %>
        <% if ("CHECKED_IN".equals(resStatus)) { %>
        <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
          <input type="hidden" name="action" value="checkOut" />
          <input type="hidden" name="id" value="<%= r.getId() %>" />
          <button class="btn btn--outline" type="submit">Check-out</button>
        </form>
        <a class="btn btn--outline" href="<%= ctx %>/bills?reservationId=<%= r.getId() %>">Bill</a>
        <% } %>
        <% if (!"CANCELLED".equals(resStatus) && !"CHECKED_OUT".equals(resStatus)) { %>
        <form method="post" action="<%= ctx %>/reservations" onsubmit="return confirm('Cancel this reservation?');" style="display:inline;">
          <input type="hidden" name="action" value="cancel" />
          <input type="hidden" name="id" value="<%= r.getId() %>" />
          <button class="btn btn--outline" type="submit">Cancel</button>
        </form>
        <% } %>
      </div>
    </div>

    <div class="panel">
      <h2>Reservation</h2>
      <table class="table table--compact">
        <tr><th>Reservation No</th><td><%= r.getReservationNo() %></td></tr>
        <tr><th>Check-in</th><td><%= r.getCheckInDate() == null ? "—" : r.getCheckInDate() %></td></tr>
        <tr><th>Check-out</th><td><%= r.getCheckOutDate() == null ? "—" : r.getCheckOutDate() %></td></tr>
        <tr><th>Status</th><td><span class="badge badge--status <%= resStatus.toLowerCase().replace('_', '-') %>"><%= resStatus %></span></td></tr>
      </table>
    </div>

    <div class="panel">
      <h2>Guest</h2>
      <table class="table table--compact">
        <tr><th>Name</th><td><%= r.getGuestName() == null ? "—" : r.getGuestName() %></td></tr>
        <tr><th>Contact (phone)</th><td><%= r.getGuestPhone() == null || r.getGuestPhone().isBlank() ? "—" : r.getGuestPhone() %></td></tr>
        <tr><th>Email</th><td><%= r.getGuestEmail() == null || r.getGuestEmail().isBlank() ? "—" : r.getGuestEmail() %></td></tr>
        <tr><th>Address</th><td><%= r.getGuestAddress() == null || r.getGuestAddress().isBlank() ? "—" : r.getGuestAddress() %></td></tr>
        <tr><th>Guest type</th><td><%= r.getGuestType() == null ? "—" : r.getGuestType() %></td></tr>
      </table>
    </div>

    <div class="panel">
      <h2>Room</h2>
      <table class="table table--compact">
        <tr><th>Room number</th><td><%= r.getRoomNumber() == null ? "—" : r.getRoomNumber() %></td></tr>
        <tr><th>Room type</th><td><%= r.getRoomTypeName() == null ? "—" : r.getRoomTypeName() %></td></tr>
      </table>
    </div>
  </main>
  <% if (isPrint) { %>
  <script>window.onload = function () { window.print(); };</script>
  <% } %>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
