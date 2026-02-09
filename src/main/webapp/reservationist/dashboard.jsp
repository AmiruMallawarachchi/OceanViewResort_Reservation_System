<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Reservationist Dashboard</title>
  <link rel="stylesheet" href="<%= request.getContextPath() %>/assets/css/style.css?v=20260206" />
</head>
<body>
  <%@ include file="/WEB-INF/partials/nav.jspf" %>
  <main class="container">
    <div class="page-header">
      <div>
        <h1 class="page-header__title">Reservationist Dashboard</h1>
        <p class="muted">Manage bookings and guest services.</p>
      </div>
      <span class="badge">Shift Active</span>
    </div>

    <div class="grid grid--3">
      <div class="card">
        <h3>Add Reservation</h3>
        <p>Create a new reservation for a guest.</p>
        <a class="btn btn--primary" href="<%= request.getContextPath() %>/reservations">Create</a>
      </div>
      <div class="card">
        <h3>Search Reservation</h3>
        <p>Find reservation details quickly.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/reservations">Search</a>
      </div>
      <div class="card">
        <h3>Billing</h3>
        <p>Calculate and print guest bills.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/bills">Generate Bill</a>
      </div>
      <div class="card">
        <h3>Guests</h3>
        <p>Register and manage guest records.</p>
        <a class="btn btn--outline" href="<%= request.getContextPath() %>/guests">Manage Guests</a>
      </div>
    </div>

    <div class="section panel">
      <h2>Today Overview</h2>
      <div class="kpi">
        <div class="kpi__item"><strong>12</strong><p>Check-ins</p></div>
        <div class="kpi__item"><strong>9</strong><p>Check-outs</p></div>
        <div class="kpi__item"><strong>5</strong><p>Pending Reservations</p></div>
      </div>
    </div>
  </main>
  <%@ include file="/WEB-INF/partials/footer.jspf" %>
</body>
</html>

