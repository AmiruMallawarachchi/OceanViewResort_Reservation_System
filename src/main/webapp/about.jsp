<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>About | OceanView Resort</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">About OceanView Resort</h1>
        <p class="muted">A modern reservation platform built for front-desk teams.</p>
      </div>
      <span class="badge">Since 2026</span>
    </div>

    <section class="section">
      <div class="grid grid--3">
        <div class="card">
          <h3>Our Mission</h3>
          <p>Deliver a reliable, fast, and user-friendly reservation experience for every guest and every staff member.</p>
        </div>
        <div class="card">
          <h3>Designed for Staff</h3>
          <p>Simple workflows for check-ins, room management, and billing make daily operations effortless.</p>
        </div>
        <div class="card">
          <h3>Data You Can Trust</h3>
          <p>Accurate availability checks and clear reporting reduce errors and increase confidence.</p>
        </div>
      </div>
    </section>

    <section class="section">
      <h2>System Highlights</h2>
      <div class="grid grid--3">
        <div class="card">
          <h3>Availability Engine</h3>
          <p>Prevents double bookings and keeps room inventory synchronized.</p>
        </div>
        <div class="card">
          <h3>Role-Based Access</h3>
          <p>Admins and reservationists see only what they need for faster decision making.</p>
        </div>
        <div class="card">
          <h3>Billing Clarity</h3>
          <p>Transparent charges and nightly rates for clean handoff to guests.</p>
        </div>
      </div>
    </section>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

