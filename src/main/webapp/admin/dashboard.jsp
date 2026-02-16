<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.DashboardSummaryDTO,com.oceanview.resort.dto.UserDTO" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Admin Dashboard</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Admin Dashboard</h1>
        <p class="muted">Overview of your resort operations.</p>
      </div>
      <div class="datetime-pill">
        <span class="datetime-pill__label">Today</span>
        <span data-current-datetime>--</span>
      </div>
    </div>

    <%
      DashboardSummaryDTO summary = (DashboardSummaryDTO) request.getAttribute("dashboardSummary");
      List<UserDTO> latestUsers = (List<UserDTO>) request.getAttribute("latestUsers");
    %>

    <section style="margin-bottom: 24px;">
      <div class="stat-cards" style="grid-template-columns: repeat(auto-fit, minmax(220px, 1fr));">
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l18 0"/><path d="M3 15l18 0"/><path d="M5 9V6a2 2 0 012-2h10a2 2 0 012 2v3"/><path d="M5 15v3a2 2 0 002 2h10a2 2 0 002-2v-3"/></svg>
          </div>
          <div class="kpi-card__label">Total Rooms</div>
          <div class="kpi-card__value"><%= summary == null ? "-" : summary.getTotalRooms() %></div>
          <div class="kpi-card__meta"><%= summary == null ? "" : (summary.getMaintenanceRooms() + " under maintenance") %></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><polyline points="22 12 18 12 15 21 9 3 6 12 2 12"/></svg>
          </div>
          <div class="kpi-card__label">Occupancy</div>
          <div class="kpi-card__value"><%= summary == null ? "-" : String.format("%.0f%%", summary.getOccupancyRate()) %></div>
          <div class="kpi-card__meta kpi-card__meta--positive"><%= summary == null || summary.getOccupancyChangePercent() == null ? "" : ("↑ " + String.format("%.0f", summary.getOccupancyChangePercent()) + "% from last week") %></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><rect x="3" y="4" width="18" height="18" rx="2" ry="2"/><line x1="16" y1="2" x2="16" y2="6"/><line x1="8" y1="2" x2="8" y2="6"/><line x1="3" y1="10" x2="21" y2="10"/></svg>
          </div>
          <div class="kpi-card__label">Today's Check-ins</div>
          <div class="kpi-card__value"><%= summary == null ? "-" : summary.getTodaysCheckins() %></div>
          <div class="kpi-card__meta"><%= summary == null ? "" : (summary.getPendingCheckins() + " pending") %></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M17 21v-2a4 4 0 0 0-4-4H5a4 4 0 0 0-4 4v2"/><circle cx="9" cy="7" r="4"/><path d="M23 21v-2a4 4 0 0 0-3-3.87"/><path d="M16 3.13a4 4 0 0 1 0 7.75"/></svg>
          </div>
          <div class="kpi-card__label">Active Staff</div>
          <div class="kpi-card__value"><%= summary == null ? "-" : summary.getActiveStaff() %></div>
          <div class="kpi-card__meta"><%= summary == null ? "" : (summary.getStaffOnLeave() + " on leave") %></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M14 2H6a2 2 0 0 0-2 2v16a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V8z"/><polyline points="14 2 14 8 20 8"/><line x1="16" y1="13" x2="8" y2="13"/><line x1="16" y1="17" x2="8" y2="17"/><polyline points="10 9 9 9 8 9"/></svg>
          </div>
          <div class="kpi-card__label">Reservations</div>
          <div class="kpi-card__value"><%= summary == null ? "-" : summary.getTotalReservations() %></div>
          <div class="kpi-card__meta"><%= summary == null ? "" : (summary.getCancelledReservations() + " cancelled · " + String.format("%.1f", summary.getCancellationRate()) + "% rate") %></div>
        </div>
        <div class="kpi-card">
          <div class="kpi-card__icon">
            <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
          </div>
          <div class="kpi-card__label">Revenue</div>
          <div class="kpi-card__value"><%= summary == null || summary.getTotalRevenue() == null ? "-" : summary.getTotalRevenue().toString() %></div>
          <div class="kpi-card__meta"><%= summary == null || summary.getTotalDiscounts() == null ? "" : ("Discounts: " + summary.getTotalDiscounts().toString()) %></div>
        </div>
      </div>
    </section>

    <section class="panel">
      <h2>Latest Users</h2>
      <table class="table table--striped">
        <thead>
          <tr>
            <th>NAME</th>
            <th>EMAIL</th>
            <th>ROLE</th>
            <th>STATUS</th>
          </tr>
        </thead>
        <tbody>
          <%
            if (latestUsers != null && !latestUsers.isEmpty()) {
              for (UserDTO u : latestUsers) {
          %>
          <tr>
            <td><%= u.getFullName() != null ? u.getFullName() : u.getUsername() %></td>
            <td><%= u.getEmail() != null ? u.getEmail() : "—" %></td>
            <td><span class="pill pill--role"><%= u.getRole() != null ? u.getRole() : "—" %></span></td>
            <td><span class="pill <%= u.isActive() ? "pill--active" : "pill--inactive" %>"><%= u.isActive() ? "Active" : "Inactive" %></span></td>
          </tr>
          <%
              }
            } else {
          %>
          <tr>
            <td colspan="4">No users found.</td>
          </tr>
          <% } %>
        </tbody>
      </table>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
