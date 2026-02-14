<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ page import="java.util.List,com.oceanview.resort.dto.ReservationDTO" %>
<% String ctx = request.getContextPath(); %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Front Desk | OceanView Resort</title>
  <link rel="stylesheet" href="<%= ctx %>/assets/css/style.css?v=20260213" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header page-header--row">
      <div>
        <h1 class="page-header__title">Front Desk</h1>
        <p class="muted">Quick actions & guest management.</p>
      </div>
      <div class="datetime-pill">
        <span class="datetime-pill__label">Today</span>
        <span data-current-datetime>--</span>
      </div>
    </div>

    <%
      com.oceanview.resort.dto.DashboardSummaryDTO summary = (com.oceanview.resort.dto.DashboardSummaryDTO) request.getAttribute("dashboardSummary");
    %>
    <div class="stat-cards" style="grid-template-columns: repeat(auto-fit, minmax(180px, 1fr)); margin-bottom: 24px;">
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
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><path d="M3 9l18 0"/><path d="M3 15l18 0"/><path d="M5 9V6a2 2 0 012-2h10a2 2 0 012 2v3"/><path d="M5 15v3a2 2 0 002 2h10a2 2 0 002-2v-3"/></svg>
        </div>
        <div class="kpi-card__label">Available Rooms</div>
        <div class="kpi-card__value"><%= summary == null ? "-" : summary.getAvailableRooms() %></div>
        <div class="kpi-card__meta"><%= summary == null ? "" : (summary.getTotalRooms() + " total") %></div>
      </div>
    </div>

    <div class="action-cards">
      <a href="<%= ctx %>/reservations" class="action-card action-card--teal">
        <div class="action-card__icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="5" x2="12" y2="19"/><line x1="5" y1="12" x2="19" y2="12"/></svg>
        </div>
        <span class="action-card__label">Create Reservation</span>
      </a>
      <a href="<%= ctx %>/reservations" class="action-card action-card--teal-alt">
        <div class="action-card__icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M5 12h14"/><path d="M12 5l7 7-7 7"/></svg>
        </div>
        <span class="action-card__label">Check-In</span>
      </a>
      <a href="<%= ctx %>/bills" class="action-card action-card--neutral">
        <div class="action-card__icon">
          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><line x1="12" y1="1" x2="12" y2="23"/><path d="M17 5H9.5a3.5 3.5 0 0 0 0 7h5a3.5 3.5 0 0 1 0 7H6"/></svg>
        </div>
        <span class="action-card__label">Billing</span>
      </a>
    </div>

    <form class="search-bar" method="get" action="<%= ctx %>/reservations">
      <div class="search-bar__wrap">
        <svg class="search-bar__icon" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2"><circle cx="11" cy="11" r="8"/><path d="m21 21-4.35-4.35"/></svg>
        <input type="search" name="q" placeholder="Search guests or room number..." />
      </div>
      <button type="submit" class="btn btn--primary">Search</button>
    </form>

    <section class="panel">
      <h2>Guests & Arrivals</h2>
      <%
        List<ReservationDTO> guestsAndArrivals = (List<ReservationDTO>) request.getAttribute("guestsAndArrivals");
        String today = java.time.LocalDate.now().toString();
      %>
      <table class="table table--striped">
        <thead>
          <tr>
            <th>GUEST</th>
            <th>ROOM</th>
            <th>TYPE</th>
            <th>DATE</th>
            <th>STATUS</th>
            <th>ACTIONS</th>
          </tr>
        </thead>
        <tbody>
          <%
            if (guestsAndArrivals != null && !guestsAndArrivals.isEmpty()) {
              for (ReservationDTO r : guestsAndArrivals) {
                String type = "—";
                if (r.getCheckOutDate() != null && r.getCheckOutDate().startsWith(today) && "CHECKED_IN".equalsIgnoreCase(r.getStatus())) type = "Departure";
                else if ("CHECKED_IN".equalsIgnoreCase(r.getStatus())) type = "In-House";
                else if (r.getCheckInDate() != null && r.getCheckInDate().startsWith(today)) type = "Arrival";
                String typeClass = "pill--arrival";
                if ("In-House".equals(type)) typeClass = "pill--in-house";
                if ("Departure".equals(type)) typeClass = "pill--departure";
                String statusDisplay = r.getStatus() != null ? r.getStatus().replace("_", "-") : "—";
                String statusClass = "pill--pending";
                if ("CONFIRMED".equalsIgnoreCase(r.getStatus())) statusClass = "pill--confirmed";
                else if ("CHECKED_IN".equalsIgnoreCase(r.getStatus())) statusClass = "pill--checked-in";
                else if ("CHECKED_OUT".equalsIgnoreCase(r.getStatus())) statusClass = "pill--checking-out";
                String dateDisplay = r.getCheckInDate() != null ? r.getCheckInDate() : (r.getCheckOutDate() != null ? r.getCheckOutDate() : "—");
                String rStatus = r.getStatus() != null ? r.getStatus() : "";
          %>
          <tr>
            <td><%= r.getGuestName() != null ? r.getGuestName() : "—" %></td>
            <td><%= r.getRoomNumber() != null ? r.getRoomNumber() : "—" %></td>
            <td><span class="pill <%= typeClass %>"><%= type %></span></td>
            <td><%= dateDisplay %></td>
            <td><span class="pill <%= statusClass %>"><%= statusDisplay %></span></td>
            <td>
              <div class="table-actions">
                <% if ("PENDING".equals(rStatus) || "CONFIRMED".equals(rStatus)) { %>
                <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
                  <input type="hidden" name="action" value="checkIn" />
                  <input type="hidden" name="id" value="<%= r.getId() %>" />
                  <button class="btn btn--primary btn--sm" type="submit">Check-in</button>
                </form>
                <% } %>
                <% if ("CHECKED_IN".equals(rStatus)) { %>
                <form method="post" action="<%= ctx %>/reservations" style="display:inline;">
                  <input type="hidden" name="action" value="checkOut" />
                  <input type="hidden" name="id" value="<%= r.getId() %>" />
                  <button class="btn btn--outline btn--sm" type="submit">Check-out</button>
                </form>
                <a class="btn btn--outline btn--sm" href="<%= ctx %>/bills">Bill</a>
                <% } %>
              </div>
            </td>
          </tr>
          <%
              }
            } else {
          %>
          <tr>
            <td colspan="6">No guests or arrivals for today.</td>
          </tr>
          <% } %>
        </tbody>
      </table>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
