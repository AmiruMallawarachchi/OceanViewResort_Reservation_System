<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Administrator Dashboard</h1>
        <p class="muted">Key performance indicators and management shortcuts.</p>
      </div>
      <span class="badge">Admin Access</span>
    </div>

    <%
      com.oceanview.resort.dto.DashboardSummaryDTO summary =
              (com.oceanview.resort.dto.DashboardSummaryDTO) request.getAttribute("dashboardSummary");
    %>

    <div class="grid grid--3" style="margin-bottom: 2rem;">
      <div class="card card--metric">
        <h3>Occupancy</h3>
        <p class="metric__value">
          <%= summary == null ? "-" : String.format("%.1f%%", summary.getOccupancyRate()) %>
        </p>
        <p class="metric__hint">
          <%= summary == null ? "" : (summary.getOccupiedRooms() + " of " + summary.getTotalRooms() + " rooms occupied") %>
        </p>
      </div>
      <div class="card card--metric">
        <h3>Reservations</h3>
        <p class="metric__value">
          <%= summary == null ? "-" : summary.getTotalReservations() %>
        </p>
        <p class="metric__hint">
          <%= summary == null ? "" : ("Cancelled: " + summary.getCancelledReservations()
                  + " (" + String.format(\"%.1f%%\", summary.getCancellationRate()) + ")") %>
        </p>
      </div>
      <div class="card card--metric">
        <h3>Revenue (All Time)</h3>
        <p class="metric__value">
          <%= summary == null || summary.getTotalRevenue() == null ? "-" : summary.getTotalRevenue() %>
        </p>
        <p class="metric__hint">
          <%= summary == null || summary.getTotalDiscounts() == null
                  ? ""
                  : ("Discounts given: " + summary.getTotalDiscounts()) %>
        </p>
      </div>
    </div>

    <div class="grid grid--3">
      <div class="card">
        <h3>Manage Users</h3>
        <p>Create and manage staff accounts.</p>
        <a class="btn btn--primary" href="<%= request.getContextPath() %>/users">Open</a>
      </div>
      <div class="card">
        <h3>Rooms & Types</h3>
        <p>Update room inventory and room types.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/rooms">Manage</a>
      </div>
      <div class="card">
        <h3>Reports</h3>
        <p>Generate occupancy and revenue reports.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/reports">View</a>
      </div>
      <div class="card">
        <h3>Discounts</h3>
        <p>Create seasonal and guest discounts.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/discounts">Manage</a>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

