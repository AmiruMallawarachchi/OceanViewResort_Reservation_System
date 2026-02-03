<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>OceanView Resort | Home</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <section class="hero">
      <span class="hero__pill">Smart Reservations • Reliable Billing</span>
      <h1 class="hero__title">Welcome to OceanView Resort</h1>
      <p class="hero__subtitle">A complete reservation system for managing rooms, guests, and billing with speed, clarity, and zero booking conflicts.</p>
      <div class="grid" style="grid-template-columns: repeat(auto-fit, minmax(160px, 1fr)); gap: 12px;">
        <a class="btn btn--primary" href="<%= request.getContextPath() %>/login.jsp">Login</a>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/help.jsp">View Help</a>
      </div>
    </section>

    <section class="section">
      <h2>Why OceanView Reservation System?</h2>
      <div class="grid grid--3">
        <div class="card">
          <h3>Fast Check-Ins</h3>
          <p>Search reservations instantly and handle check-ins within seconds.</p>
        </div>
        <div class="card">
          <h3>Accurate Billing</h3>
          <p>Automated bill calculation based on nights and room rates.</p>
        </div>
        <div class="card">
          <h3>Clear Reports</h3>
          <p>Generate occupancy and revenue summaries for management.</p>
        </div>
      </div>
    </section>

    <section class="section">
      <h2>Quick Snapshot</h2>
      <div class="kpi">
        <div class="kpi__item">
          <div class="badge">98% Accuracy</div>
          <h3>Booking Integrity</h3>
          <p>No double bookings with automated availability checks.</p>
        </div>
        <div class="kpi__item">
          <div class="badge">24/7</div>
          <h3>System Availability</h3>
          <p>Always-on access for staff at any shift.</p>
        </div>
        <div class="kpi__item">
          <div class="badge">100+</div>
          <h3>Rooms Managed</h3>
          <p>Scalable structure for large resorts.</p>
        </div>
      </div>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>
